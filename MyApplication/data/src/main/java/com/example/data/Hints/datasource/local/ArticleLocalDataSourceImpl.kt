package com.example.data.Hints.datasource.local

import com.example.data.Hints.model.*
import com.example.domain.Hints.model.ContentBlock
import com.example.domain.Hints.model.TextArea
import com.example.domain.Hints.model.TextStyle
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDateTime

class ArticleLocalDataSourceImpl : ArticleLocalDataSource {

    private val categories = listOf(
        CategoryEntity(1, "Продуктивность", "Статьи об увеличении ритма жизни"),
        CategoryEntity(2, "Здоровье", "Статьи о здоровом образе жизни"),
        CategoryEntity(3, "Привычки", "Статьи о формировании полезных привычек"),
        CategoryEntity(4, "Организация", "Статьи о документах"),
        CategoryEntity(5, "ЖКХ", "Статьи о коммунальных услугах")
    )

    private val imageUrls = listOf(
        "https://i.ibb.co/bjy899VJ/aerial-view-business-data-analysis-graph.jpg",
        "https://i.ibb.co/zTjB1k12/brunette-woman-sitting-desk-surrounded-with-gadgets-papers.jpg",
        "https://i.ibb.co/274TSKSf/close-up-person-meditating-home.jpg",
        "https://i.ibb.co/Ld1K3vgj/tea-book-relax.jpg",
        "https://i.ibb.co/gMNnL2Yp/ceramic-mug-with-coffee-silver-dollar-gum-leaves.jpg",
        "https://i.ibb.co/YF85HrRg/doctor-doing-their-work-pediatrics-office.jpg"
    )

    private val articles = listOf(
        ArticleEntity(
            id = 1,
            title = "Как управлять временем",
            categoryId = 1,
            mainWords = listOf("тайм-менеджмент", "планирование"),
            author = "Анна Смирнова",
            imageUrl = imageUrls[0],
            createdAt = LocalDateTime.now().minusDays(2),
            updatedAt = LocalDateTime.now().minusDays(1)
        ),
        ArticleEntity(
            id = 2,
            title = "Техники продуктивности",
            categoryId = 1,
            mainWords = listOf("pomodoro", "фокус"),
            author = "Иван Петров",
            imageUrl = imageUrls[1],
            createdAt = LocalDateTime.now().minusDays(5),
            updatedAt = LocalDateTime.now().minusDays(3)
        ),
        ArticleEntity(
            id = 3,
            title = "Медитация для начинающих",
            categoryId = 2,
            mainWords = listOf("mindfulness", "релаксация"),
            author = "Елена Козлова",
            imageUrl = imageUrls[2],
            createdAt = LocalDateTime.now().minusWeeks(1),
            updatedAt = LocalDateTime.now().minusDays(2)
        ),
        ArticleEntity(
            id = 4,
            title = "Здоровый сон и режим",
            categoryId = 2,
            mainWords = listOf("сон", "циркадные ритмы"),
            author = "Михаил Васильев",
            imageUrl = imageUrls[3],
            createdAt = LocalDateTime.now().minusDays(10),
            updatedAt = LocalDateTime.now().minusDays(8)
        ),
        ArticleEntity(
            id = 5,
            title = "Утренние ритуалы",
            categoryId = 3,
            mainWords = listOf("утро", "рутина"),
            author = "Ольга Новикова",
            imageUrl = imageUrls[4],
            createdAt = LocalDateTime.now().minusDays(3),
            updatedAt = LocalDateTime.now().minusDays(2)
        ),
        ArticleEntity(
            id = 6,
            title = "Как прикрепиться в поликлинике",
            categoryId = 4,
            mainWords = listOf("здоровье", "документы", "поликлиника", "больница"),
            author = "Александра Майснер",
            imageUrl = imageUrls[5],
            createdAt = LocalDateTime.now().minusDays(21),
            updatedAt = LocalDateTime.now().minusDays(13)
        )
    )

