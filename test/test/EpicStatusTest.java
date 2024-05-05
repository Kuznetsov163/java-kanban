package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicStatusTest {
    private Epic epic;

    @BeforeEach
    void setUp() {
        epic = new Epic("Test Epic", "Test Description",1);
    }

    @Test
    void allSubtasksNew() {
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(60),epic);
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description 2", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(60),epic);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        epic.updateStatus();

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void allSubtasksDone() {
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", Status.DONE, LocalDateTime.now(), Duration.ofMinutes(60),epic);
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description 2", Status.DONE, LocalDateTime.now(), Duration.ofMinutes(60),epic);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        epic.updateStatus();

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void mixSubtasks() {
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(60),epic);
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description 2", Status.DONE, LocalDateTime.now(), Duration.ofMinutes(60),epic);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        epic.updateStatus();

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void inProgressSubtasks() {
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(60),epic);
        Subtask subtask2 = new Subtask(2, "Subtask 2", "Description 2", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(60),epic);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        epic.updateStatus();

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}
 /*
 . Все подзадачи со статусом NEW.
 . Все подзадачи со статусом DONE.
 . Подзадачи со статусами NEW и DONE.
 . Подзадачи со статусом IN_PROGRESS
  */