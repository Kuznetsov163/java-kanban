

package tracker.controllers;


import tracker.exceptions.ManagerSaveException;
import tracker.model.Status;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.model.TaskType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryManager {
    private final File file;
    private static final String HEADER_CSV_FILE = "id,type,name,status,description,epicId\n";

    public FileBackedTasksManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file)  {
        FileBackedTasksManager manager = new FileBackedTasksManager(new InMemoryHistoryManager(), file);
        try {

            manager.restoreFromFile();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return manager;
    }

    private void restoreFromFile() throws ManagerSaveException {
        int maxTaskId;
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            reader.readLine(); // заголовок
            String line;

            maxTaskId = 0;
            while ((line = reader.readLine()) != null) {

                if (line.equals(HEADER_CSV_FILE)) {

                    continue;
                }

                try {

                    String[] parts = line.split(",");
                    int id = Integer.parseInt(parts[0]);
                    if (id > maxTaskId) {
                        maxTaskId = id;
                    }
                    String type = parts[1];
                    String name = parts[2];
                    Status status = Status.valueOf(parts[3].toUpperCase());
                    String description = parts[4];
                    int epicId = (parts.length == 6) ? Integer.parseInt(parts[5]) : -1;

                    if (TaskType.EPIC.toString().equals(type)) {
                        Epic epic = new Epic(name, description, id);
                        epic.setId(id);
                        addEpic(epic);
                    } else if (TaskType.SUBTASK.toString().equals(type)) {
                        Subtask subtask = new Subtask(name, description, id, getEpicById(epicId));
                        subtask.setId(id);
                        addSubtask(subtask, getEpicById(epicId));
                    } else {
                        Task task = new Task(name, description, id);
                        task.setStatus(status);
                        addTask(task);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка идентификатора в файле CSV: " + e.getMessage());

                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла: " + e.getMessage(), e);
        }
        taskIdCounter = maxTaskId + 1;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADER_CSV_FILE);

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : getEpics()) {
                writer.write(toString(epic) + "\n");
                for (Subtask subtask : epic.getSubtasks()) {
                    writer.write(toString(subtask) + "\n");
                }
            }

            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    private String getParentEpicId(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            if (subtask.getEpic() != null) {
                return Integer.toString(subtask.getEpic().getId());
            }
        }
        return "";
    }

    private TaskType getType(Task task) {
        if (task instanceof Epic) return TaskType.EPIC;
        else if (task instanceof Subtask) return TaskType.SUBTASK;
        else return TaskType.TASK;
    }

    private String toString(Task task) {
        String[] toJoin = {Integer.toString(task.getId()), getType(task).toString(), task.getName(),
                task.getStatus().toString(), task.getDescription(), getParentEpicId(task)};
        return String.join(",", toJoin);
    }

    public Task fromString(String value) {
        String[] params = value.split(",");
        int id = Integer.parseInt(params[0]);
        String type = params[1];
        String name = params[2];
        Status status = Status.valueOf(params[3].toUpperCase());
        String description = params[4];
        int epicId = (params.length == 6) ? Integer.parseInt(params[5]) : -1;

        if (TaskType.EPIC.toString().equals(type)) {
            Epic epic = new Epic(name, description, id);
            epic.setId(id);
            return epic;
        } else if (TaskType.SUBTASK.toString().equals(type)) {
            Subtask subtask = new Subtask(name, description, id, getEpicById(epicId));
            subtask.setId(id);
            return subtask;
        } else {
            Task task = new Task(name, description, id);
            task.setStatus(status);
            return task;
        }
    }

    public static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder str = new StringBuilder();

        if (history.isEmpty()) {
            return "";
        }

        for (Task task : history) {
            str.append(task.getId()).append(",");
        }

        if (str.length() != 0) {
            str.deleteCharAt(str.length() - 1);
        }

        return str.toString();
    }

     public static List<Integer> historyFromString(String value) {
        List<Integer> toReturn = new ArrayList<>();
        if (value != null) {
            String[] id = value.split(",");
            for (String number : id) {
                toReturn.add(Integer.parseInt(number));
            }
        }
        return toReturn;
    }


     @Override
     public Task updateTask(Task updatedTask) {
        Task task = super.updateTask(updatedTask);
        save();

        return task;
    }

     @Override
     public Epic updateEpic(Epic updatedEpic) {
        Epic epic = super.updateEpic(updatedEpic);
        save();

        return epic;
    }

     @Override
     public Subtask updateSubtask(Subtask updatedSubtask) {
        Subtask subtask = super.updateSubtask(updatedSubtask);
        save();

        return subtask;
    }

     @Override
     public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

     @Override
     public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

     @Override
     public void deleteSubtaskById(int subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save();
    }

     @Override
     public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

     @Override
     public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

     @Override
     public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }



    @Override
    public Task addTask(Task newTask) {
        newTask.setId(taskIdCounter);
        tasks.put(taskIdCounter, newTask);
        taskIdCounter++;
        save();

        return newTask;
    }

    @Override
    public Epic addEpic(Epic newEpic) {
        newEpic.setId(taskIdCounter);
        epics.put(newEpic.getId(), newEpic);
        taskIdCounter++;
        save();

        return newEpic;
    }

    @Override
    public Subtask addSubtask(Subtask newSubtask, Epic epic) {
        newSubtask.setId(taskIdCounter);
        epic.addSubtask(newSubtask);
        subtasks.put(taskIdCounter, newSubtask);
        taskIdCounter++;
        save();

        return newSubtask;
    }
}