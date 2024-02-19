import tracker.controllers.Manager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        Manager taskManager = new Manager();

        Task task1 = new Task("Помыть посуду", "Вымыть всю посуду ", 1);
        Task task2 = new Task("Сходить в магазин", "Купить продукты для ужина", 2);

        Epic epic1 = new Epic("Организация большого  праздника", "Подготовка к празднику", 3);
        Subtask subtask1 = new Subtask("Выбрать праздничное меню", "Подготовить список блюд для праздника", 5, epic1);
        Subtask subtask2 = new Subtask("Разослать приглашения", "Отправить приглашения родственникам и друзьям", 6, epic1);

        Epic epic2 = new Epic("Подготовка к отпуску", "Купить билеты, забронировать отель", 4);
        Subtask subtask3 = new Subtask("Купить билеты", "Приобрести авиабилеты ", 7, epic2);


        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(epic1);
        taskManager.addSubtask(subtask1, epic1);
        taskManager.addSubtask(subtask2, epic1);
        taskManager.addTask(epic2);
        taskManager.addSubtask(subtask3, epic2);

        System.out.println("Список задач:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getName());
        }

        taskManager.setStatusForSubtask(subtask1, Status.DONE);
        taskManager.setStatusForSubtask(subtask2, Status.IN_PROGRESS);
        taskManager.updateEpicStatus(epic1);
        taskManager.updateEpicStatus(epic2);

        System.out.println("Статус эпика 1: " + epic1.getStatus());
        System.out.println("Статус эпика 2: " + epic2.getStatus());
        System.out.println("Статус подзадачи 1: " + subtask1.getStatus());
        System.out.println("Статус подзадачи 2: " + subtask2.getStatus());
        System.out.println("Статус подзадачи 3: " + subtask3.getStatus());

        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteTaskById(epic2.getId());

        System.out.println("Список задач после удаления:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getName());
        }
    }
}
