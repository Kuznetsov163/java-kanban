package test;


import org.junit.jupiter.api.*;
import tracker.controllers.FileBackedTasksManager;
import tracker.controllers.InMemoryHistoryManager;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest {

    private FileBackedTasksManager manager;
    private Path filePath;

    @BeforeEach
    void setUp() throws IOException {
        Path dataPath = Paths.get(System.getProperty("user.dir"), "data");
        Files.createDirectories(dataPath);
        filePath = Paths.get(System.getProperty("user.dir"), "data", "data.csv");
        manager = new FileBackedTasksManager(new InMemoryHistoryManager(), filePath.toFile());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(filePath);
    }

    //сохранение загрузки пустого файла
    @Test
    void saveLoadEmptyFile() throws IOException {
        Path filePath = Paths.get(System.getProperty("user.dir"), "data", "data.csv").toAbsolutePath();
        Files.deleteIfExists(filePath);
        Files.createFile(filePath);

        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(new File(filePath.toString()));
        assertTrue(loadedManager.getAllTasks().isEmpty());
    }

    // сохранение задачи
    @Test
    void saveLoadTasks()  {
        Task task1 = new Task("Task 1", "Description 1", 1);
        Task task2 = new Task("Task 2", "Description 2", 2);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.save();

        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(new File(filePath.toString()));
        List<Task> tasks = loadedManager.getAllTasks();

        assertEquals("Task 1", tasks.get(0).getName());
        assertEquals("Description 2", tasks.get(1).getDescription());
    }

    // Загрузить эпики и подзадачи

    @Test
    void saveLoadEpicsAndSubtasks()  {
        Epic epic = new Epic(1, "Epic 1", "Epic Description 1",  LocalDateTime.now(), Duration.ofMinutes(10));
        Subtask subtask = new Subtask(2, "Subtask 1", "Description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30), epic);

        manager.addEpic(epic);
        manager.addSubtask(subtask, epic);
        manager.save();

        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(new File(filePath.toString()));
        List<Epic> epics = loadedManager.getEpics();
        List<Subtask> subtasks = epics.get(0).getSubtasks();

        assertEquals(1, epics.size());
        assertEquals(1, subtasks.size());
        assertEquals("Epic 1", epics.get(0).getName());
        assertEquals("Subtask 1", subtasks.get(0).getName());
    }

    // обновление /сохранение

    @Test
    void updateTaskUpdatesAndSaves() {
        Task task = new Task("Task 1", "Description 1", 1);
        manager.addTask(task);

        Task updatedTask = new Task("Task 1", "Description 1", 1);
        Task returnedTask = manager.updateTask(updatedTask);

        assertEquals("Task 1", returnedTask.getName());
        assertEquals("Description 1", returnedTask.getDescription());


        manager.save();


        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(new File(filePath.toString()));
        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals("Task 1", tasks.get(0).getName());
        assertEquals("Description 1", tasks.get(0).getDescription());
    }

    // выброс исключения
    @Test
    public void testException() {
        assertThrows(ArithmeticException.class, () -> {
            int a = 10 / 0;
        }, "Деление на ноль должно приводить к исключению");
    }

    // выброс исключения
    @Test
    public void testOverlappingIntervals() {
        Task task1 = new Task("Task1", "Description1", 1);
        task1.setStartTime(LocalDateTime.now().plusMinutes(10));
        task1.setDuration(Duration.ofMinutes(30));

        Task task2 = new Task("Task2", "Description2", 2);
        task2.setStartTime(LocalDateTime.now().plusMinutes(20));
        task2.setDuration(Duration.ofMinutes(30));

        manager.addTask(task1);
        manager.addTask(task2);

        Assertions.assertThrows(Exception.class, manager::checkingTheIntersection);
    }

    // Создание задач с непересекающимися временными интервалами, проверка на исключения
    @Test
    public void testNonOverlappingIntervals() {


        LocalDateTime start1 = LocalDateTime.of(2023, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2023, 1, 1, 10, 0);
        Task task1 = new Task(1, "Task1", "Description1", Status.NEW, start1, Duration.ofMinutes(60));

        LocalDateTime start2 = LocalDateTime.of(2023, 1, 1, 10, 30);
        LocalDateTime end2 = LocalDateTime.of(2023, 1, 1, 11, 30);
        Task task2 = new Task(2, "Task2", "Description2", Status.NEW, start2, Duration.ofMinutes(60));


        manager.addTask(task1);
        manager.addTask(task2);


        assertDoesNotThrow(() -> manager.checkingTheIntersection());
    }


    // исправления теста
    @DisplayName("Пересечения по времени, продолжительность")
    @Test
    public void testDuration() {
        Epic epic = new Epic("Epic 1", "Epic Description", 1);
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", Status.NEW,
                LocalDateTime.parse("2024-05-02T11:00"), Duration.ofMinutes(15), epic);
        Subtask subtask2 = new Subtask(3, "Subtask 1", "Description 1", Status.NEW,
                LocalDateTime.parse("2024-05-01T12:00"), Duration.ofMinutes(30), epic);
        manager.addEpic(epic);
        manager.addSubtask(subtask1, epic);
        manager.addSubtask(subtask2, epic);

        assertEquals(2,epic.getSubtasks().size(),"Должно быть 2 подзадачи");

        assertEquals(subtask2.getStartTime(), epic.getStartTime(),
                "Начало эпика - начало ранней подзадачи");
        assertEquals(subtask1.getEndTime(), epic.getEndTime(),
                "Завершение эпика - завершение последней подзадачи");
        assertEquals(subtask1.getDuration().plus(subtask2.getDuration()),
                epic.getDuration(),
                "Длительности эпика - сумма длительностей подзадач");

        Subtask subtask4 = new Subtask(4, "Subtask 1", "Description 1", Status.NEW,
                LocalDateTime.parse("2024-05-02T11:00"), Duration.ofMinutes(30), epic);

        Assertions.assertThrows(RuntimeException.class, () -> manager.addSubtask(subtask4, epic));
    }
}