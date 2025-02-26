# java-kanban
## Приложение "Трекер задач"
Монолитное приложение для управления задачами включает три типа задач (task, subtask, epic) и три статуса (new, in progress, done). Реализованы функции хранения данных в памяти, работы с XML, а также создание, обновление и управление статусами задач с возможностью просмотра истории.



# Основная функциональность:

    Хранение данных в оперативной памяти, загрузка и сохранение данных в XML-файл
    Создание, получение, удаление, обновление, управление статусами всех типов задач
    Получение истории просмотров задач

# Для каждого вида задач предусмотрены следующие действия:

    Получение конкретной задачи по id
    Получение всех задач
    Добавление задачи
    Обновление задачи
    Удаление задачи по id
    Удаление всех задач
    Получение списка приоритетных задач по времени
    Получение истории из 10 последних просмотренных задач

# Tech Stack 🔧

Java Core, JUnit 5, Gson, HttpServer, KVServer, Insomnia, API

# API Reference ⚙️

    GET tasks/ - получить список приоритетных задач

    GET tasks/history - получить историю из 10 последних просмотренных задач

    GET subtask/epic/{id} - получить список подзадач конкретного эпика

    GET tasks/task - получить список всех задач

    GET tasks/task/{id} - получить задачу по id

    POST tasks/task - создать задачу

    POST tasks/task/{id} - обновить задачу

    DELETE tasks/task - удалить все задачи

    DELETE tasks/task/{id} - удалить задачу по id *аналогичные эндпоинты для эпиков и подзадач

# How to start the project ▶️
1) Склонируйте репозиторий и перейдите в него
2) Запустите проект в выбранной IDE
3) Перейдите по адресу http://localhost:8070/tasks
4) Можно работать с проектом