package tracker.controllers;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;



import java.util.List;
    public interface Manager {

        // Методы для добавления задач, эпиков и подзадач
        Task addTask(Task newTask);
        Epic addEpic(Epic newEpic);
        Subtask addSubtask(Subtask newSubtask, Epic epic);

        // Методы для обновления задач, подзадач и эпиков
        Task updateTask(Task updatedTask);
        Subtask updateSubtask(Subtask updatedSubtask);
        Epic updateEpic(Epic updatedEpic);

        // Методы для удаления задач, подзадач и эпиков
        void deleteTaskById(int taskId);
        void deleteEpicById(int epicId);
        void deleteSubtaskById(int subtaskId);

        // Методы для получения задач, подзадач и эпиков по идентификатору
        Subtask getSubtaskById(int subtaskId);
        Epic getEpicById(int epicId);
        Task getTaskById(int taskId);

        // Методы для получения всех задач, подзадач и эпиков
        List<Task> getAllTasks();
        List<Subtask> getSubtasks();
        List<Epic> getEpics();

        // Методы для установки статуса подзадачи и обновления статуса эпика
        void setStatusForSubtask(Subtask subtask, Status status);
        void updateEpicStatus(Epic epic);

        // Методы для получения всех подзадач для эпика
        List<Subtask> getAllSubtasksForEpic(Epic epic);

        // Методы для удаления всех задач, подзадач и эпиков
        void deleteAllTasks();
        void deleteAllEpics();
        void deleteAllSubtasks();

        // Метод история
        List<Task> getHistory();
    }

