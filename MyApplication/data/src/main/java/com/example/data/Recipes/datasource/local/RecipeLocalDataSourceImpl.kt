package com.example.data.Recipes.datasource.local

import com.example.data.Recipes.model.*
import com.example.domain.Recipes.model.ContentBlock
import com.example.domain.Recipes.model.TextArea
import com.example.domain.Recipes.model.TextStyle
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDateTime

class RecipeLocalDataSourceImpl : RecipeLocalDataSource {

    private val categories = listOf(
        CategoryEntity(1, "Первые блюда", "Супы, борщи, бульоны"),
        CategoryEntity(2, "Вторые блюда", "Мясные, рыбные, овощные блюда"),
        CategoryEntity(3, "Салаты", "Холодные и горячие закуски"),
        CategoryEntity(4, "Десерты", "Сладкая выпечка и десерты"),
        CategoryEntity(5, "Напитки", "Коктейли, компоты, смузи"),
        CategoryEntity(6, "Выпечка", "Пироги, булочки, хлеб")
    )

    private val groceries = listOf(
        GroceryEntity(1, "Мясо", "Говядина, свинина, курица"),
        GroceryEntity(2, "Овощи", "Свежие, замороженные"),
        GroceryEntity(3, "Крупы и макароны", "Рис, гречка, спагетти"),
        GroceryEntity(4, "Молочные продукты", "Молоко, сыр, сливки"),
        GroceryEntity(5, "Яйца и специи", "Яйца, соль, перец, сахар"),
        GroceryEntity(6, "Масла", "Растительное, оливковое, сливочное"),
        GroceryEntity(7, "Соусы", "Томатная паста, соусы"),
        GroceryEntity(8, "Сладости", "Шоколад, орехи"),
        GroceryEntity(9, "Фрукты", "Яблоки, вишня"),
        GroceryEntity(10, "Зелень", "Укроп, петрушка, базилик"),
        GroceryEntity(11, "Хлеб", "Белый хлеб"),
        GroceryEntity(12, "Бекон", "Бекон")
    )

    private val groceryItems = listOf(
        GroceryItemEntity(1, 1, "Говядина", "г"),
        GroceryItemEntity(2, 1, "Свинина", "г"),
        GroceryItemEntity(3, 1, "Курица", "г"),
        GroceryItemEntity(4, 2, "Свекла", "г"),
        GroceryItemEntity(5, 2, "Картофель", "г"),
        GroceryItemEntity(6, 2, "Морковь", "г"),
        GroceryItemEntity(7, 2, "Капуста", "г"),
        GroceryItemEntity(8, 2, "Лук", "г"),
        GroceryItemEntity(9, 2, "Чеснок", "зубчик"),
        GroceryItemEntity(10, 2, "Тыква", "г"),
        GroceryItemEntity(11, 2, "Помидоры", "г"),
        GroceryItemEntity(12, 3, "Гречка", "г"),
        GroceryItemEntity(13, 3, "Рис", "г"),
        GroceryItemEntity(14, 3, "Спагетти", "г"),
        GroceryItemEntity(15, 3, "Мука", "г"),
        GroceryItemEntity(16, 4, "Молоко", "мл"),
        GroceryItemEntity(17, 4, "Сливки", "мл"),
        GroceryItemEntity(18, 4, "Сметана", "г"),
        GroceryItemEntity(19, 4, "Масло сливочное", "г"),
        GroceryItemEntity(20, 4, "Сыр пармезан", "г"),
        GroceryItemEntity(21, 4, "Моцарелла", "г"),
        GroceryItemEntity(22, 4, "Мороженое", "г"),
        GroceryItemEntity(23, 5, "Яйца", "шт"),
        GroceryItemEntity(24, 5, "Соль", "ч.л."),
        GroceryItemEntity(25, 5, "Перец", "ч.л."),
        GroceryItemEntity(26, 5, "Сахар", "г"),
        GroceryItemEntity(27, 5, "Корица", "ч.л."),
        GroceryItemEntity(28, 5, "Ванильный экстракт", "ч.л."),
        GroceryItemEntity(29, 5, "Разрыхлитель", "г"),
        GroceryItemEntity(30, 5, "Дрожжи", "г"),
        GroceryItemEntity(31, 5, "Какао-порошок", "ст.л."),
        GroceryItemEntity(32, 6, "Масло растительное", "ст.л."),
        GroceryItemEntity(33, 6, "Масло оливковое", "ст.л."),
        GroceryItemEntity(34, 7, "Томатная паста", "ст.л."),
        GroceryItemEntity(35, 7, "Томатный соус", "г"),
        GroceryItemEntity(36, 7, "Соус Цезарь", "мл"),
        GroceryItemEntity(37, 8, "Темный шоколад", "г"),
        GroceryItemEntity(38, 8, "Грецкие орехи", "г"),
        GroceryItemEntity(39, 8, "Сахарная пудра", "ст.л."),
        GroceryItemEntity(40, 8, "Шоколадная крошка", "г"),
        GroceryItemEntity(41, 9, "Яблоки", "г"),
        GroceryItemEntity(42, 9, "Вишня", "шт"),
        GroceryItemEntity(43, 10, "Укроп", "пучок"),
        GroceryItemEntity(44, 10, "Петрушка", "пучок"),
        GroceryItemEntity(45, 10, "Базилик", "пучок"),
        GroceryItemEntity(46, 11, "Белый хлеб", "г"),
        GroceryItemEntity(47, 12, "Бекон", "г")
    )

