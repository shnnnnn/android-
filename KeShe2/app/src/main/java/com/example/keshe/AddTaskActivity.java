package com.example.keshe;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.keshe.entity.Task;
import com.example.keshe.viewmodel.TaskViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDescription;
    private RadioGroup rgCategory, rgPriority;
    private Button btnSelectDate, btnSave;
    private TextView tvSelectedDate;
    private CheckBox cbRecurring;
    private Spinner spinnerRecurrence;

    private TaskViewModel taskViewModel;
    private Calendar selectedCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // ===== Toolbar 返回键 =====
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        // =========================

        initViews();
        setupListeners();

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        selectedCalendar = Calendar.getInstance();
    }

    private void initViews() {
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        rgCategory = findViewById(R.id.rg_category);
        rgPriority = findViewById(R.id.rg_priority);
        btnSelectDate = findViewById(R.id.btn_select_date);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        cbRecurring = findViewById(R.id.cb_recurring);
        spinnerRecurrence = findViewById(R.id.spinner_recurrence);
        btnSave = findViewById(R.id.btn_save);

        ((RadioButton) findViewById(R.id.rb_work)).setChecked(true);
        ((RadioButton) findViewById(R.id.rb_medium)).setChecked(true);

        spinnerRecurrence.setAdapter(
                new android.widget.ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        new String[]{"每天", "每周", "每月", "每年"}
                )
        );
    }

    private void setupListeners() {
        btnSelectDate.setOnClickListener(v -> showDateTimePicker());

        cbRecurring.setOnCheckedChangeListener((buttonView, isChecked) ->
                spinnerRecurrence.setVisibility(isChecked ? View.VISIBLE : View.GONE)
        );

        btnSave.setOnClickListener(v -> saveTask());
    }

    private void showDateTimePicker() {
        Calendar now = Calendar.getInstance();

        new DatePickerDialog(this, (view, year, month, day) -> {
            selectedCalendar.set(year, month, day);

            new TimePickerDialog(this, (timeView, hour, minute) -> {
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hour);
                selectedCalendar.set(Calendar.MINUTE, minute);
                selectedCalendar.set(Calendar.SECOND, 0);
                tvSelectedDate.setText(
                        android.text.format.DateFormat.format(
                                "yyyy-MM-dd HH:mm", selectedCalendar
                        )
                );
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();

        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "请输入任务标题", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(etDescription.getText().toString().trim());

        int catId = rgCategory.getCheckedRadioButtonId();
        task.setCategory(catId == R.id.rb_work ? "工作" :
                catId == R.id.rb_study ? "学习" : "生活");

        int priId = rgPriority.getCheckedRadioButtonId();
        task.setPriority(priId == R.id.rb_low ? 1 :
                priId == R.id.rb_medium ? 2 : 3);

        task.setDueDate(selectedCalendar != null ? selectedCalendar.getTime() : new Date());

        if (cbRecurring.isChecked()) {
            task.setRecurring(true);
            task.setRecurrencePattern(spinnerRecurrence.getSelectedItem().toString());
        }

        taskViewModel.insert(task);
        Toast.makeText(this, "任务已保存", Toast.LENGTH_SHORT).show();
        finish();
    }
}