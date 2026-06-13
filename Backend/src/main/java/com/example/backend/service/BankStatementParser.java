package com.example.backend.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BankStatementParser {

    public record ParsedTransaction(
            String name,
            BigDecimal amount,
            String type,
            String description,
            LocalDate date
    ) {}

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public List<ParsedTransaction> parse(String fileName, byte[] content) {
        String text = extractText(content);
        if (text == null || text.isBlank()) {
            return List.of();
        }

        BankType bankType = detectBank(text);
        return switch (bankType) {
            case ALFA -> parseAlfa(text);
            case TBANK -> parseTBank(text);
            case SBER -> parseSber(text);
            case UNKNOWN -> List.of();
        };
    }

    private String extractText(byte[] content) {
        try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(content))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        } catch (IOException e) {
            return null;
        }
    }

    private enum BankType { ALFA, TBANK, SBER, UNKNOWN }

    private BankType detectBank(String text) {
        if (text.contains("АЛЬФА-БАНК") || text.contains("Альфа-Банк")) {
            return BankType.ALFA;
        }
        if (text.contains("ТБАНК") || text.contains("TBANK.RU") || text.contains("АО «ТБанк»")) {
            return BankType.TBANK;
        }
        if (text.contains("СберБанк") || text.contains("Сбербанк") || text.contains("sberbank.ru")) {
            return BankType.SBER;
        }
        return BankType.UNKNOWN;
    }

    private List<ParsedTransaction> parseAlfa(String text) {
        List<ParsedTransaction> result = new ArrayList<>();
        String[] lines = text.split("\\n");

        Pattern startPattern = Pattern.compile(
                "^(\\d{2}\\.\\d{2}\\.\\d{4})\\s+(\\S+)\\s+(.+)$");
        Pattern amountEndPattern = Pattern.compile(
                "(-?[\\d\\s\u00A0]+,\\d{2})\\s*RUR\\s*$");
        Pattern amountLinePattern = Pattern.compile(
                "^\\s*(-?[\\d\\s\u00A0]+,\\d{2})\\s*RUR\\s*$");

        boolean inOperations = false;
        int i = 0;
        while (i < lines.length) {
            String line = lines[i];

            if (line.contains("Операции по счету")) {
                inOperations = true;
                i++;
                continue;
            }
            if (!inOperations) { i++; continue; }
            if (isAlfaSkipLine(line)) { i++; continue; }

            Matcher startM = startPattern.matcher(line.trim());
            if (startM.find()) {
                String dateStr = startM.group(1);
                String rest = startM.group(3);

                String descText = null;
                BigDecimal amount = null;

                Matcher endM = amountEndPattern.matcher(line);
                if (endM.find()) {
                    amount = new BigDecimal(cleanAmount(endM.group(1)));
                    int amtStart = endM.start();
                    String beforeAmt = line.substring(0, amtStart);
                    Matcher m2 = Pattern.compile("^\\d{2}\\.\\d{2}\\.\\d{4}\\s+\\S+\\s+(.*)$")
                            .matcher(beforeAmt.trim());
                    descText = m2.find() ? m2.group(1).trim() : rest;
                    i++;
                } else {
                    StringBuilder descBuilder = new StringBuilder(rest);
                    i++;
                    while (i < lines.length) {
                        String next = lines[i];
                        Matcher amtM = amountLinePattern.matcher(next);
                        if (amtM.find()) {
                            amount = new BigDecimal(cleanAmount(amtM.group(1)));
                            descText = descBuilder.toString().trim();
                            i++;
                            break;
                        }
                        if (startPattern.matcher(next.trim()).find()) break;
                        Matcher inlineAmt = amountEndPattern.matcher(next);
                        if (inlineAmt.find()) {
                            amount = new BigDecimal(cleanAmount(inlineAmt.group(1)));
                            String prefix = next.substring(0, inlineAmt.start()).trim();
                            if (!prefix.isEmpty()) descBuilder.append(" ").append(prefix);
                            descText = descBuilder.toString().trim();
                            i++;
                            break;
                        }
                        descBuilder.append(" ").append(next.trim());
                        i++;
                    }
                }

                if (amount != null && descText != null) {
                    String type = amount.compareTo(BigDecimal.ZERO) >= 0 ? "income" : "expense";
                    String name = truncate(descText.isEmpty() ? "Операция " + dateStr : descText, 100);
                    result.add(new ParsedTransaction(
                            name, amount.abs(), type, truncate(descText, 200),
                            LocalDate.parse(dateStr, DATE_FMT)));
                }
                continue;
            }
            i++;
        }
        return result;
    }

    private boolean isAlfaSkipLine(String line) {
        String l = line.trim();
        return l.contains("Страница") || l.contains("Уполномоченное лицо")
                || l.contains("(подпись") || l.contains("(Ф.И.О")
                || l.contains("АЛЬФА-БАНК")
                || l.equals("Дата проводки Код операции Описание Сумма")
                || l.equals("в валюте счета") || l.isEmpty();
    }

    private List<ParsedTransaction> parseTBank(String text) {
        List<ParsedTransaction> result = new ArrayList<>();
        String[] lines = text.split("\\n");

        Pattern dateP = Pattern.compile("^(\\d{2}\\.\\d{2}\\.\\d{4})$");
        Pattern timeP = Pattern.compile("^(\\d{2}:\\d{2})$");
        Pattern amountDescP = Pattern.compile(
                "^([+-]?[\\d\\s\u00A0]+\\.\\d{2})\\s*₽\\s+[+-]?[\\d\\s\u00A0]+\\.\\d{2}\\s*₽\\s+(.*)$");
        Pattern cardP = Pattern.compile("^(\\d{4}|—)$");

        int i = 0;
        while (i < lines.length) {
            String line = lines[i].trim();

            if (line.isEmpty() || isTBankSkipLine(line)) { i++; continue; }
            if (line.matches("^\\d{1,2}$") && !timeP.matcher(line).matches()) { i++; continue; }

            Matcher dm = dateP.matcher(line);
            if (dm.matches()) {
                String opDate = dm.group(1);
                i++;
                if (i >= lines.length) break;

                if (!timeP.matcher(lines[i].trim()).matches()) continue;
                i++;
                if (i >= lines.length) break;

                if (!dateP.matcher(lines[i].trim()).matches()) continue;
                i++;
                if (i >= lines.length) break;

                if (!timeP.matcher(lines[i].trim()).matches()) continue;
                i++;
                if (i >= lines.length) break;

                String amtLine = lines[i].trim();
                Matcher amtM = amountDescP.matcher(amtLine);
                if (!amtM.find()) continue;

                String amtStr = amtM.group(1).replace(" ", "").replace("\u00A0", "");
                BigDecimal amount = new BigDecimal(amtStr);
                String descStart = amtM.group(2).trim();
                i++;

                List<String> descParts = new ArrayList<>();
                if (!descStart.isEmpty()) descParts.add(descStart);

                while (i < lines.length) {
                    String testLine = lines[i].trim();
                    if (cardP.matcher(testLine).matches()) { i++; break; }
                    if (dateP.matcher(testLine).matches()) break;
                    if (testLine.isEmpty() || isTBankSkipLine(testLine)) { i++; continue; }
                    descParts.add(testLine);
                    i++;
                }

                String description = String.join(" ", descParts);
                String type = amount.compareTo(BigDecimal.ZERO) > 0 ? "income" : "expense";
                String name = truncate(description.isEmpty() ? "Операция " + opDate : description, 100);

                result.add(new ParsedTransaction(
                        name, amount.abs(), type, truncate(description, 200),
                        LocalDate.parse(opDate, DATE_FMT)));
                continue;
            }
            i++;
        }
        return result;
    }

    private boolean isTBankSkipLine(String line) {
        return line.contains("АКЦИОНЕРНОЕ") || line.contains("РОССИЯ, 127287")
                || line.contains("ТЕЛ.:") || line.contains("Справка о движении")
                || line.contains("Адрес места")
                || line.contains("О продукте") || line.contains("Дата заключения")
                || line.contains("Номер договора") || line.contains("Номер лицевого")
                || line.contains("Движение средств") || line.equals("Дата и время")
                || line.equals("операции") || line.equals("Дата")
                || line.equals("списания") || line.contains("Сумма в валюте")
                || line.contains("Сумма операции") || line.contains("в валюте карты")
                || line.equals("Описание") || line.equals("Номер")
                || line.equals("карты") || line.contains("АО «ТБанк»")
                || line.contains("БИК") || line.contains("универсальная лицензия")
                || line.contains("Исх. №") || line.contains("TBANK.RU")
                || line.contains("Итого за период") || line.contains("Обороты:")
                || line.contains("Поступления") || line.contains("Списания")
                || line.contains("Остаток на начало") || line.contains("Остаток на конец");
    }

    private List<ParsedTransaction> parseSber(String text) {
        List<ParsedTransaction> result = new ArrayList<>();
        String[] lines = text.split("\\n");

        Pattern line1P = Pattern.compile(
                "^(\\d{2}\\.\\d{2}\\.\\d{4})\\s+(\\d{2}:\\d{2})\\s+(.+?)\\s+(\\+?[\\d\\s\u00A0]+,\\d{2})\\s+[\\d\\s\u00A0]+,\\d{2}\\s*$");
        Pattern line2P = Pattern.compile(
                "^(\\d{2}\\.\\d{2}\\.\\d{4})\\s+(\\d{6})\\s+(.+)$");

        int i = 0;
        while (i < lines.length) {
            String line = lines[i];
            if (isSberSkipLine(line)) { i++; continue; }

            Matcher m1 = line1P.matcher(line.trim());
            if (m1.find()) {
                String dateStr = m1.group(1);
                String category = m1.group(3).trim();
                String rawAmt = m1.group(4).trim();
                boolean isIncome = rawAmt.startsWith("+");
                BigDecimal amount = new BigDecimal(cleanAmount(rawAmt.replace("+", "")));
                i++;

                StringBuilder descBuilder = new StringBuilder();
                if (i < lines.length) {
                    Matcher m2 = line2P.matcher(lines[i].trim());
                    if (m2.find()) {
                        descBuilder.append(m2.group(3));
                        i++;
                        while (i < lines.length) {
                            String next = lines[i].trim();
                            if (next.isEmpty() || isSberSkipLine(next)) { i++; continue; }
                            if (line1P.matcher(next).find()) break;
                            if (line2P.matcher(next).find()) break;
                            descBuilder.append(" ").append(next);
                            i++;
                        }
                    }
                }

                String description = descBuilder.toString().trim();
                String type = isIncome ? "income" : "expense";
                String name = truncate(description.isEmpty() ? category : description, 100);

                result.add(new ParsedTransaction(
                        name, amount, type, truncate(description, 200),
                        LocalDate.parse(dateStr, DATE_FMT)));
                continue;
            }
            i++;
        }
        return result;
    }

    private boolean isSberSkipLine(String line) {
        String l = line.trim();
        return l.isEmpty() || l.contains("Действителен") || l.contains("Для проверки")
                || l.contains("Зайдите") || l.contains("Нажмите")
                || l.contains("Получите") || l.contains("Предоставляя")
                || l.contains("www.sberbank") || l.contains("sberbank.ru")
                || l.contains("ул. Вавилова") || l.contains("Заказано")
                || l.contains("Выписка по платёжному") || l.contains("За период")
                || l.contains("Владелец") || l.contains("Номер счёта")
                || l.contains("Валюта") || l.contains("Российский рубль")
                || l.contains("Дата открытия") || l.contains("Дата закрытия")
                || l.contains("ИТОГО ПО ОПЕРАЦИЯМ") || l.contains("Остаток на")
                || l.startsWith("Пополнение") || l.startsWith("Списание")
                || l.contains("Расшифровка") || l.contains("ДАТА ОПЕРАЦИИ")
                || l.contains("Дата обработки") || l.contains("и код авторизации")
                || l.equals("КАТЕГОРИЯ") || l.contains("Описание операции")
                || l.contains("СУММА В ВАЛЮТЕ") || l.contains("Сумма в валюте")
                || l.contains("операции²") || l.contains("ОСТАТОК СРЕДСТВ")
                || l.contains("В валюте счёта") || l.contains("Страница")
                || l.contains("Продолжение на") || l.contains("Дата формирования")
                || l.contains("ПАО Сбербанк") || l.contains("Денежные средства")
                || l.contains("отображаются") || l.contains("Согласно статье")
                || l.contains("электронной подписи") || l.contains("правоотношениях")
                || l.contains("Скачать электронный") || l.contains("Проверить подпись")
                || l.startsWith("900 ")
                || l.matches("^[0-9a-fA-F]{32}$") || l.startsWith("с 02.07");
    }

    private static String cleanAmount(String s) {
        return s.replace(" ", "")
                .replace("\u00A0", "")
                .replace(",", ".");
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max);
    }
}