    private val recipes = listOf(
        RecipeEntity(1, "Борщ с пампушками", "Традиционный украинский борщ с насыщенным вкусом и ароматными пампушками с чесноком. Готовится на говяжьем бульоне с добавлением свеклы, капусты и моркови. Подается со сметаной и зеленью.", "Елена Иванова", "https://i.ibb.co/0j76530T/article-ae55d88d-13f6-40a3-a4f6-077cfee27e1b-large.jpg", 90, LocalDateTime.now().minusDays(15), LocalDateTime.now().minusDays(10), 0),
        RecipeEntity(2, "Цезарь с курицей", "Классический салат Цезарь с хрустящими гренками, сочной курицей и пармезаном. Заправляется оригинальным соусом на основе яиц, оливкового масла и анчоусов.", "Алексей Петров", "https://i.ibb.co/Q7SN4SNc/2325597-amp.jpg", 25, LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(5), 0),
        RecipeEntity(3, "Паста Карбонара", "Итальянская паста с беконом и сливочным соусом. Традиционный рецепт с использованием гуанчиале (вяленые свиные щеки), яиц и сыра пекорино романо.", "Мария Смирнова", "https://i.ibb.co/23XKwRQf/slivochnaya-quotkarbonaraquot-550154.jpg", 30, LocalDateTime.now().minusDays(25), LocalDateTime.now().minusDays(7), 0),
        RecipeEntity(4, "Шоколадный брауни", "Влажный шоколадный пирог с орехами и насыщенным вкусом. Идеальный десерт для любителей шоколада. Подается с шариком ванильного мороженого.", "Дмитрий Соколов", "https://i.ibb.co/j1HjbR8/f3155d0e00724da353ac4876dcc27cf6.jpg", 45, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(2), 0),
        RecipeEntity(5, "Овощной суп-пюре", "Легкий и полезный суп из запеченной тыквы с добавлением моркови и картофеля. Нежная кремовая текстура и пряные нотки имбиря и куркумы.", "Анна Кузнецова", "https://i.ibb.co/7txhpLMB/soup-veget.jpg", 40, LocalDateTime.now().minusDays(30), LocalDateTime.now().minusDays(12), 0),
        RecipeEntity(6, "Гречка по-купечески", "Сытная гречневая каша с курицей, грибами и луком. Традиционное русское блюдо, которое готовится в горшочках с добавлением томатной пасты и специй.", "Иван Петров", "https://i.ibb.co/DgTD6Kqc/maxresdefault.jpg", 50, LocalDateTime.now().minusDays(18), LocalDateTime.now().minusDays(3), 0),
        RecipeEntity(7, "Пицца Маргарита", "Тонкое хрустящее тесто, сочная моцарелла и ароматный соус из помидоров. Классическая итальянская пицца с минимальным набором ингредиентов, но максимальным вкусом.", "Павел Орлов", "https://i.ibb.co/QjNcX5pp/pitstsa-margarita-istoriya-sostav-retsept-4.jpg", 35, LocalDateTime.now().minusDays(22), LocalDateTime.now().minusDays(8), 0),
        RecipeEntity(8, "Салат Оливье", "Классический новогодний салат с вареной колбасой, огурцами, яйцами и горошком. Заправляется майонезом и украшается свежей зеленью.", "Татьяна Морозова", "https://i.ibb.co/7xXV6S2j/maxresdefault-1.jpg", 60, LocalDateTime.now().minusDays(40), LocalDateTime.now().minusDays(15), 0),
        RecipeEntity(9, "Молочный коктейль", "Освежающий молочный коктейль с вишней и шоколадной крошкой. Идеальный напиток для жаркого дня или в качестве десерта.", "Сергей Волков", "https://i.ibb.co/G4YwssmM/maxresdefault-2.jpg", 10, LocalDateTime.now().minusDays(12), LocalDateTime.now().minusDays(1), 0),
        RecipeEntity(10, "Яблочный пирог", "Нежный и ароматный пирог с яблоками и корицей. Простой домашний десерт, который готовится из доступных ингредиентов.", "Ольга Новикова", "https://i.ibb.co/8n7p62Sp/maxresdefault-3.jpg", 70, LocalDateTime.now().minusDays(28), LocalDateTime.now().minusDays(9), 0)
    )