    private val articleContents = listOf(
        ArticleContentEntity(
            articleId = 1,
            blocks = listOf(
                ContentBlock.Paragraph(
                    text = "Как управлять временем",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Тайм-менеджмент — это искусство управлять временем. В современном мире, где каждый день наполнен задачами и обязательствами, умение правильно распределять свое время становится ключевым навыком успешного человека.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Основные принципы тайм-менеджмента:",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "1. Матрица Эйзенхауэра\nРазделяйте дела на важные и срочные. Это поможет сфокусироваться на действительно значимых задачах и не тратить время на второстепенные.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "2. Метод Помодоро\nРаботайте интервалами: 25 минут работы, 5 минут отдыха. После 4 циклов сделайте длинный перерыв 15-30 минут.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "3. Правило 2 минут\nЕсли дело занимает меньше 2 минут — сделайте его сразу. Не откладывайте мелкие задачи на потом.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Планирование дня",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Начинайте каждый день с составления плана. Выделите 3 самые важные задачи, которые нужно выполнить сегодня. Используйте ежедневник или приложения для планирования.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Борьба с прокрастинацией",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Прокрастинация — главный враг продуктивности. Чтобы побороть желание отложить дела на потом, разбивайте большие задачи на маленькие шаги и начинайте с самого простого.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                )
            ),
            checklist = ""
        ),
        ArticleContentEntity(
            articleId = 2,
            blocks = listOf(
                ContentBlock.Paragraph(
                    text = "Техники продуктивности",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Существует множество техник продуктивности, которые помогают работать эффективнее и успевать больше. Рассмотрим самые популярные и действенные методы.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Метод Pomodoro",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Техника предполагает работу интервалами по 25 минут, после каждого интервала следует 5-минутный перерыв. После 4 таких циклов делается длинный перерыв 15-30 минут. Это помогает сохранять концентрацию и не переутомляться.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Метод GTD (Getting Things Done)",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Методология Дэвида Аллена учит фиксировать все задачи и идеи в надежной системе, освобождая голову для творчества и решения текущих задач. Важно регулярно пересматривать и обновлять списки дел.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Матрица Эйзенхауэра",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Разделите все дела на 4 категории:\n• Важные и срочные — делайте сразу\n• Важные, но не срочные — планируйте\n• Срочные, но не важные — делегируйте\n• Не важные и не срочные — удаляйте",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                )
            ),
            checklist = ""
        ),
        ArticleContentEntity(
            articleId = 3,
            blocks = listOf(
                ContentBlock.Paragraph(
                    text = "Медитация для начинающих",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Медитация — это практика тренировки ума, которая помогает развить осознанность, снизить стресс и улучшить концентрацию. Для начинающих важно начать с простых техник и постепенно увеличивать время практики.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "С чего начать?",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Начните с 5-10 минут в день. Найдите тихое место, сядьте удобно, закройте глаза и сосредоточьтесь на дыхании. Когда мысли отвлекают, просто возвращайте внимание к дыханию без осуждения.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Простые техники для новичков:",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "1. Медитация на дыхание\nСледите за естественным дыханием, ощущая, как воздух входит и выходит через нос.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "2. Сканирование тела\nМысленно проходитесь вниманием по всему телу от макушки до пальцев ног, замечая ощущения.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "3. Медитация с мантрой\nПовторяйте про себя слово или фразу, чтобы удерживать фокус внимания.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                )
            ),
            checklist = ""
        ),
        ArticleContentEntity(
            articleId = 4,
            blocks = listOf(
                ContentBlock.Paragraph(
                    text = "Здоровый сон и режим",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Качественный сон — основа здоровья и продуктивности. Во сне организм восстанавливается, обрабатывает информацию и накапливает энергию для нового дня.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Сколько нужно спать?",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Взрослому человеку требуется 7-9 часов сна. Важна не только продолжительность, но и регулярность — ложитесь и вставайте в одно и то же время, даже в выходные.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Правила гигиены сна:",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "1. За час до сна откажитесь от гаджетов\nСиний свет экранов подавляет выработку мелатонина — гормона сна.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "2. Создайте ритуал отхода ко сну\nТеплая ванна, чтение книги, легкая растяжка помогут настроиться на сон.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "3. Обеспечьте комфортные условия\nВ спальне должно быть темно, тихо и прохладно (18-20°C).",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "4. Не ешьте перед сном\nПоследний прием пищи должен быть за 2-3 часа до сна.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                )
            ),
            checklist = ""
        ),
        ArticleContentEntity(
            articleId = 5,
            blocks = listOf(
                ContentBlock.Paragraph(
                    text = "Утренние ритуалы",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Утро задает тон всему дню. Правильные утренние привычки помогают проснуться, настроиться на продуктивный лад и сохранять энергию до вечера.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Идеальное утро: с чего начать?",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Вставайте без телефона. Первые 30 минут после пробуждения посвятите себе, а не проверке соцсетей и новостей.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Полезные утренние привычки:",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "1. Стакан воды\nВыпейте стакан теплой воды с лимоном — это запустит пищеварение и восполнит жидкость после сна.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "2. Легкая зарядка\n5-10 минут растяжки или йоги разбудят тело и улучшат кровообращение.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "3. Медитация или дыхательная практика\n5 минут осознанного дыхания помогут успокоить ум и настроиться на день.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "4. Планирование дня\nЗапишите 3 главные задачи на день. Это поможет сфокусироваться на важном.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "5. Полезный завтрак\nНе пропускайте завтрак. Включите в него белки, сложные углеводы и полезные жиры для энергии.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                )
            ),
            checklist = ""
        )
    )


    private val favorites = mutableListOf<FavoriteEntity>()
    private val likes = mutableListOf<LikeEntity>()

    private val mutex = Mutex()
    private var nextFavoriteId = 1
    private var nextLikeId = 1



    override suspend fun getArticles(): List<ArticleEntity> = articles

    override suspend fun getArticleById(articleId: Int): ArticleEntity? =
        articles.find { it.id == articleId }

    override suspend fun getArticlesByCategory(categoryId: Int): List<ArticleEntity> =
        articles.filter { it.categoryId == categoryId }

    override suspend fun getCategories(): List<CategoryEntity> = categories

    override suspend fun getArticleContent(articleId: Int): ArticleContentEntity? =
        articleContents.find { it.articleId == articleId }


    override suspend fun getFavorites(userId: Int): List<FavoriteEntity> =
        favorites.filter { it.userId == userId }

    override suspend fun addFavorite(userId: Int, articleId: Int): FavoriteEntity = mutex.withLock {
        if (favorites.any { it.userId == userId && it.articleId == articleId }) {
            throw IllegalStateException("Already in favorites")
        }
        val favorite = FavoriteEntity(
            id = nextFavoriteId++,
            userId = userId,
            articleId = articleId
        )
        favorites.add(favorite)
        return favorite
    }

    override suspend fun removeFavorite(userId: Int, articleId: Int): Boolean = mutex.withLock {
        favorites.removeAll { it.userId == userId && it.articleId == articleId }
    }

    override suspend fun isFavorite(userId: Int, articleId: Int): Boolean =
        favorites.any { it.userId == userId && it.articleId == articleId }

    override suspend fun getLikes(articleId: Int): List<LikeEntity> =
        likes.filter { it.articleId == articleId }

    override suspend fun getUserLikes(userId: Int): List<LikeEntity> =
        likes.filter { it.userId == userId }

    override suspend fun addLike(userId: Int, articleId: Int): LikeEntity = mutex.withLock {
        if (likes.any { it.userId == userId && it.articleId == articleId }) {
            throw IllegalStateException("Already liked")
        }
        val like = LikeEntity(
            id = nextLikeId++,
            userId = userId,
            articleId = articleId
        )
        likes.add(like)
        return like
    }

    override suspend fun removeLike(userId: Int, articleId: Int): Boolean = mutex.withLock {
        likes.removeAll { it.userId == userId && it.articleId == articleId }
    }

    override suspend fun isLiked(userId: Int, articleId: Int): Boolean =
        likes.any { it.userId == userId && it.articleId == articleId }

    override suspend fun getLikesCount(articleId: Int): Int =
        likes.count { it.articleId == articleId }
}