package com.grigorevmp.simpletodo.ui.settings

enum class BuildType { RELEASE, BETA, ALPHA }

data class ChangelogEntry(
    val version: String,
    val title: String,
    val buildType: BuildType,
    val changes: List<String>
)

val CHANGELOG_ENTRIES = listOf(
    ChangelogEntry(
        version = "1.1.0",
        title = "Календарь и заметки",
        buildType = BuildType.RELEASE,
        changes = listOf(
            "Новый раздел проектов: список проектов, канбан по статусам и привязка задач/заметок к проектам",
            "Виджеты на Android: разные размеры, задачи на сегодня и ближайшие дни",
            "Новый календарь на главном экране с суммой часов по дням и списком задач",
            "Расширение функционала блокнота: интерактивные блоки, улучшенный просмотр и работа со вложениями",
            "Скролл в окне истории изменений"
        )
    ),
    ChangelogEntry(
        version = "1.0.2",
        title = "Локализация и стабильность",
        buildType = BuildType.BETA,
        changes = listOf(
            "Переводы интерфейса (RU/EN) и переключатель языка",
            "Импорт/экспорт данных и удаление всех данных",
            "Обновлён нижний бар: анимации, скругления, выравнивание",
            "Обновлён раздел настроек и информации о приложении"
        )
    ),
    ChangelogEntry(
        version = "0.7.1",
        title = "Новый экран заметок",
        buildType = BuildType.ALPHA,
        changes = listOf(
            "Новый экран заметок",
            "Улучшенная сортировка и фильтры",
            "Оптимизация анимаций и скролла",
            "Мелкие фиксы и стабильность"
        )
    ),
    ChangelogEntry(
        version = "0.7.0",
        title = "База приложения",
        buildType = BuildType.ALPHA,
        changes = listOf(
            "Домашний экран и таймлайн",
            "Подзадачи и теги",
            "Редактор заметок с Markdown"
        )
    )
)
