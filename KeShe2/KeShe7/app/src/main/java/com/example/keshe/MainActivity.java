package com.example.keshe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.example.keshe.adapter.TaskAdapter;
import com.example.keshe.entity.Task;
import com.example.keshe.notification.ReminderScheduler;
import com.example.keshe.viewmodel.TaskViewModel;
import com.example.keshe.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TaskViewModel taskViewModel;
    private UserViewModel userViewModel;
    private TaskAdapter taskAdapter;
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;  // 添加Toolbar引用
    private SharedPreferences sharedPreferences;
    private MaterialButtonToggleGroup categoryGroup;

    // 当前状态筛选出来的所有任务（再由右上角分类筛选）
    private List<Task> allCurrentTasks = new ArrayList<>();
    // 右上角当前分类筛选：null 或 "全部" 表示不过滤
    private String currentCategoryFilter = "全部";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 检查是否已登录，如果未登录则跳转到登录界面
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            // 未登录，跳转到登录界面
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        initViews();
        setupToolbar();  // 设置Toolbar
        setupViewModel();
        setupRecyclerView();
        setupNavigationDrawer();
        setupFloatingButton();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        categoryGroup = findViewById(R.id.category_group);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        // 设置Toolbar的菜单按钮点击事件，打开侧边栏
        toolbar.setNavigationOnClickListener(v -> {
            drawerLayout.openDrawer(navigationView);
        });

        // 默认选中“全部”分类按钮
        if (categoryGroup != null) {
            categoryGroup.check(R.id.btn_category_all);
            categoryGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (!isChecked) return;
                if (checkedId == R.id.btn_category_all) {
                    currentCategoryFilter = "全部";
                } else if (checkedId == R.id.btn_category_work) {
                    currentCategoryFilter = "工作";
                } else if (checkedId == R.id.btn_category_study) {
                    currentCategoryFilter = "学习";
                } else if (checkedId == R.id.btn_category_life) {
                    currentCategoryFilter = "生活";
                }
                applyCategoryFilter();
            });
        }
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        userViewModel = new UserViewModel(getApplication());

        // 观察任务数据变化
        taskViewModel.getAllTasks().observe(this, tasks -> {
            updateTaskList(tasks);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter();
        taskAdapter.setViewModel(taskViewModel);
        recyclerView.setAdapter(taskAdapter);

        // 设置点击监听器
        taskAdapter.setOnTaskClickListener(new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClick(Task task) {
                // 点击任务项（可以跳转到详情页）
                showTaskDetails(task);
            }

            @Override
            public void onEditClick(Task task) {
                // 长按编辑任务
                editTask(task);
            }
        });
    }

    private void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_all_tasks) {
                taskViewModel.getAllTasks().observe(this, this::updateTaskList);
            } else if (id == R.id.nav_pending) {
                taskViewModel.getTasksByStatus(0).observe(this, this::updateTaskList);
            } else if (id == R.id.nav_completed) {
                // 已完成视为历史列表：按完成时间倒序
                taskViewModel.getCompletedHistory().observe(this, this::updateTaskList);
            } else if (id == R.id.nav_logout) {
                // 退出登录
                logout();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    // 更新当前任务列表，并应用右上角的分类筛选
    private void updateTaskList(List<Task> tasks) {
        if (tasks == null) {
            allCurrentTasks = new ArrayList<>();
        } else {
            allCurrentTasks = tasks;
        }
        applyCategoryFilter();
    }

    // 根据 currentCategoryFilter 对 allCurrentTasks 进行筛选
    private void applyCategoryFilter() {
        if (allCurrentTasks == null) {
            taskAdapter.setTasks(null);
            return;
        }
        if (currentCategoryFilter == null || "全部".equals(currentCategoryFilter)) {
            taskAdapter.setTasks(allCurrentTasks);
        } else {
            List<Task> filtered = new ArrayList<>();
            for (Task t : allCurrentTasks) {
                if (currentCategoryFilter.equals(t.getCategory())) {
                    filtered.add(t);
                }
            }
            taskAdapter.setTasks(filtered);
        }
    }

    private void logout() {
        // 清除登录信息
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // 清除ViewModel中的当前用户
        userViewModel.logout();

        // 跳转到登录界面
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        
        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
    }

    private void setupFloatingButton() {
        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });
    }

    private void showTaskDetails(Task task) {
        // 可以创建详情页面，这里简单用Toast显示
        Toast.makeText(this, "任务详情: " + task.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void editTask(Task task) {
        // 跳转到编辑页面
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra("task_id", task.getId());  // 传递任务ID
        startActivity(intent);
    }

}