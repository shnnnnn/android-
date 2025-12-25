package com.example.keshe.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.keshe.entity.Task;

import java.util.Date;
import java.util.List;

@Dao
public interface TaskDao {

    // ===== 基础 CRUD =====

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    // ===== 全部任务（按时间）=====
    @Query("SELECT * FROM tasks ORDER BY due_date ASC")
    LiveData<List<Task>> getAllTasks();

    // ===== 状态分类 =====
    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY due_date ASC")
    LiveData<List<Task>> getTasksByStatus(int status);

    // ===== 性质分类 =====
    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY due_date ASC")
    LiveData<List<Task>> getTasksByCategory(String category);

    // ===== 排序：按优先级 =====
    @Query("SELECT * FROM tasks ORDER BY priority DESC")
    LiveData<List<Task>> getTasksOrderByPriority();

    // ===== 分类 + 排序 =====
    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY priority DESC")
    LiveData<List<Task>> getTasksByCategoryOrderByPriority(String category);

    // ===== 其他 =====
    @Query("SELECT * FROM tasks WHERE id = :id")
    Task getTaskById(int id);

    @Query("UPDATE tasks SET status = :status WHERE id = :id")
    void updateTaskStatus(int id, int status);

    @Query("SELECT * FROM tasks WHERE due_date BETWEEN :start AND :end")
    LiveData<List<Task>> getTasksBetweenDates(Date start, Date end);
}