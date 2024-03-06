package tests;

import org.junit.jupiter.api.Test;
import tracker.controllers.HistoryManager;
import tracker.controllers.InMemoryHistoryManager;
import tracker.controllers.InMemoryManager;
import tracker.controllers.Manager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class InMemoryManagerTest {
    private Manager manager;
    private HistoryManager historyManager;
    @BeforeEach
    public void setUp() {
        manager = new InMemoryManager(historyManager);
        historyManager = new InMemoryHistoryManager();


    }
    //проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    public void testTaskEqualityById() {
        Task task1 = new Task("Task 1", "Description 1", 1);
        Task task2 = new Task("Task 2", "Description 2", 1);
        assertEquals(task1, task2, "Задачи с одинаковым идентификатором должны быть равными");
    }

    //проверьте, что наследники класса Task равны друг другу, если равен их id;
    @Test
    public void testSubtaskEqualityById() {
        Epic epic = new Epic("Epic 1", "Epic Description", 1);
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description 1", 1, epic);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description 2", 1, epic);
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым идентификатором должны быть равны");
    }

    // проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
    @Test
    public void testAddEpicToItself() {
        Epic epic = new Epic("Epic 1", "Description", 1);

        Subtask subtask = new Subtask("Subtask 1", "Description", 2, epic);
        epic.addSubtask(subtask);

        assertFalse(epic.getSubtasks().contains(epic));
    }

    // проверьте, что объект Subtask нельзя сделать своим же эпиком;
    @Test
    public void testSetSubtaskAsEpic() {


        Task task = new Task("Task 1", "Description 1", 1);
        Epic epic = new Epic("Epic 1", "Epic Description 1", 2);

        manager.addTask(task);
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask Description 1", 3, epic);
        Subtask addedSubtask = manager.addSubtask(subtask, epic);

        assertEquals(epic, addedSubtask.getEpic());
        addedSubtask.setEpic(addedSubtask.getEpic());

        assertNotEquals(addedSubtask, addedSubtask.getEpic());
    }


    //убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
    @Test
    public void testManagerInitialization() {
        assertNotNull(manager);
    }

    //проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    @Test
    public void testInMemoryManager() {
        Task task = new Task("Task 1", "Description 1", 1);

        Task addedTask = manager.addTask(task);
        Task foundTask = manager.getTaskById(1);

        assertEquals(task, addedTask);
        assertEquals(task, foundTask);
    }
    //проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    @Test
    public void testTaskIdUniqueness() {

        Task task1 = new Task("Task 1", "Description 1", 1);
        Task task2 = new Task("Task 2", "Description 2", 1);

        Task addedTask1 = manager.addTask(task1);
        Task addedTask2 = manager.addTask(task2);

        assertNotEquals(task1.getId(), task2.getId());
    }
    //создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    public void testTaskImmutability() {

        Task task = new Task("Task 1", "Description 1", 1);
        Task originalTask = new Task(task.getName(), task.getDescription(), task.getId());

        Task addedTask = manager.addTask(task);
        assertEquals(originalTask, task);
    }

    //убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    public void testHistoryManager() {



        Task task = new Task("Task 1", "Description 1", 1);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.contains(task));
    }
}