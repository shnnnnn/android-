package com.example.keshe.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.keshe.database.AppDatabase;
import com.example.keshe.database.TaskDao;
import com.example.keshe.entity.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {

    private final TaskDao taskDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        taskDao = db.taskDao();
    }

    public LiveData<List<Task>> getAllTasks() {
        return taskDao.getAllTasks();
    }

    public LiveData<List<Task>> getTasksByStatus(int status) {
        return taskDao.getTasksByStatus(status);
    }

    public LiveData<List<Task>> getTasksByCategory(String category) {
        return taskDao.getTasksByCategory(category);
    }

    public LiveData<List<Task>> getTasksOrderByPriority() {
        return taskDao.getTasksOrderByPriority();
    }

    public LiveData<List<Task>> getTasksByCategoryOrderByPriority(String category) {
        return taskDao.getTasksByCategoryOrderByPriority(category);
    }

    public void insert(Task task) {
        executor.execute(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        executor.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        executor.execute(() -> taskDao.delete(task));
    }
}