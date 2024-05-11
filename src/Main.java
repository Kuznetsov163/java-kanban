import tracker.controllers.HttpTaskManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.server.HttpTaskServer;
import tracker.server.KVServer;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {


    public static void main(String[] args) throws IOException {

        new KVServer().start();
        HttpTaskManager manager = new HttpTaskManager("http://localhost:8070");
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();


        Task task1 = new Task(1, "Задача 1",
                "Задача 1", Status.NEW,
                LocalDateTime.of(2024, Month.APRIL, 1, 12, 0), Duration.ofMinutes(15));

        manager.addTask(task1);

        Task task2 = new Task(2, "Задача 2",
                "Задача 2", Status.NEW,
                LocalDateTime.of(2024, Month.APRIL, 1, 12, 0), Duration.ofMinutes(9));

        manager.addTask(task2);

        Epic epic1 = new Epic("Эпик1", "Описание эпика",3);
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask(4, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW,LocalDateTime.of(2024, Month.APRIL, 2, 15, 0), Duration.ofMinutes(24),epic1);


        Subtask subtask2 = new Subtask(5, "Подзадача 2", "Описание подзадачи 2",
                Status.DONE,LocalDateTime.of(2024, Month.APRIL, 3, 12, 30), Duration.ofMinutes(15),epic1);


        Subtask subtask3 = new Subtask(6, "подзадача 3", "Описание подзадачи 3",
                Status.IN_PROGRESS,LocalDateTime.of(2024, Month.MAY, 5, 13, 0), Duration.ofMinutes(9),epic1);


        Epic epic2 = new Epic(7, "Эпик2", "эпик",LocalDateTime.of(2024, Month.MAY, 5, 14, 0), Duration.ofMinutes(9));
        manager.addEpic(epic2);


        Task task3 = new Task("задача", "Описание задачи", 8);


        manager.addTask(task3);


        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
       // manager.getSubtaskById(4);
       // manager.getSubtaskById(5);
       // manager.getSubtaskById(6);
        manager.getEpicById(7);
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getTaskById(1);
        manager.getTaskById(1);
        manager.getTaskById(8);

        manager.load();
        server.stop();
    }

}