    private val recipeCategories = listOf(
        RecipeCategoryCrossEntity(1, 1), RecipeCategoryCrossEntity(2, 3),
        RecipeCategoryCrossEntity(3, 2), RecipeCategoryCrossEntity(4, 4),
        RecipeCategoryCrossEntity(5, 1), RecipeCategoryCrossEntity(6, 2),
        RecipeCategoryCrossEntity(7, 2), RecipeCategoryCrossEntity(7, 6),
        RecipeCategoryCrossEntity(8, 3), RecipeCategoryCrossEntity(9, 5),
        RecipeCategoryCrossEntity(10, 4), RecipeCategoryCrossEntity(10, 6)
    )

    private val recipeGroceryItemsCrossRef = listOf(
        RecipeGroceryItemCrossEntity(1, 1, 500.0, "г"),
        RecipeGroceryItemCrossEntity(1, 4, 300.0, "г"),
        RecipeGroceryItemCrossEntity(1, 5, 400.0, "г"),
        RecipeGroceryItemCrossEntity(1, 6, 200.0, "г"),
        RecipeGroceryItemCrossEntity(1, 7, 300.0, "г"),
        RecipeGroceryItemCrossEntity(1, 8, 150.0, "г"),
        RecipeGroceryItemCrossEntity(1, 9, 3.0, "зубчик"),
        RecipeGroceryItemCrossEntity(1, 34, 2.0, "ст.л."),
        RecipeGroceryItemCrossEntity(1, 43, 1.0, "пучок"),
        RecipeGroceryItemCrossEntity(1, 18, 100.0, "г"),
        RecipeGroceryItemCrossEntity(1, 24, 10.0, "г"),
        RecipeGroceryItemCrossEntity(1, 25, 5.0, "г"),
        RecipeGroceryItemCrossEntity(2, 3, 400.0, "г"),
        RecipeGroceryItemCrossEntity(2, 46, 100.0, "г"),
        RecipeGroceryItemCrossEntity(2, 11, 150.0, "г"),
        RecipeGroceryItemCrossEntity(2, 20, 50.0, "г"),
        RecipeGroceryItemCrossEntity(2, 23, 2.0, "шт"),
        RecipeGroceryItemCrossEntity(2, 36, 100.0, "мл"),
        RecipeGroceryItemCrossEntity(2, 9, 1.0, "зубчик"),
        RecipeGroceryItemCrossEntity(2, 33, 2.0, "ст.л."),
        RecipeGroceryItemCrossEntity(2, 24, 5.0, "г"),
        RecipeGroceryItemCrossEntity(2, 25, 5.0, "г"),
        RecipeGroceryItemCrossEntity(3, 14, 400.0, "г"),
        RecipeGroceryItemCrossEntity(3, 47, 200.0, "г"),
        RecipeGroceryItemCrossEntity(3, 23, 4.0, "шт"),
        RecipeGroceryItemCrossEntity(3, 20, 100.0, "г"),
        RecipeGroceryItemCrossEntity(3, 9, 2.0, "зубчик"),
        RecipeGroceryItemCrossEntity(3, 17, 100.0, "мл"),
        RecipeGroceryItemCrossEntity(3, 33, 2.0, "ст.л."),
        RecipeGroceryItemCrossEntity(3, 44, 1.0, "пучок"),
        RecipeGroceryItemCrossEntity(3, 24, 5.0, "г"),
        RecipeGroceryItemCrossEntity(3, 25, 5.0, "г"),
        RecipeGroceryItemCrossEntity(4, 37, 200.0, "г"),
        RecipeGroceryItemCrossEntity(4, 19, 150.0, "г"),
        RecipeGroceryItemCrossEntity(4, 26, 200.0, "г"),
        RecipeGroceryItemCrossEntity(4, 23, 3.0, "шт"),
        RecipeGroceryItemCrossEntity(4, 15, 100.0, "г"),
        RecipeGroceryItemCrossEntity(4, 31, 2.0, "ст.л."),
        RecipeGroceryItemCrossEntity(4, 38, 100.0, "г"),
        RecipeGroceryItemCrossEntity(4, 28, 1.0, "ч.л."),
        RecipeGroceryItemCrossEntity(5, 10, 500.0, "г"),
        RecipeGroceryItemCrossEntity(5, 5, 300.0, "г"),
        RecipeGroceryItemCrossEntity(5, 6, 200.0, "г"),
        RecipeGroceryItemCrossEntity(5, 8, 150.0, "г"),
        RecipeGroceryItemCrossEntity(5, 17, 200.0, "мл"),
        RecipeGroceryItemCrossEntity(5, 9, 2.0, "зубчик"),
        RecipeGroceryItemCrossEntity(5, 33, 2.0, "ст.л."),
        RecipeGroceryItemCrossEntity(5, 24, 8.0, "г"),
        RecipeGroceryItemCrossEntity(5, 25, 3.0, "г"),
        RecipeGroceryItemCrossEntity(6, 12, 300.0, "г"),
        RecipeGroceryItemCrossEntity(6, 2, 400.0, "г"),
        RecipeGroceryItemCrossEntity(6, 8, 200.0, "г"),
        RecipeGroceryItemCrossEntity(6, 6, 200.0, "г"),
        RecipeGroceryItemCrossEntity(6, 9, 3.0, "зубчик"),
        RecipeGroceryItemCrossEntity(6, 34, 1.0, "ст.л."),
        RecipeGroceryItemCrossEntity(6, 32, 3.0, "ст.л."),
        RecipeGroceryItemCrossEntity(6, 44, 1.0, "пучок"),
        RecipeGroceryItemCrossEntity(6, 24, 10.0, "г"),
        RecipeGroceryItemCrossEntity(7, 15, 500.0, "г"),
        RecipeGroceryItemCrossEntity(7, 30, 7.0, "г"),
        RecipeGroceryItemCrossEntity(7, 16, 300.0, "мл"),
        RecipeGroceryItemCrossEntity(7, 33, 3.0, "ст.л."),
        RecipeGroceryItemCrossEntity(7, 24, 10.0, "г"),
        RecipeGroceryItemCrossEntity(7, 26, 5.0, "г"),
        RecipeGroceryItemCrossEntity(7, 35, 150.0, "г"),
        RecipeGroceryItemCrossEntity(7, 21, 250.0, "г"),
        RecipeGroceryItemCrossEntity(7, 45, 1.0, "пучок"),
        RecipeGroceryItemCrossEntity(8, 5, 400.0, "г"),
        RecipeGroceryItemCrossEntity(8, 6, 200.0, "г"),
        RecipeGroceryItemCrossEntity(8, 23, 4.0, "шт"),
        RecipeGroceryItemCrossEntity(8, 2, 300.0, "г"),
        RecipeGroceryItemCrossEntity(8, 43, 1.0, "пучок"),
        RecipeGroceryItemCrossEntity(8, 24, 10.0, "г"),
        RecipeGroceryItemCrossEntity(8, 25, 5.0, "г"),
        RecipeGroceryItemCrossEntity(9, 16, 300.0, "мл"),
        RecipeGroceryItemCrossEntity(9, 22, 200.0, "г"),
        RecipeGroceryItemCrossEntity(9, 40, 20.0, "г"),
        RecipeGroceryItemCrossEntity(9, 42, 3.0, "шт"),
        RecipeGroceryItemCrossEntity(10, 15, 250.0, "г"),
        RecipeGroceryItemCrossEntity(10, 19, 150.0, "г"),
        RecipeGroceryItemCrossEntity(10, 26, 200.0, "г"),
        RecipeGroceryItemCrossEntity(10, 23, 3.0, "шт"),
        RecipeGroceryItemCrossEntity(10, 41, 500.0, "г"),
        RecipeGroceryItemCrossEntity(10, 29, 10.0, "г"),
        RecipeGroceryItemCrossEntity(10, 27, 1.0, "ч.л."),
        RecipeGroceryItemCrossEntity(10, 39, 2.0, "ст.л.")
    )

