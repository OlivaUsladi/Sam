package com.example.myapplication.ai

import android.content.Context
import com.example.myapplication.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

data class DayMenu(
    val breakfast: String,
    val lunch: String,
    val dinner: String,
    val snack: String
)

data class WeeklyMenu(
    val weekNumber: Int,
    val days: List<DayMenu>,
    val shoppingItems: List<ShoppingItem>
)

data class ShoppingItem(
    val name: String,
    val quantity: Double?,
    val unit: String?
)


 //Авторизация: OAuth-токен по ключу из BuildConfig.GIGACHAT_AUTH_KEY (scope GIGACHAT_API_PERS),
 //затем запрос к chat/completions. HTTPS работает через сертификаты НУЦ Минцифры из assets/certs.

class GigaChatService(context: Context) {

    private val appContext = context.applicationContext

    private val httpClient: OkHttpClient by lazy { buildTrustingClient() }

    @Volatile private var cachedToken: String? = null
    @Volatile private var tokenExpiresAtMs: Long = 0L

    private companion object {
        const val MAX_WEEK_ATTEMPTS = 2
    }

    suspend fun generateMenu(
        budgetRemaining: BigDecimal,
        weeksCount: Int,
        preferences: String = ""
    ): List<WeeklyMenu> = withContext(Dispatchers.IO) {
        if (BuildConfig.GIGACHAT_AUTH_KEY.isBlank()) {
            throw IllegalStateException(
                "Не задан ключ GigaChat. Добавьте GIGACHAT_AUTH_KEY в local.properties."
            )
        }
        val token = obtainAccessToken()
        // Г
        val weeks = weeksCount.coerceAtLeast(1)
        val perWeekBudget = budgetRemaining.divide(BigDecimal(weeks), 2, RoundingMode.HALF_UP)
        (1..weeks).map { week ->
            generateWeekWithRetry(token, perWeekBudget, week, preferences)
        }
    }


     //Генерирует одну неделю с повторами: GigaChat иногда обрезает JSON по лимиту.
     //Если пришло меньше 7 полных дней, то пробуем ещё раз; в конце возвращаем лучший результат.

    private fun generateWeekWithRetry(
        token: String,
        weekBudget: BigDecimal,
        week: Int,
        preferences: String
    ): WeeklyMenu {
        var best: WeeklyMenu? = null
        var lastError: Exception? = null
        repeat(MAX_WEEK_ATTEMPTS) {
            try {
                val content = requestCompletion(token, buildWeekPrompt(weekBudget, week, preferences))
                val parsed = parseWeek(extractJson(content), week)
                if (parsed.days.size >= 7) return parsed
                if (parsed.days.size > (best?.days?.size ?: 0)) best = parsed
            } catch (e: Exception) {
                lastError = e
            }
        }
        return best ?: throw (lastError ?: Exception("GigaChat вернул пустой ответ для недели $week"))
    }

    private fun buildWeekPrompt(
        weekBudget: BigDecimal,
        weekNumber: Int,
        preferences: String
    ): String = """
        Составь меню на неделю №$weekNumber (ровно 7 дней: завтрак, обед, ужин, перекус на каждый день)
        и полный список покупок на эту неделю.

        Бюджет на неделю: $weekBudget рублей.
        Предпочтения: ${preferences.ifBlank { "нет" }}

        Рецепты простые, доступные и экономные.

        Ответ верни СТРОГО в JSON, без markdown и пояснений, один объект на одну неделю:
        {
          "weekNumber": $weekNumber,
          "days": [
            {"breakfast": "Овсянка с бананом", "lunch": "Суп с курицей", "dinner": "Гречка с котлетами", "snack": "Яблоко"}
          ],
          "shoppingItems": [
            {"name": "Овсяные хлопья", "quantity": 0.5, "unit": "кг"},
            {"name": "Бананы", "quantity": 4, "unit": "шт"}
          ]
        }

        Ровно 7 дней. Список покупок — полный для этой недели.
    """.trimIndent()

