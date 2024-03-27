package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.model.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> tasksMap;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager()  {
        this.tasksMap = new HashMap<>();
        this.head = null;
        this.tail = null;
    }

    private static class Node {  // класс Node сделал внутренним
        private Task task;
        private Node prev;
        private Node next;

        public Task getTask() {
            return task;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public void setTask(Task task) {
            this.task = task;
        }
    }


    private void removeNode(Node node) {

        if (node != null) {
            tasksMap.remove(node.getTask().getId());
            Node prev = node.getPrev();
            Node next = node.getNext();

            if (head == node) {
                head = node.getNext();
            }
            if (tail == node) {
                tail = node.getPrev();
            }

            if (prev != null) {
                prev.setNext(next);
            }

            if (next != null) {
                next.setPrev(prev);
            }
        }
    }

    private void linkLast(Task task) {
        Node  element = new Node();
        element.setTask(task);

        if (tasksMap.containsKey(task.getId())) {
            removeNode(tasksMap.get(task.getId()));
        }

        if (head == null) {
            tail = element;
            head = element;
            element.setNext(null);
            element.setPrev(null);
        } else {
            element.setPrev(tail);
            element.setNext(null);
            tail.setNext(element);
            tail = element;
        }

        tasksMap.put(task.getId(), element);
    }

    @Override
    public void add(Task task) {
        int taskId = task.getId();
        if (tasksMap.containsKey(taskId)) {
            Node existingNode = tasksMap.get(taskId);
            removeNode(existingNode);
        }
        linkLast(task);
        tasksMap.put(taskId, tail);
    }

    @Override
    public void remove(int id) { // исправил внутреннюю структуру
        if (tasksMap.containsKey(id)) {
            Node nodeToRemove = tasksMap.get(id);
            Task taskToRemove = nodeToRemove.getTask();


            if (taskToRemove.getType() == TaskType.EPIC) {
                Epic epicToRemove = (Epic) taskToRemove;

                List<Subtask> subtasksToRemove = epicToRemove.getSubtasks();
                for (Subtask subtask : subtasksToRemove) {
                    remove(subtask.getId());
                }
            } else if (taskToRemove.getType() == TaskType.SUBTASK) {
                Subtask subtaskToRemove = (Subtask) taskToRemove;
                Epic epics = subtaskToRemove.getEpic();
                epics.removeSubtask(subtaskToRemove.getId());
            }

            removeNode(nodeToRemove);
        }
    }

     @Override
    public List<Task> getHistory() {  // полностью согласен с совместимостью метода getTasks
         List<Task> result = new ArrayList<>();
         Node element = head;
         while (element != null) {
             result.add(element.getTask());
             element = element.getNext();
         }
         return result;
     }
}
