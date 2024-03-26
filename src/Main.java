import tracker.controllers.HistoryManager;
import tracker.controllers.InMemoryHistoryManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        HistoryManager historyManager = new InMemoryHistoryManager();



        Task task1 = new Task("Задача 1", "Описание задачи 1", 1);
        Task task2 = new Task("Задача 2", "Описание задачи 2", 2);
        Task task3 = new Task("Задача 77", "Описание задачи 77", 2);
        Task task99 = new Task("Задача 7777", "Описание задачи 8877", 2);
        Task task4 = new Task("Задача 3", "Описание задачи 3", 3);
        Task task5 = new Task("Задача 4", "Описание задачи 4", 4);
        Task task6 = new Task("Задача 5", "Описание задачи 5", 5);



        Epic epic1 = new Epic("Эпик с подзадачами", "Описание эпика с подзадачами", 22);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", 99, epic1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", 88, epic1);
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", 77, epic1);
        epic1.addSubtask(subtask1);
        epic1.addSubtask(subtask2);
        epic1.addSubtask(subtask3);


        Epic epic2 = new Epic("Эпик без подзадач", "Описание эпика без подзадач", 15);


        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task99);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.add(task6);

        historyManager.add(epic1);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        historyManager.add(subtask3);
        historyManager.add(epic2);


        System.out.println("История после запроса :");
        printHistory(historyManager);

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(task2);
        historyManager.add(epic2);

        System.out.println("История после запроса 1:");
        printHistory(historyManager);


        historyManager.remove(1);

        System.out.println("История после удаления задачи с id=1:");
        printHistory(historyManager);

        // Удаляем эпик с тремя подзадачами
        historyManager.remove(22);

        System.out.println("История после удаления эпика с id=22:");
        printHistory(historyManager);
    }

    public static void printHistory(HistoryManager historyManager) {

        ArrayList<Task> tasksList = (ArrayList<Task>) historyManager.getHistory();
        for (Task task : tasksList) {
            System.out.println(task.getId() + ": " + task.getName() + " " + task.getDescription());
        }

        System.out.println();
    }
}