    private fun obtainAccessToken(): String {
        val now = System.currentTimeMillis()
        cachedToken?.let { if (now < tokenExpiresAtMs - 60_000L) return it }

        val body = FormBody.Builder()
            .add("scope", "GIGACHAT_API_PERS")
            .build()
        val request = Request.Builder()
            .url("https://ngw.devices.sberbank.ru:9443/api/v2/oauth")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Accept", "application/json")
            .addHeader("RqUID", UUID.randomUUID().toString())
            .addHeader("Authorization", "Basic ${BuildConfig.GIGACHAT_AUTH_KEY}")
            .post(body)
            .build()

        httpClient.newCall(request).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw Exception("Ошибка авторизации GigaChat (${response.code}): $responseBody")
            }
            val json = JSONObject(responseBody)
            val token = json.optString("access_token")
            if (token.isBlank()) throw Exception("GigaChat не вернул access_token")
            cachedToken = token
            tokenExpiresAtMs = json.optLong("expires_at", now + 30 * 60_000L)
            return token
        }
    }

    private fun requestCompletion(token: String, prompt: String): String {
        val payload = JSONObject().apply {
            put("model", "GigaChat")
            put("temperature", 0.7)
            put("max_tokens", 4096)
            put(
                "messages",
                JSONArray().put(
                    JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    }
                )
            )
        }
        val request = Request.Builder()
            .url("https://gigachat.devices.sberbank.ru/api/v1/chat/completions")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $token")
            .post(payload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        httpClient.newCall(request).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw Exception("Ошибка GigaChat (${response.code}): $responseBody")
            }
            val json = JSONObject(responseBody)
            val choices = json.optJSONArray("choices")
                ?: throw Exception("Пустой ответ от GigaChat")
            if (choices.length() == 0) throw Exception("Пустой ответ от GigaChat")
            return choices.getJSONObject(0).getJSONObject("message").getString("content")
        }
    }

    private fun extractJson(raw: String): String {
        val start = raw.indexOf('{')
        val end = raw.lastIndexOf('}')
        return if (start in 0 until end) raw.substring(start, end + 1) else raw
    }

     //Парсит ответ для одной недели. Сначала строгий JSON-парсер, и если ответ обрезан по
     //лимиту токенов - вытаскивает все полные объекты без падения.
    private fun parseWeek(json: String, fallbackWeek: Int): WeeklyMenu {
        runCatching { return strictParseWeek(json, fallbackWeek) }
        return salvageWeek(json, fallbackWeek)
    }

    private fun strictParseWeek(json: String, fallbackWeek: Int): WeeklyMenu {
        val root = JSONObject(json)
        val w = root.optJSONArray("weeks")?.let { arr ->
            if (arr.length() > 0) arr.getJSONObject(0) else null
        } ?: root
        val daysArr = w.getJSONArray("days")
        val days = (0 until daysArr.length()).map { d -> dayFrom(daysArr.getJSONObject(d)) }
        val itemsArr = w.optJSONArray("shoppingItems") ?: JSONArray()
        val items = (0 until itemsArr.length()).mapNotNull { j -> itemFrom(itemsArr.getJSONObject(j)) }
        return WeeklyMenu(weekNumber = w.optInt("weekNumber", fallbackWeek), days = days, shoppingItems = items)
    }

    // Разбор возможно обрезанного JSON: берём только полные {} из массивов days/shoppingItems.
    private fun salvageWeek(json: String, fallbackWeek: Int): WeeklyMenu {
        val days = topLevelObjects(arrayAfterKey(json, "days")).mapNotNull { obj ->
            runCatching { dayFrom(JSONObject(obj)) }.getOrNull()
        }
        val items = topLevelObjects(arrayAfterKey(json, "shoppingItems")).mapNotNull { obj ->
            runCatching { itemFrom(JSONObject(obj)) }.getOrNull()
        }
        val weekNumber = Regex("\"weekNumber\"\\s*:\\s*(\\d+)")
            .find(json)?.groupValues?.get(1)?.toIntOrNull() ?: fallbackWeek
        return WeeklyMenu(weekNumber = weekNumber, days = days, shoppingItems = items)
    }

    private fun dayFrom(day: JSONObject) = DayMenu(
        breakfast = day.optString("breakfast"),
        lunch = day.optString("lunch"),
        dinner = day.optString("dinner"),
        snack = day.optString("snack")
    )

    private fun itemFrom(item: JSONObject): ShoppingItem? {
        val name = item.optString("name")
        if (name.isBlank()) return null
        return ShoppingItem(
            name = name,
            quantity = if (item.has("quantity") && !item.isNull("quantity")) item.optDouble("quantity") else null,
            unit = if (item.has("unit") && !item.isNull("unit")) item.optString("unit") else null
        )
    }

    //Возвращает подстроку, начиная с '[' после указанного ключа (или "" если ключа нет).
    private fun arrayAfterKey(json: String, key: String): String {
        val keyIdx = json.indexOf("\"$key\"")
        if (keyIdx < 0) return ""
        val bracket = json.indexOf('[', keyIdx)
        return if (bracket < 0) "" else json.substring(bracket)
    }

    private fun topLevelObjects(arrayText: String): List<String> {
        if (arrayText.isEmpty()) return emptyList()
        val result = mutableListOf<String>()
        var depth = 0
        var start = -1
        var inString = false
        var escaped = false
        for (i in arrayText.indices) {
            val c = arrayText[i]
            if (inString) {
                when {
                    escaped -> escaped = false
                    c == '\\' -> escaped = true
                    c == '"' -> inString = false
                }
                continue
            }
            when (c) {
                '"' -> inString = true
                '{' -> { if (depth == 0) start = i; depth++ }
                '}' -> {
                    depth--
                    if (depth == 0 && start >= 0) {
                        result.add(arrayText.substring(start, i + 1))
                        start = -1
                    }
                }
                ']' -> if (depth == 0) return result
            }
        }
        return result
    }


    private fun buildTrustingClient(): OkHttpClient {
        val certFactory = CertificateFactory.getInstance("X.509")
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply { load(null, null) }
        listOf(
            "certs/russian_trusted_root_ca.pem",
            "certs/russian_trusted_sub_ca.pem"
        ).forEachIndexed { index, path ->
            appContext.assets.open(path).use { input ->
                val cert = certFactory.generateCertificate(input) as X509Certificate
                keyStore.setCertificateEntry("ru_mincifry_$index", cert)
            }
        }

        val customTmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
            init(keyStore)
        }
        val systemTmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
            init(null as KeyStore?)
        }
        val custom = customTmf.trustManagers.filterIsInstance<X509TrustManager>().first()
        val system = systemTmf.trustManagers.filterIsInstance<X509TrustManager>().first()
        val trustManager = CompositeTrustManager(listOf(system, custom))

        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, arrayOf(trustManager), null)
        }
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(150, TimeUnit.SECONDS)
            .build()
    }

    private class CompositeTrustManager(
        private val managers: List<X509TrustManager>
    ) : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            managers.forEach { runCatching { it.checkClientTrusted(chain, authType) }.onSuccess { return } }
            throw java.security.cert.CertificateException("Ни один TrustManager не доверяет клиентскому сертификату")
        }

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            var last: Exception? = null
            for (m in managers) {
                try {
                    m.checkServerTrusted(chain, authType)
                    return
                } catch (e: Exception) {
                    last = e
                }
            }
            throw last ?: java.security.cert.CertificateException("Сертификат сервера не доверен")
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> =
            managers.flatMap { it.acceptedIssuers.toList() }.toTypedArray()
    }
}
