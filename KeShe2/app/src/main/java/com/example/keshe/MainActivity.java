package com.example.keshe;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.example.keshe.adapter.TaskAdapter;
import com.example.keshe.viewmodel.TaskViewModel;

public class MainActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar.setNavigationOnClickListener(v ->
                drawerLayout.openDrawer(findViewById(R.id.nav_view)));

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskAdapter.setViewModel(taskViewModel);

        // 只观察一个
        taskViewModel.getTasks().observe(this, taskAdapter::setTasks);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_all_tasks) {
                taskViewModel.loadAllTasks();
            } else if (id == R.id.nav_pending) {
                taskViewModel.loadTasksByStatus(0);
            } else if (id == R.id.nav_completed) {
                taskViewModel.loadTasksByStatus(2);
            } else if (id == R.id.nav_work) {
                taskViewModel.loadTasksByCategory("工作");
            } else if (id == R.id.nav_study) {
                taskViewModel.loadTasksByCategory("学习");
            } else if (id == R.id.nav_life) {
                taskViewModel.loadTasksByCategory("生活");
            } else if (id == R.id.nav_priority) {
                taskViewModel.loadTasksByPriority();
            }

            drawerLayout.closeDrawers();
            return true;
        });

        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddTaskActivity.class)));
    }
}