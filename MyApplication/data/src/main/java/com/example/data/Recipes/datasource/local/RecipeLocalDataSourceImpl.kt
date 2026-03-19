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
        GroceryEntity(2, "Рыба", "Речная, морская"),
        GroceryEntity(3, "Овощи", "Свежие, замороженные"),
        GroceryEntity(4, "Фрукты", "Свежие, сухофрукты"),
        GroceryEntity(5, "Крупы", "Рис, гречка, овсянка"),
        GroceryEntity(6, "Молочные продукты", "Молоко, сыр, йогурт"),
        GroceryEntity(7, "Зелень", "Укроп, петрушка, кинза"),
        GroceryEntity(8, "Специи", "Соль, перец, приправы")
    )

    // Таблица рецептов (карточки)
    //ДОБАВИТЬ НОРМАЛЬНЫЕ КАРТИНКИ
    private val recipes = listOf(
        RecipeEntity(
            id = 1,
            title = "Борщ с пампушками",
            description = "Традиционный украинский борщ с мясом и свеклой, подается с чесночными пампушками",
            author = "Елена Иванова",
            previewImageUrl = "https://images.unsplash.com/photo-1547592180-2f1a1b3c3b3a?w=500",
            cookingTimeMinutes = 90,
            createdAt = LocalDateTime.now().minusDays(15),
            updatedAt = LocalDateTime.now().minusDays(10),
            likesCount = 45
        ),
        RecipeEntity(
            id = 2,
            title = "Цезарь с курицей",
            description = "Классический салат Цезарь с куриным филе, пармезаном и соусом",
            author = "Алексей Петров",
            previewImageUrl = "https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=500",
            cookingTimeMinutes = 25,
            createdAt = LocalDateTime.now().minusDays(20),
            updatedAt = LocalDateTime.now().minusDays(5),
            likesCount = 32
        ),
        RecipeEntity(
            id = 3,
            title = "Паста Карбонара",
            description = "Итальянская паста с беконом, яйцом и сыром пармезан",
            author = "Мария Смирнова",
            previewImageUrl = "https://images.unsplash.com/photo-1612874742237-6526221588e3?w=500",
            cookingTimeMinutes = 30,
            createdAt = LocalDateTime.now().minusDays(25),
            updatedAt = LocalDateTime.now().minusDays(7),
            likesCount = 67
        ),
        RecipeEntity(
            id = 4,
            title = "Шоколадный брауни",
            description = "Влажный шоколадный пирог с орехами и шоколадной глазурью",
            author = "Дмитрий Соколов",
            previewImageUrl = "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=500",
            cookingTimeMinutes = 45,
            createdAt = LocalDateTime.now().minusDays(10),
            updatedAt = LocalDateTime.now().minusDays(2),
            likesCount = 89
        ),
        RecipeEntity(
            id = 5,
            title = "Овощной суп-пюре",
            description = "Легкий суп из тыквы, моркови и картофеля со сливками",
            author = "Анна Кузнецова",
            previewImageUrl = "https://images.unsplash.com/photo-1476718406336-bb5a9690ee2a?w=500",
            cookingTimeMinutes = 40,
            createdAt = LocalDateTime.now().minusDays(30),
            updatedAt = LocalDateTime.now().minusDays(12),
            likesCount = 23
        ),
        RecipeEntity(
            id = 6,
            title = "Гречка по-купечески",
            description = "Гречневая каша с мясом, луком и морковью в горшочке",
            author = "Иван Петров",
            previewImageUrl = "https://images.unsplash.com/photo-1586201375761-83865001e8ac?w=500",
            cookingTimeMinutes = 50,
            createdAt = LocalDateTime.now().minusDays(18),
            updatedAt = LocalDateTime.now().minusDays(3),
            likesCount = 18
        ),
        RecipeEntity(
            id = 7,
            title = "Пицца Маргарита",
            description = "Тонкое тесто, томатный соус, моцарелла и базилик",
            author = "Павел Орлов",
            previewImageUrl = "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=500",
            cookingTimeMinutes = 35,
            createdAt = LocalDateTime.now().minusDays(22),
            updatedAt = LocalDateTime.now().minusDays(8),
            likesCount = 56
        ),
        RecipeEntity(
            id = 8,
            title = "Салат Оливье",
            description = "Классический новогодний салат с вареной колбасой",
            author = "Татьяна Морозова",
            previewImageUrl = "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=500",
            cookingTimeMinutes = 60,
            createdAt = LocalDateTime.now().minusDays(40),
            updatedAt = LocalDateTime.now().minusDays(15),
            likesCount = 34
        ),
        RecipeEntity(
            id = 9,
            title = "Молочный коктейль",
            description = "Освежающий коктейль из молока и мороженого с сиропом",
            author = "Сергей Волков",
            previewImageUrl = "https://images.unsplash.com/photo-1551024601-bec78aea704b?w=500",
            cookingTimeMinutes = 10,
            createdAt = LocalDateTime.now().minusDays(12),
            updatedAt = LocalDateTime.now().minusDays(1),
            likesCount = 12
        ),
        RecipeEntity(
            id = 10,
            title = "Яблочный пирог",
            description = "Нежный пирог с яблоками и корицей",
            author = "Ольга Новикова",
            previewImageUrl = "https://images.unsplash.com/photo-1600335895229-6e755c92e4a7?w=500",
            cookingTimeMinutes = 70,
            createdAt = LocalDateTime.now().minusDays(28),
            updatedAt = LocalDateTime.now().minusDays(9),
            likesCount = 41
        )
    )


    // Связи рецептов с категориями
    private val recipeCategories = listOf(
        // Борщ - Первые блюда
        RecipeCategoryCrossEntity(1, 1),
        // Цезарь - Салаты
        RecipeCategoryCrossEntity(2, 3),
        // Паста - Вторые блюда
        RecipeCategoryCrossEntity(3, 2),
        // Брауни - Десерты
        RecipeCategoryCrossEntity(4, 4),
        // Суп-пюре - Первые блюда
        RecipeCategoryCrossEntity(5, 1),
        // Гречка - Вторые блюда
        RecipeCategoryCrossEntity(6, 2),
        // Пицца - Вторые блюда и Выпечка
        RecipeCategoryCrossEntity(7, 2),
        RecipeCategoryCrossEntity(7, 6),
        // Оливье - Салаты
        RecipeCategoryCrossEntity(8, 3),
        // Коктейль - Напитки
        RecipeCategoryCrossEntity(9, 5),
        // Пирог - Десерты и Выпечка
        RecipeCategoryCrossEntity(10, 4),
        RecipeCategoryCrossEntity(10, 6)
    )

    // Связи рецептов с продуктами
    private val recipeGroceries = listOf(
        // Суп-пюре
        RecipeGroceryCrossEntity(5, 3), // Овощи
        // Гречка
        RecipeGroceryCrossEntity(6, 5), // Крупы
        // Коктейль
        RecipeGroceryCrossEntity(9, 6), // Молочные
        // Пирог
        RecipeGroceryCrossEntity(10, 4), // Фрукты
    )

    private val recipeContents = listOf(
        RecipeContentEntity(
            recipeId = 1,
            ingredients = listOf(
                IngredientEntity(1, "Говядина", 500.0, "г", 1),
                IngredientEntity(2, "Свекла", 300.0, "г", 1),
                IngredientEntity(3, "Картофель", 400.0, "г", 1),
                IngredientEntity(4, "Морковь", 200.0, "г", 1),
                IngredientEntity(5, "Капуста", 300.0, "г", 1),
                IngredientEntity(6, "Лук", 150.0, "г", 1),
                IngredientEntity(7, "Томатная паста", 2.0, "ст.л.", 1),
                IngredientEntity(8, "Чеснок", 3.0, "зубчика", 1),
                IngredientEntity(9, "Укроп", 1.0, "пучок", 1),
                IngredientEntity(10, "Сметана", 100.0, "г", 1)
            ),
            cookingSteps = listOf(
                ContentBlock.Paragraph(
                    text = "Приготовление борща",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 1: Варим бульон",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Мясо залейте холодной водой (3 литра), доведите до кипения, снимите пену. Убавьте огонь и варите 1.5 часа. За 30 минут до готовности посолите.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 2: Подготовка овощей",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Свеклу очистите и натрите на крупной терке. Морковь натрите, лук мелко нарежьте. Картофель нарежьте кубиками, капусту нашинкуйте.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 3: Зажарка",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "На сковороде разогрейте масло, обжарьте лук до прозрачности, добавьте морковь и свеклу. Тушите 5-7 минут, затем добавьте томатную пасту и еще 2 минуты.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 4: Сборка борща",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Из готового бульона выньте мясо, отделите от кости и нарежьте кусочками. В бульон положите картофель, через 10 минут - капусту. Варите еще 10 минут.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Добавьте зажарку и нарезанное мясо, варите 5-7 минут. В конце добавьте измельченный чеснок и зелень. Дайте настояться 20-30 минут.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                )
            ),
            tips = "Для более насыщенного цвета добавьте в зажарку немного лимонного сока или уксуса. Борщ вкуснее на второй день. Подавайте со сметаной и пампушками."
        ),

        RecipeContentEntity(
            recipeId = 2,
            ingredients = listOf(
                IngredientEntity(11, "Куриное филе", 400.0, "г", 2),
                IngredientEntity(12, "Салат Айсберг", 200.0, "г", 2),
                IngredientEntity(13, "Помидоры черри", 150.0, "г", 2),
                IngredientEntity(14, "Сыр пармезан", 50.0, "г", 2),
                IngredientEntity(15, "Белый хлеб", 100.0, "г", 2),
                IngredientEntity(16, "Яйца", 2.0, "шт", 2),
                IngredientEntity(17, "Соус Цезарь", 100.0, "мл", 2),
                IngredientEntity(18, "Чеснок", 1.0, "зубчик", 2),
                IngredientEntity(19, "Оливковое масло", 2.0, "ст.л.", 2)
            ),
            cookingSteps = listOf(
                ContentBlock.Paragraph(
                    text = "Приготовление салата Цезарь",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 1: Подготовка курицы",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Куриное филе промойте, обсушите. Натрите солью, перцем и измельченным чесноком. Обжарьте на оливковом масле по 5-7 минут с каждой стороны до золотистой корочки. Остудите и нарежьте кубиками.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 2: Гренки",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Хлеб нарежьте кубиками, подсушите на сковороде без масла или в духовке при 180°C 5-7 минут до золотистого цвета.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 3: Яйца",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Яйца сварите вкрутую (10 минут после закипания). Остудите в холодной воде, очистите и нарежьте.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 4: Сборка",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Салат порвите руками на небольшие кусочки. Помидоры черри разрежьте пополам. Сыр натрите на терке.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "В большой миске смешайте салат, курицу, яйца и помидоры. Заправьте соусом, перемешайте. Сверху посыпьте тертым сыром и гренками. Подавайте сразу.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                )
            ),
            tips = "Гренки добавляйте непосредственно перед подачей, чтобы они оставались хрустящими. Соус можно приготовить самостоятельно: смешайте майонез, йогурт, чеснок, пармезан и анчоусы."
        ),

        RecipeContentEntity(
            recipeId = 3,
            ingredients = listOf(
                IngredientEntity(20, "Спагетти", 400.0, "г", 3),
                IngredientEntity(21, "Бекон", 200.0, "г", 3),
                IngredientEntity(22, "Яйца", 4.0, "шт", 3),
                IngredientEntity(23, "Сыр пармезан", 100.0, "г", 3),
                IngredientEntity(24, "Чеснок", 2.0, "зубчика", 3),
                IngredientEntity(25, "Сливки 20%", 100.0, "мл", 3),
                IngredientEntity(26, "Оливковое масло", 2.0, "ст.л.", 3),
                IngredientEntity(27, "Петрушка", 1.0, "пучок", 3)
            ),
            cookingSteps = listOf(
                ContentBlock.Paragraph(
                    text = "Приготовление пасты Карбонара",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 1: Паста",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "В большой кастрюле вскипятите воду (1 литр на 100г пасты). Добавьте соль (10г на литр). Спагетти варите согласно инструкции до состояния al dente (слегка недоваренные). Сохраните 1 стакан воды от варки.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 2: Бекон",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Бекон нарежьте полосками. На сковороде разогрейте оливковое масло, обжарьте бекон до хруста (5-7 минут). Добавьте измельченный чеснок, готовьте еще 1 минуту. Снимите с огня.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 3: Соус",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "В миске взбейте яйца с тертым пармезаном и сливками. Добавьте черный перец. Тщательно перемешайте.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 4: Соединение",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Готовые спагетти переложите в сковороду с беконом (сковорода должна быть снята с огня). Хорошо перемешайте. Быстро влейте яичную смесь и активно перемешивайте, пока соус не загустеет. Если соус слишком густой, добавьте немного воды от варки пасты.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Подавайте сразу, посыпав дополнительным пармезаном, черным перцем и рубленой петрушкой.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                )
            ),
            tips = "Не ставьте сковороду обратно на огонь после добавления яиц - яйца свернутся и соус испортится. В Италии в карбонару не добавляют сливки, но с ними соус получается более нежным."
        ),

        RecipeContentEntity(
            recipeId = 4,
            ingredients = listOf(
                IngredientEntity(28, "Темный шоколад", 200.0, "г", 4),
                IngredientEntity(29, "Сливочное масло", 150.0, "г", 4),
                IngredientEntity(30, "Сахар", 200.0, "г", 4),
                IngredientEntity(31, "Яйца", 3.0, "шт", 4),
                IngredientEntity(32, "Мука", 100.0, "г", 4),
                IngredientEntity(33, "Какао-порошок", 2.0, "ст.л.", 4),
                IngredientEntity(34, "Грецкие орехи", 100.0, "г", 4),
                IngredientEntity(35, "Ванильный экстракт", 1.0, "ч.л.", 4)
            ),
            cookingSteps = listOf(
                ContentBlock.Paragraph(
                    text = "Приготовление шоколадного брауни",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 1: Шоколадная основа",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шоколад поломайте на кусочки, добавьте нарезанное кубиками масло. Растопите на водяной бане или в микроволновке (импульсами по 30 секунд, перемешивая). Остудите до комнатной температуры.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 2: Яйца с сахаром",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Яйца взбейте с сахаром и ванильным экстрактом до пышной светлой массы (миксером 3-5 минут).",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 3: Соединение",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "В яичную массу влейте остывший шоколад, аккуратно перемешайте лопаткой. Просейте муку и какао, снова перемешайте до однородности. Орехи порубите и вмешайте в тесто.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 4: Выпекание",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Форму 20х20 см застелите пергаментом. Вылейте тесто, разровняйте. Выпекайте при 180°C 25-30 минут. Важно не пересушить! Края должны пропечься, а середина оставаться слегка влажной.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Дайте полностью остыть в форме, затем нарежьте квадратиками. Подавайте с шариком ванильного мороженого.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                )
            ),
            tips = "Брауни должен оставаться слегка влажным внутри. Проверяйте зубочисткой - она должна выходить с влажными крошками, не сухая. Чем дольше выпекать, тем более сухим будет брауни."
        ),

        RecipeContentEntity(
            recipeId = 5,
            ingredients = listOf(
                IngredientEntity(36, "Тыква", 500.0, "г", 5),
                IngredientEntity(37, "Картофель", 300.0, "г", 5),
                IngredientEntity(38, "Морковь", 200.0, "г", 5),
                IngredientEntity(39, "Лук", 150.0, "г", 5),
                IngredientEntity(40, "Сливки 20%", 200.0, "мл", 5),
                IngredientEntity(41, "Чеснок", 2.0, "зубчика", 5),
                IngredientEntity(42, "Имбирь", 20.0, "г", 5),
                IngredientEntity(43, "Оливковое масло", 2.0, "ст.л.", 5),
                IngredientEntity(44, "Тыквенные семечки", 30.0, "г", 5)
            ),
            cookingSteps = listOf(
                ContentBlock.Paragraph(
                    text = "Приготовление овощного супа-пюре",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 1: Подготовка овощей",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Тыкву очистите от кожуры и семян, нарежьте кубиками. Картофель и морковь очистите, нарежьте кубиками. Лук и чеснок мелко нарежьте. Имбирь натрите на терке.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 2: Обжарка",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "В кастрюле с толстым дном разогрейте масло, обжарьте лук до прозрачности (3 минуты). Добавьте чеснок и имбирь, готовьте еще 1 минуту.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 3: Варка",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Добавьте морковь, тыкву и картофель. Залейте водой так, чтобы она покрывала овощи. Посолите, поперчите. Доведите до кипения, убавьте огонь и варите 20-25 минут до мягкости овощей.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 4: Пюрирование",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Снимите кастрюлю с огня. Погружным блендером измельчите суп до однородного состояния. Добавьте сливки, перемешайте. Если суп слишком густой, добавьте кипяток до желаемой консистенции.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Подавайте горячим, посыпав тыквенными семечками и полив тыквенным маслом.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                )
            ),
            tips = "Для более насыщенного вкуса часть воды можно заменить куриным бульоном. Имбирь добавляет пикантность - если не любите, можно не добавлять."
        ),

        RecipeContentEntity(
            recipeId = 6,
            ingredients = listOf(
                IngredientEntity(45, "Гречка", 300.0, "г", 6),
                IngredientEntity(46, "Свинина", 400.0, "г", 6),
                IngredientEntity(47, "Лук", 200.0, "г", 6),
                IngredientEntity(48, "Морковь", 200.0, "г", 6),
                IngredientEntity(49, "Чеснок", 3.0, "зубчика", 6),
                IngredientEntity(50, "Томатная паста", 1.0, "ст.л.", 6),
                IngredientEntity(51, "Растительное масло", 3.0, "ст.л.", 6),
                IngredientEntity(52, "Лавровый лист", 2.0, "шт", 6),
                IngredientEntity(53, "Зелень", 1.0, "пучок", 6)
            ),
            cookingSteps = listOf(
                ContentBlock.Paragraph(
                    text = "Приготовление гречки по-купечески",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 1: Подготовка мяса",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Свинину нарежьте небольшими кубиками. На сковороде разогрейте масло, обжарьте мясо до золотистой корочки (7-10 минут). Переложите в казан или глубокую сковороду.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 2: Овощи",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Лук нарежьте кубиками, морковь натрите на крупной терке. На той же сковороде обжарьте лук до прозрачности, добавьте морковь и готовьте еще 5 минут. Добавьте томатную пасту, перемешайте.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 3: Гречка",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Гречку переберите, промойте холодной водой. Добавьте к мясу обжаренные овощи и гречку. Залейте горячей водой (2.5 стакана), посолите, добавьте лавровый лист.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 4: Тушение",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Доведите до кипения, убавьте огонь до минимума, накройте крышкой и тушите 20-25 минут до готовности гречки. В конце добавьте измельченный чеснок и зелень, перемешайте.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Дайте настояться под крышкой 10 минут перед подачей.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                )
            ),
            tips = "Можно использовать любое мясо: говядину, курицу или смесь. Для более насыщенного вкуса добавьте грибы."
        ),

        RecipeContentEntity(
            recipeId = 7,
            ingredients = listOf(
                IngredientEntity(54, "Мука", 500.0, "г", 7),
                IngredientEntity(55, "Дрожжи сухие", 7.0, "г", 7),
                IngredientEntity(56, "Вода", 300.0, "мл", 7),
                IngredientEntity(57, "Оливковое масло", 3.0, "ст.л.", 7),
                IngredientEntity(58, "Соль", 10.0, "г", 7),
                IngredientEntity(59, "Сахар", 5.0, "г", 7),
                IngredientEntity(60, "Томатный соус", 150.0, "г", 7),
                IngredientEntity(61, "Моцарелла", 250.0, "г", 7),
                IngredientEntity(62, "Базилик", 1.0, "пучок", 7)
            ),
            cookingSteps = listOf(
                ContentBlock.Paragraph(
                    text = "Приготовление пиццы Маргарита",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 1: Тесто",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "В теплой воде (не выше 40°C) растворите дрожжи и сахар, оставьте на 10 минут. Муку просейте с солью. Влейте дрожжевую смесь и масло, замесите тесто. Месите 10 минут до гладкости.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 2: Расстойка",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Положите тесто в миску, накройте пленкой и оставьте в теплом месте на 1 час, пока тесто не увеличится вдвое.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 3: Формовка",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Тесто обомните, раскатайте в круг толщиной 5 мм. Переложите на противень, смазанный маслом. Сделайте бортики.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 4: Сборка",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Смажьте основу томатным соусом. Моцареллу нарежьте ломтиками и разложите сверху. Посолите, поперчите.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 5: Выпекание",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Выпекайте при 250°C 10-12 минут до золотистого края. Готовую пиццу посыпьте свежими листьями базилика.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                )
            ),
            tips = "Для настоящей итальянской пиццы нужна очень высокая температура. Если есть камень для пиццы, используйте его."
        ),

        RecipeContentEntity(
            recipeId = 8,
            ingredients = listOf(
                IngredientEntity(63, "Картофель", 400.0, "г", 8),
                IngredientEntity(64, "Морковь", 200.0, "г", 8),
                IngredientEntity(65, "Яйца", 4.0, "шт", 8),
                IngredientEntity(66, "Колбаса вареная", 300.0, "г", 8),
                IngredientEntity(67, "Огурцы соленые", 200.0, "г", 8),
                IngredientEntity(68, "Горошек консервированный", 200.0, "г", 8),
                IngredientEntity(69, "Майонез", 150.0, "г", 8),
                IngredientEntity(70, "Укроп", 1.0, "пучок", 8)
            ),
            cookingSteps = listOf(
                ContentBlock.Paragraph(
                    text = "Приготовление салата Оливье",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 1: Варка овощей",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Картофель и морковь вымойте, отварите в мундире до готовности (картофель 25-30 минут, морковь 20-25 минут). Остудите, очистите.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 2: Яйца",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Яйца сварите вкрутую (10 минут после закипания). Остудите в холодной воде, очистите.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 3: Нарезка",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Все ингредиенты нарежьте мелкими кубиками одинакового размера (примерно 5-7 мм): картофель, морковь, колбасу, огурцы, яйца.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 4: Смешивание",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "В большой миске смешайте все нарезанные ингредиенты, добавьте горошек (слив жидкость). Посолите, поперчите по вкусу. Заправьте майонезом, хорошо перемешайте.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Перед подачей охладите в холодильнике минимум 1 час. Украсьте зеленью.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                )
            ),
            tips = "Для более нежного вкуса можно заменить колбасу отварным мясом или курицей. Лук в классическом Оливье не добавляют, но по желанию можно добавить зеленый лук."
        ),

        RecipeContentEntity(
            recipeId = 9,
            ingredients = listOf(
                IngredientEntity(71, "Молоко", 300.0, "мл", 9),
                IngredientEntity(72, "Мороженое пломбир", 200.0, "г", 9),
                IngredientEntity(73, "Сироп (любой)", 50.0, "мл", 9),
                IngredientEntity(74, "Взбитые сливки", 50.0, "г", 9),
                IngredientEntity(75, "Шоколадная крошка", 20.0, "г", 9),
                IngredientEntity(76, "Вишня для украшения", 3.0, "шт", 9)
            ),
            cookingSteps = listOf(
                ContentBlock.Paragraph(
                    text = "Приготовление молочного коктейля",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 1: Подготовка",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Молоко охладите. Мороженое достаньте из морозилки на 5-10 минут, чтобы оно немного подтаяло.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 2: Смешивание",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "В чашу блендера налейте молоко, добавьте мороженое и сироп. Взбивайте на высокой скорости 1-2 минуты до образования пышной пены.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 3: Подача",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Разлейте коктейль по высоким бокалам. Сверху украсьте взбитыми сливками, посыпьте шоколадной крошкой и украсьте вишней.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Подавайте сразу с трубочкой.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                )
            ),
            tips = "Пропорции можно менять по вкусу: больше мороженого - гуще и слаще коктейль. Для шоколадного коктейля добавьте какао или шоколадный сироп."
        ),

        RecipeContentEntity(
            recipeId = 10,
            ingredients = listOf(
                IngredientEntity(77, "Мука", 250.0, "г", 10),
                IngredientEntity(78, "Сливочное масло", 150.0, "г", 10),
                IngredientEntity(79, "Сахар", 200.0, "г", 10),
                IngredientEntity(80, "Яйца", 3.0, "шт", 10),
                IngredientEntity(81, "Яблоки", 500.0, "г", 10),
                IngredientEntity(82, "Разрыхлитель", 10.0, "г", 10),
                IngredientEntity(83, "Корица", 1.0, "ч.л.", 10),
                IngredientEntity(84, "Сахарная пудра", 2.0, "ст.л.", 10)
            ),
            cookingSteps = listOf(
                ContentBlock.Paragraph(
                    text = "Приготовление яблочного пирога",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 1: Тесто",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Масло комнатной температуры взбейте с сахаром до пышности. По одному добавьте яйца, взбивая после каждого. Муку смешайте с разрыхлителем, просейте и добавьте в масляную смесь. Быстро замесите мягкое тесто.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 2: Яблоки",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Яблоки очистите от кожуры и сердцевины, нарежьте тонкими дольками. Смешайте с корицей.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 3: Сборка",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Форму (22-24 см) смажьте маслом и присыпьте мукой. Выложите тесто, разровняйте, сделайте бортики. Красиво выложите яблоки сверху, слегка вдавливая в тесто.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Шаг 4: Выпекание",
                    style = TextStyle.Bold,
                    size = 18,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Выпекайте при 180°C 40-50 минут до золотистого цвета. Готовность проверяйте деревянной шпажкой - она должна выходить сухой.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Остудите в форме 15 минут, затем переложите на решетку. Перед подачей посыпьте сахарной пудрой.",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                )
            ),
            tips = "Яблоки лучше брать кисло-сладкие сорта. В тесто можно добавить цедру лимона или ваниль для аромата."
        )
    )


    private val favorites = mutableListOf<FavoriteEntity>()
    private val likes = mutableListOf<LikeEntity>()

    private val mutex = Mutex()
    private var nextFavoriteId = 1
    private var nextLikeId = 1


    override suspend fun getRecipes(): List<RecipeEntity> = recipes

    override suspend fun getRecipeById(recipeId: Int): RecipeEntity? =
        recipes.find { it.id == recipeId }

    override suspend fun getRecipesByCategory(categoryId: Int): List<RecipeEntity> {
        val recipeIds = recipeCategories
            .filter { it.categoryId == categoryId }
            .map { it.recipeId }
        return recipes.filter { it.id in recipeIds }
    }

    override suspend fun getRecipesByGrocery(groceryId: Int): List<RecipeEntity> {
        val recipeIds = recipeGroceries
            .filter { it.groceryId == groceryId }
            .map { it.recipeId }
        return recipes.filter { it.id in recipeIds }
    }

    override suspend fun searchRecipes(query: String): List<RecipeEntity> =
        recipes.filter { recipe ->
            recipe.title.contains(query, ignoreCase = true) ||
                    recipe.description?.contains(query, ignoreCase = true) == true
        }

    override suspend fun getCategories(): List<CategoryEntity> = categories

    override suspend fun getGroceries(): List<GroceryEntity> = groceries

    override suspend fun getRecipeContent(recipeId: Int): RecipeContentEntity? =
        recipeContents.find { it.recipeId == recipeId }


    override suspend fun getFavorites(userId: Int): List<FavoriteEntity> =
        favorites.filter { it.userId == userId }

    override suspend fun addFavorite(userId: Int, recipeId: Int): FavoriteEntity = mutex.withLock {
        if (favorites.any { it.userId == userId && it.recipeId == recipeId }) {
            throw IllegalStateException("Уже в Избранном")
        }
        val favorite = FavoriteEntity(
            id = nextFavoriteId++,
            userId = userId,
            recipeId = recipeId
        )
        favorites.add(favorite)
        favorite
    }

    override suspend fun removeFavorite(userId: Int, recipeId: Int): Boolean = mutex.withLock {
        favorites.removeAll { it.userId == userId && it.recipeId == recipeId }
    }

    override suspend fun isFavorite(userId: Int, recipeId: Int): Boolean =
        favorites.any { it.userId == userId && it.recipeId == recipeId }

    override suspend fun getLikes(recipeId: Int): List<LikeEntity> =
        likes.filter { it.recipeId == recipeId }

    override suspend fun getUserLikes(userId: Int): List<LikeEntity> =
        likes.filter { it.userId == userId }

    override suspend fun addLike(userId: Int, recipeId: Int): LikeEntity = mutex.withLock {
        if (likes.any { it.userId == userId && it.recipeId == recipeId }) {
            throw IllegalStateException("Уже лайкнуто")
        }
        val like = LikeEntity(
            id = nextLikeId++,
            userId = userId,
            recipeId = recipeId
        )
        likes.add(like)

        val recipeIndex = recipes.indexOfFirst { it.id == recipeId }
        if (recipeIndex != -1) {
            val updatedRecipe = recipes[recipeIndex].copy(
                likesCount = recipes[recipeIndex].likesCount + 1
            )
            (recipes as MutableList)[recipeIndex] = updatedRecipe
        }

        like
    }

    override suspend fun removeLike(userId: Int, recipeId: Int): Boolean = mutex.withLock {
        val removed = likes.removeAll { it.userId == userId && it.recipeId == recipeId }

        if (removed) {
            val recipeIndex = recipes.indexOfFirst { it.id == recipeId }
            if (recipeIndex != -1) {
                val updatedRecipe = recipes[recipeIndex].copy(
                    likesCount = maxOf(0, recipes[recipeIndex].likesCount - 1)
                )
                (recipes as MutableList)[recipeIndex] = updatedRecipe
            }
        }

        removed
    }

    override suspend fun isLiked(userId: Int, recipeId: Int): Boolean =
        likes.any { it.userId == userId && it.recipeId == recipeId }

    override suspend fun getLikesCount(recipeId: Int): Int =
        likes.count { it.recipeId == recipeId }

    override suspend fun getCategoryById(categoryId: Int): CategoryEntity? =
        categories.find { it.id == categoryId }

    override suspend fun getGroceryById(groceryId: Int): GroceryEntity? =
        groceries.find { it.id == groceryId }

    override suspend fun getAllRecipeCategoryCross(): List<RecipeCategoryCrossEntity> =
        recipeCategories

    override suspend fun getAllRecipeGroceryCross(): List<RecipeGroceryCrossEntity> =
        recipeGroceries

    override suspend fun getRecipeCategoryIds(recipeId: Int): List<Int> =
        recipeCategories
            .filter { it.recipeId == recipeId }
            .map { it.categoryId }

    override suspend fun getRecipeGroceryIds(recipeId: Int): List<Int> =
        recipeGroceries
            .filter { it.recipeId == recipeId }
            .map { it.groceryId }


    override suspend fun getIngredientsForRecipe(recipeId: Int): List<IngredientEntity> =
        recipeContents
            .find { it.recipeId == recipeId }
            ?.ingredients ?: emptyList()
}