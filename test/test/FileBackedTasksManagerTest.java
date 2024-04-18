package test;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.controllers.FileBackedTasksManager;
import tracker.controllers.InMemoryHistoryManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        Epic epic = new Epic("Epic 1", "Epic Description 1", 1);
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description 1", 1, epic);

        manager.addEpic(epic);
        manager.addSubtask(subtask, epic);
        manager.save();

        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(new File(filePath.toString()));
        List<Epic> epics = loadedManager.getEpics();
        List<Subtask> subtasks = epics.get(0).getSubtasks();

        assertEquals(1, epics.size());
        assertEquals(1, subtasks.size());
        assertEquals("Epic 1", epics.get(0).getName());
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
}