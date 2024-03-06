import tracker.controllers.HistoryManager;
import tracker.controllers.InMemoryHistoryManager;
import tracker.controllers.InMemoryManager;
import tracker.controllers.Manager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        HistoryManager historyManager = new InMemoryHistoryManager();
        Manager taskManager = new InMemoryManager(historyManager);




        Task task1 = new Task("Помыть посуду", "Вымыть всю посуду ", 1);
        Task task2 = new Task("Сходить в магазин", "Купить продукты для ужина", 2);

        Epic epic1 = new Epic("Организация большого  праздника", "Подготовка к празднику", 3);
        Subtask subtask1 = new Subtask("Выбрать праздничное меню", "Подготовить список блюд для праздника", 5, epic1);
        Subtask subtask2 = new Subtask("Разослать приглашения", "Отправить приглашения родственникам и друзьям", 6, epic1);

        Epic epic2 = new Epic("Подготовка к отпуску", "Купить билеты, забронировать отель", 4);
        Subtask subtask3 = new Subtask("Купить билеты", "Приобрести авиабилеты ", 7, epic2);


        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1, epic1);
        taskManager.addSubtask(subtask2, epic1);

        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3, epic2);

        System.out.println("Список задач:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getName());
        }
        System.out.println("Список эпиков:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic.getName());
        }

        System.out.println("Список подзадач:");
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println(subtask.getName());
        }





        System.out.println("Статус задачи 1: " + task1.getStatus());
        System.out.println("Статус задачи 2: " + task2.getStatus());

        System.out.println("Статус эпика 1: " + epic1.getStatus());
        System.out.println("Статус подзадачи 1 : " + subtask1.getStatus());
        System.out.println("Статус подзадачи 2: " + subtask2.getStatus());

        System.out.println("Статус эпика 2 : " + epic2.getStatus());
        System.out.println("Статус подзадачи 1: " + subtask3.getStatus());




        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epic1.getId());

        System.out.println("\n" + "Статус задачи 1: " + task1.getStatus() );
        System.out.println("Статус задачи 2: " + task2.getStatus());

        System.out.println("Статус эпика 1: " + epic1.getStatus());
        System.out.println("Статус подзадачи 1 : " + subtask1.getStatus());
        System.out.println("Статус подзадачи 2: " + subtask2.getStatus());

        System.out.println("Статус эпика 2 : " + epic2.getStatus());
        System.out.println("Статус подзадачи 1: " + subtask3.getStatus());

        System.out.println("Список задач после удаления:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getName());
        }

        System.out.println("Список эпиков после удаления:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic.getName());
        }

        System.out.println("Список подзадач после удаления:");
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println(subtask.getName());
        }
    }
    }