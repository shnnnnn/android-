package com.example.keshe.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.keshe.entity.Task;
import com.example.keshe.repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository repository;

    // UI 永远观察这个
    private final MediatorLiveData<List<Task>> tasks = new MediatorLiveData<>();
    private LiveData<List<Task>> source;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        loadAllTasks();
    }

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    private void switchSource(LiveData<List<Task>> newSource) {
        if (source != null) {
            tasks.removeSource(source);
        }
        source = newSource;
        tasks.addSource(source, tasks::setValue);
    }

    // ===== 分类 / 排序 =====

    public void loadAllTasks() {
        switchSource(repository.getAllTasks());
    }

    public void loadTasksByStatus(int status) {
        switchSource(repository.getTasksByStatus(status));
    }

    public void loadTasksByCategory(String category) {
        switchSource(repository.getTasksByCategory(category));
    }

    public void loadTasksByPriority() {
        switchSource(repository.getTasksOrderByPriority());
    }

    public void loadTasksByCategoryAndPriority(String category) {
        switchSource(repository.getTasksByCategoryOrderByPriority(category));
    }

    // ===== CRUD =====

    public void insert(Task task) {
        repository.insert(task);
    }

    public void update(Task task) {
        repository.update(task);
    }

    public void delete(Task task) {
        repository.delete(task);
    }
}