    private val recipeContents = listOf(
        RecipeContentEntity(1, listOf(
            ContentBlock.Paragraph("Приготовление настоящего украинского борща", TextStyle.Bold, 24, TextArea.Center),
            ContentBlock.Paragraph("Шаг 1: Приготовление бульона", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Промойте говядину под холодной водой, залейте 3 литрами воды и поставьте на сильный огонь. После закипания снимите пену, убавьте огонь и варите 1.5-2 часа.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 2: Подготовка овощей", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Свеклу натрите на крупной терке, морковь и лук мелко нарежьте. Картофель нарежьте кубиками, капусту нашинкуйте.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 3: Зажарка", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("На сковороде разогрейте масло, обжарьте лук до золотистого цвета, добавьте морковь и свеклу. Тушите 10 минут, добавьте томатную пасту.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 4: Сборка борща", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("В готовый бульон добавьте картофель и капусту, варите 10 минут. Затем добавьте зажарку, лавровый лист, соль и перец. Варите еще 10 минут.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 5: Настой", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Выключите огонь и дайте борщу настояться под крышкой 30 минут.", TextStyle.Normal, 18, TextArea.Left)
        ), "Подавайте со свежей сметаной и зеленью. Пампушки с чесноком отлично дополнят вкус."),

        RecipeContentEntity(2, listOf(
            ContentBlock.Paragraph("Классический Цезарь с курицей", TextStyle.Bold, 24, TextArea.Center),
            ContentBlock.Paragraph("Шаг 1: Приготовление соуса", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("В миске смешайте яйца, растертый чеснок, горчицу и оливковое масло. Постепенно добавляйте тертый пармезан и соус Цезарь, постоянно помешивая.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 2: Курица", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Куриное филе нарежьте полосками, обжарьте на оливковом масле до золотистой корочки. Посолите и поперчите.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 3: Гренки", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Белый хлеб нарежьте кубиками, обжарьте на сухой сковороде или в духовке до хруста.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 4: Сборка салата", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Листья салата порвите руками, добавьте помидоры черри, курицу и гренки. Заправьте соусом и посыпьте пармезаном.", TextStyle.Normal, 18, TextArea.Left)
        ), "Гренки добавляйте перед подачей, чтобы они оставались хрустящими."),

        RecipeContentEntity(3, listOf(
            ContentBlock.Paragraph("Паста Карбонара", TextStyle.Bold, 24, TextArea.Center),
            ContentBlock.Paragraph("Шаг 1: Приготовление пасты", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("В большой кастрюле вскипятите воду, добавьте соль и варите спагетти до состояния al dente (на 1-2 минуты меньше, чем указано на упаковке).", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 2: Подготовка бекона", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Бекон нарежьте небольшими полосками и обжарьте на сковороде без масла до хрустящей текстуры.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 3: Соус", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("В миске смешайте яйца, тертый пармезан, измельченный чеснок и сливки. Тщательно перемешайте.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 4: Смешивание", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Слейте воду из готовой пасты, добавьте бекон и яично-сырную смесь. Быстро перемешайте, чтобы яйца свернулись от тепла.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 5: Подача", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Подавайте сразу, посыпав свежемолотым перцем и украсив веточкой базилика.", TextStyle.Normal, 18, TextArea.Left)
        ), "Не перегревайте соус, чтобы яйца не свернулись крупными хлопьями."),

        RecipeContentEntity(4, listOf(
            ContentBlock.Paragraph("Шоколадный брауни", TextStyle.Bold, 24, TextArea.Center),
            ContentBlock.Paragraph("Шаг 1: Растопить шоколад", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("На водяной бане растопите темный шоколад вместе со сливочным маслом, постоянно помешивая.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 2: Взбить яйца", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Отдельно взбейте яйца с сахаром до светлой пены.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 3: Смешивание", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Смешайте растопленный шоколад с яичной смесью, добавьте муку, какао и ванильный экстракт.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 4: Выпечка", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Выложите тесто в форму, застеленную пергаментом, и выпекайте 25 минут при 180 градусах.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 5: Охлаждение", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Дайте брауни полностью остыть, затем нарежьте квадратиками.", TextStyle.Normal, 18, TextArea.Left)
        ), "Подавайте с шариком ванильного мороженого и посыпьте рублеными орехами."),

        RecipeContentEntity(5, listOf(
            ContentBlock.Paragraph("Овощной суп-пюре", TextStyle.Bold, 24, TextArea.Center),
            ContentBlock.Paragraph("Шаг 1: Запекание тыквы", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Тыкву нарежьте кубиками, сбрызните оливковым маслом и запекайте в духовке 20 минут при 200 градусах.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 2: Обжарка овощей", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("В кастрюле обжарьте лук, морковь и картофель. Добавьте чеснок.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 3: Варка", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Добавьте запеченную тыкву, залейте сливками и водой. Варите 15 минут.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 4: Пюрирование", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Погружным блендером измельчите суп до однородной консистенции.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 5: Подача", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Подавайте с тыквенными семечками и свежей зеленью.", TextStyle.Normal, 18, TextArea.Left)
        ), "Украсьте семечками и добавьте ложку сметаны для нежности."),

        RecipeContentEntity(6, listOf(
            ContentBlock.Paragraph("Гречка по-купечески", TextStyle.Bold, 24, TextArea.Center),
            ContentBlock.Paragraph("Шаг 1: Подготовка мяса", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Курицу нарежьте кубиками, обжарьте до золотистой корочки.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 2: Обжарка лука и моркови", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Лук нарежьте полукольцами, морковь натрите. Обжарьте до мягкости.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 3: Тушение", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Добавьте гречку, томатную пасту, залейте водой. Тушите 20 минут.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 4: Завершение", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Посолите, поперчите, добавьте зелень. Дайте настояться 10 минут.", TextStyle.Normal, 18, TextArea.Left)
        ), "Подавайте горячим, украсив свежей зеленью."),

        RecipeContentEntity(7, listOf(
            ContentBlock.Paragraph("Пицца Маргарита", TextStyle.Bold, 24, TextArea.Center),
            ContentBlock.Paragraph("Шаг 1: Приготовление теста", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("В теплой воде растворите дрожжи и сахар. Добавьте муку, соль и оливковое масло. Замесите тесто и оставьте на 1 час.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 2: Формирование", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Раскатайте тесто в тонкий круг, смажьте томатным соусом.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 3: Начинка", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Выложите кусочки моцареллы, посыпьте сушеным базиликом и орегано.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 4: Выпечка", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Выпекайте 15 минут при 220 градусах до золотистой корочки.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 5: Подача", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Перед подачей украсьте свежими листьями базилика.", TextStyle.Normal, 18, TextArea.Left)
        ), "Для более насыщенного вкуса добавьте свежие помидоры черри."),

        RecipeContentEntity(8, listOf(
            ContentBlock.Paragraph("Салат Оливье", TextStyle.Bold, 24, TextArea.Center),
            ContentBlock.Paragraph("Шаг 1: Варка овощей", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Отварите картофель, морковь и яйца до готовности. Остудите и очистите.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 2: Нарезка", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Нарежьте картофель, морковь, яйца и колбасу мелкими кубиками.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 3: Смешивание", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Смешайте все ингредиенты в большой миске, добавьте консервированный горошек и мелко нарезанный укроп.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 4: Заправка", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Заправьте салат майонезом, посолите и поперчите по вкусу.", TextStyle.Normal, 18, TextArea.Left)
        ), "Перед подачей охладите салат в холодильнике 1-2 часа."),

        RecipeContentEntity(9, listOf(
            ContentBlock.Paragraph("Молочный коктейль", TextStyle.Bold, 24, TextArea.Center),
            ContentBlock.Paragraph("Шаг 1: Подготовка ингредиентов", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Молоко охладите, мороженое достаньте из морозилки за 5 минут до приготовления.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 2: Смешивание", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("В блендере смешайте молоко, мороженое и вишню.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 3: Взбивание", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Взбивайте на высокой скорости 1-2 минуты до появления пены.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 4: Подача", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Перелейте коктейль в высокий стакан, посыпьте шоколадной крошкой.", TextStyle.Normal, 18, TextArea.Left)
        ), "Украсьте коктейль вишенкой и трубочкой."),

        RecipeContentEntity(10, listOf(
            ContentBlock.Paragraph("Яблочный пирог", TextStyle.Bold, 24, TextArea.Center),
            ContentBlock.Paragraph("Шаг 1: Подготовка яблок", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Яблоки вымойте, очистите от кожуры и семян, нарежьте тонкими ломтиками.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 2: Приготовление теста", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Яйца взбейте с сахаром до пены. Добавьте растопленное масло, муку и разрыхлитель. Тщательно перемешайте.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 3: Сборка пирога", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Форму смажьте маслом, выложите половину теста, затем яблоки, посыпанные корицей, сверху залейте оставшимся тестом.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 4: Выпечка", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Выпекайте 40-45 минут при 180 градусах до золотистого цвета.", TextStyle.Normal, 18, TextArea.Left),
            ContentBlock.Paragraph("Шаг 5: Подача", TextStyle.Bold, 20, TextArea.Left),
            ContentBlock.Paragraph("Перед подачей посыпьте сахарной пудрой.", TextStyle.Normal, 18, TextArea.Left)
        ), "Пирог вкуснее всего в теплом виде с шариком мороженого.")
    )

    private val favorites = mutableListOf<FavoriteEntity>()
    private val likes = mutableListOf<LikeEntity>()

    private val mutex = Mutex()
    private var nextFavoriteId = 1
    private var nextLikeId = 1

    override suspend fun getRecipes(): List<RecipeEntity> = recipes
    override suspend fun getRecipeById(recipeId: Int): RecipeEntity? = recipes.find { it.id == recipeId }
    override suspend fun getCategories(): List<CategoryEntity> = categories
    override suspend fun getCategoryById(categoryId: Int): CategoryEntity? = categories.find { it.id == categoryId }
    override suspend fun getGroceries(): List<GroceryEntity> = groceries
    override suspend fun getGroceryById(groceryId: Int): GroceryEntity? = groceries.find { it.id == groceryId }
    override suspend fun getGroceryItems(): List<GroceryItemEntity> = groceryItems
    override suspend fun getGroceryItemById(groceryItemId: Int): GroceryItemEntity? = groceryItems
        .find { it.id == groceryItemId }
    override suspend fun getRecipeGroceryItemIds(recipeId: Int): List<Int> = recipeGroceryItemsCrossRef
        .filter { it.recipeId == recipeId }.map { it.groceryItemId }
    override suspend fun getRecipeGroceryItemsCrossRef(recipeId: Int): List<RecipeGroceryItemCrossEntity> = recipeGroceryItemsCrossRef
        .filter { it.recipeId == recipeId }
    override suspend fun getRecipeContent(recipeId: Int): RecipeContentEntity? = recipeContents.find { it.recipeId == recipeId }
    override suspend fun getAllRecipeGroceryItemCross(): List<RecipeGroceryItemCrossEntity> = recipeGroceryItemsCrossRef

    override suspend fun getRecipesByCategory(categoryId: Int): List<RecipeEntity> {
        val recipeIds = recipeCategories.filter { it.categoryId == categoryId }.map { it.recipeId }
        return recipes.filter { it.id in recipeIds }
    }

    override suspend fun getRecipesByGrocery(groceryId: Int): List<RecipeEntity> = emptyList()

    override suspend fun getRecipesByGroceryItems(groceryItemIds: List<Int>): List<RecipeEntity> {
        val recipeIds = recipeGroceryItemsCrossRef
            .filter { it.groceryItemId in groceryItemIds }
            .map { it.recipeId }
            .distinct()
        return recipes.filter { it.id in recipeIds }
    }

    override suspend fun getRecipesByExactGroceryItems(groceryItemIds: List<Int>): List<RecipeEntity> {
        return recipes.filter { recipe ->
            val recipeItemIds = recipeGroceryItemsCrossRef
                .filter { it.recipeId == recipe.id }
                .map { it.groceryItemId }
            recipeItemIds.all { it in groceryItemIds }
        }
    }

    override suspend fun searchRecipes(query: String): List<RecipeEntity> =
        recipes.filter { it.title.contains(query, ignoreCase = true) || it.description?.contains(query, ignoreCase = true) == true }

    override suspend fun getRecipeCategoryIds(recipeId: Int): List<Int> = recipeCategories.filter { it.recipeId == recipeId }.map { it.categoryId }
    override suspend fun getRecipeGroceryIds(recipeId: Int): List<Int> = emptyList()

    override suspend fun getFavorites(userId: Int): List<FavoriteEntity> = favorites.filter { it.userId == userId }
    override suspend fun addFavorite(userId: Int, recipeId: Int): FavoriteEntity = mutex.withLock {
        if (favorites.any { it.userId == userId && it.recipeId == recipeId }) throw IllegalStateException("Уже в Избранном")
        val favorite = FavoriteEntity(nextFavoriteId++, userId, recipeId)
        favorites.add(favorite)
        favorite
    }
    override suspend fun removeFavorite(userId: Int, recipeId: Int): Boolean = mutex.withLock { favorites.removeAll { it.userId == userId && it.recipeId == recipeId } }
    override suspend fun isFavorite(userId: Int, recipeId: Int): Boolean = favorites.any { it.userId == userId && it.recipeId == recipeId }

    override suspend fun getLikes(recipeId: Int): List<LikeEntity> = likes.filter { it.recipeId == recipeId }
    override suspend fun getUserLikes(userId: Int): List<LikeEntity> = likes.filter { it.userId == userId }
    override suspend fun addLike(userId: Int, recipeId: Int): LikeEntity = mutex.withLock {
        if (likes.any { it.userId == userId && it.recipeId == recipeId }) throw IllegalStateException("Уже лайкнуто")
        val like = LikeEntity(nextLikeId++, userId, recipeId)
        likes.add(like)
        val recipeIndex = recipes.indexOfFirst { it.id == recipeId }
        if (recipeIndex != -1) (recipes as MutableList)[recipeIndex] = recipes[recipeIndex].copy(likesCount = recipes[recipeIndex].likesCount + 1)
        like
    }
    override suspend fun removeLike(userId: Int, recipeId: Int): Boolean = mutex.withLock {
        val removed = likes.removeAll { it.userId == userId && it.recipeId == recipeId }
        if (removed) {
            val recipeIndex = recipes.indexOfFirst { it.id == recipeId }
            if (recipeIndex != -1) (recipes as MutableList)[recipeIndex] = recipes[recipeIndex].copy(likesCount = maxOf(0, recipes[recipeIndex].likesCount - 1))
        }
        removed
    }
    override suspend fun isLiked(userId: Int, recipeId: Int): Boolean = likes.any { it.userId == userId && it.recipeId == recipeId }
    override suspend fun getLikesCount(recipeId: Int): Int = likes.count { it.recipeId == recipeId }



    private val shoppingLists = mutableListOf<ShoppingListEntity>()
    private val shoppingListItems = mutableListOf<ShoppingListItemEntity>()
    private var nextListId = 1
    private var nextItemId = 1

    override suspend fun getShoppingLists(userId: Int): List<ShoppingListEntity> {
        return shoppingLists.filter { it.userId == userId }
    }

    override suspend fun getShoppingListById(listId: Int): ShoppingListEntity? {
        return shoppingLists.find { it.id == listId }
    }

    override suspend fun createShoppingList(userId: Int, name: String): ShoppingListEntity {
        val newList = ShoppingListEntity(
            id = nextListId++,
            userId = userId,
            name = name,
            createdAt = LocalDateTime.now(),
            isCompleted = false
        )
        shoppingLists.add(newList)
        return newList
    }

    override suspend fun updateShoppingListName(listId: Int, newName: String): ShoppingListEntity? {
        val index = shoppingLists.indexOfFirst { it.id == listId }
        if (index != -1) {
            val updated = shoppingLists[index].copy(name = newName)
            shoppingLists[index] = updated
            return updated
        }
        return null
    }

    override suspend fun deleteShoppingList(listId: Int): Boolean {
        val removed = shoppingLists.removeAll { it.id == listId }
        shoppingListItems.removeAll { it.shoppingListId == listId }
        return removed
    }

    override suspend fun getShoppingListItems(listId: Int): List<ShoppingListItemEntity> {
        return shoppingListItems.filter { it.shoppingListId == listId }
    }

    override suspend fun addShoppingListItem(listId: Int, description: String): ShoppingListItemEntity {
        val newItem = ShoppingListItemEntity(
            id = nextItemId++,
            shoppingListId = listId,
            description = description,
            isChecked = false
        )
        shoppingListItems.add(newItem)
        return newItem
    }

    override suspend fun updateShoppingListItem(itemId: Int, isChecked: Boolean): ShoppingListItemEntity? {
        val index = shoppingListItems.indexOfFirst { it.id == itemId }
        if (index != -1) {
            val updated = shoppingListItems[index].copy(isChecked = isChecked)
            shoppingListItems[index] = updated
            return updated
        }
        return null
    }

    override suspend fun deleteShoppingListItem(itemId: Int): Boolean {
        return shoppingListItems.removeAll { it.id == itemId }
    }

    override suspend fun clearCompletedItems(listId: Int): Boolean {
        val itemsToRemove = shoppingListItems.filter { it.shoppingListId == listId && it.isChecked }
        return shoppingListItems.removeAll(itemsToRemove.toSet())
    }

    override suspend fun mergeShoppingLists(targetListId: Int, sourceListIds: List<Int>): ShoppingListEntity? {
        val targetList = shoppingLists.find { it.id == targetListId } ?: return null

        for (sourceId in sourceListIds) {
            val sourceItems = shoppingListItems.filter { it.shoppingListId == sourceId }
            for (item in sourceItems) {
                val existingItem = shoppingListItems.find {
                    it.shoppingListId == targetListId &&
                            it.description.equals(item.description, ignoreCase = true)
                }

                if (existingItem != null) {
                    val newQuantity = (existingItem.quantity ?: 0.0) + (item.quantity ?: 0.0)
                    val index = shoppingListItems.indexOfFirst { it.id == existingItem.id }
                    if (index != -1) {
                        shoppingListItems[index] = existingItem.copy(quantity = newQuantity)
                    }
                } else {
                    val newItem = item.copy(
                        id = nextItemId++,
                        shoppingListId = targetListId
                    )
                    shoppingListItems.add(newItem)
                }
            }
            shoppingLists.removeAll { it.id == sourceId }
            shoppingListItems.removeAll { it.shoppingListId == sourceId }
        }

        return targetList
    }

    override suspend fun updateShoppingListItemDetails(
        itemId: Int,
        description: String,
        quantity: Double?,
        unit: String?
    ): ShoppingListItemEntity? {
        val index = shoppingListItems.indexOfFirst { it.id == itemId }
        if (index != -1) {
            val updated = shoppingListItems[index].copy(
                description = description,
                quantity = quantity,
                unit = unit
            )
            shoppingListItems[index] = updated
            return updated
        }
        return null
    }
}