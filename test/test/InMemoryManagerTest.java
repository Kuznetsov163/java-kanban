package test;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.controllers.Manager;
import tracker.controllers.HistoryManager;
import tracker.controllers.InMemoryManager;
import tracker.controllers.InMemoryHistoryManager;
import java.time.Duration;
import java.time.LocalDateTime;
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
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30),epic);
        Subtask subtask2 = new Subtask(1, "Subtask 1", "Description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30),epic);
        assertEquals(subtask1.getId(), subtask2.getId(), "Подзадачи с одинаковым идентификатором должны быть равными");
    }

    // проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
    @Test
    public void testAddEpicToItself() {
        Epic epic = new Epic("Epic 1", "Description", 1);

        Subtask subtask = new Subtask(1, "Subtask 1", "Description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30),epic);
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

        assertNotEquals(addedSubtask, addedSubtask.getEpic(), "Subtask не может быть своим собственным Epic");
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

    // обновление статусов
    @Test
    void testEpicStatusDone() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        Subtask subtask = new Subtask("Subtask 1", "1", 1, epic);
        subtask.setStatus(Status.DONE);
        manager.addSubtask(subtask, epic);
        manager.addEpic(epic);
        assertEquals(Status.DONE, epic.getStatus());
    }


    @Test
    void testEpicStatusInProgress() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 1, epic);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 2, epic);
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.IN_PROGRESS);
        manager.addSubtask(subtask1, epic);
        manager.addSubtask(subtask2, epic);
        manager.addEpic(epic);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void testEpicStatusNew() {
        Epic epic = new Epic("Epic 1", "Description 1", 1);
        manager.addEpic(epic);
        assertEquals(Status.NEW, epic.getStatus());
    }

    // спринт 6


    //убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test

    public void testHistoryManager() {

        Task task = new Task("Task 1", "Description 1", 1);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.contains(task));
    }



    // Удаляемые подзадачи не должны хранить внутри себя старые id.
    @Test

    public void testRemoveSubtaskFromEpic() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        Subtask subtask = new Subtask(2, "Subtask 1", "Description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30),epic);
        epic.addSubtask(subtask);

        epic.removeSubtask(2);
        assertFalse(epic.getSubtasks().contains(subtask), "Подзадача не должна присутствовать в эпике после удаления");
    }

    // Внутри эпиков не должно оставаться неактуальных id подзадач.
    @Test
    public void testEpicSubtaskIdsAfterRemoval() {
        Epic epic = new Epic("Epic 1", "Description", 1);
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30),epic);
        epic.addSubtask(subtask1);

        epic.removeSubtask(2);
        assertFalse(epic.getSubtasks().contains(subtask1), "Epic не должен содержать удаленную подзадачу по старому идентификатору.");
    }

    // С помощью сеттеров экземпляры задач позволяют изменить любое своё поле,
    // но это может повлиять на данные внутри менеджера.
    @Test
    public void testTaskFieldChangeNotAffectingManagerData() {
        Task task = new Task("Task 1", "Description 1", 1);
        manager.addTask(task);

        Task retrievedTask = manager.getTaskById(1);
        retrievedTask.setName("Updated Task Name");

        Task updatedTask = manager.getTaskById(1);

        assertEquals(retrievedTask.getName(), updatedTask.getName(), "Изменение поля задачи должно повлиять на данные менеджера.");
    }

    // Проверяем, что изменение в оригинальной задаче не отразилось на задаче в менеджере
    @Test
    void testTaskSetterDoesNotAffectManagerData() {
        Task task = new Task("Task 1","g",1);
        manager.addTask(task);

        Task originalTask = new Task("Task 1","g",1);

        task.setName("Updated Task 1");

        assertNotEquals(originalTask.getName(), manager.getTaskById(task.getId()).getName());
        assertEquals("Task 1", originalTask.getName());
    }

    // Проверяем, что задача добавлена в историю

    @Test
    public void testAddTaskToHistory() {
        Task task = new Task("task", "Test Task",1);
        historyManager.add(task);

        assertTrue(historyManager.getHistory().contains(task));
    }

    //  Проверяем, что после удаления задачи из истории ее размер стал равен 0.

    @Test
    public void testRemoveTaskFromHistory() {
        Task task = new Task("task", "Test Task",1);
        historyManager.add(task);
        historyManager.remove(1);
        assertEquals(0, historyManager.getHistory().size());
    }
    //  Проверяем, что после удаления эпика из истории размер истории равен 0.

    @Test
    public void testRemoveEpicFromHistory() {
        Epic epic = new Epic("epic", "Test Epic",2);
        Subtask subtask = new Subtask(3, "Subtask 1", "Description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30),epic);
        epic.addSubtask(subtask);

        historyManager.add(epic);
        historyManager.add(subtask);

        historyManager.remove(2);

        assertEquals(0, historyManager.getHistory().size());
    }

    // дублирование
    @Test
    void testDuplicateAdd() {
        Task task = new Task("Task 1", "Description 1", 1);
        historyManager.add(task);
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
    }

}
