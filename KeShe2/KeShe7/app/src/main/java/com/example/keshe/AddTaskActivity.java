package com.example.keshe;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.example.keshe.entity.Task;
import com.example.keshe.notification.ReminderScheduler;
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
    private Spinner spinnerRemindOffset;
    private Button btnSelectStartDate;
    private TextView tvSelectedStartDate;

    private TaskViewModel taskViewModel;
    private Calendar selectedCalendar;
    private Calendar selectedStartCalendar;
    private boolean isEditMode = false;
    private int editingTaskId = -1;
    private Task editingTask;

    private static final int REQ_SELECT_START_DATE = 2001;
    private static final int REQ_SELECT_DUE_DATE = 2002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // 检查是否是编辑模式
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("task_id")) {
            isEditMode = true;
            editingTaskId = intent.getIntExtra("task_id", -1);

            // 设置标题为"编辑任务"
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("编辑任务");
            }
        }


        // ===== Toolbar 返回键 =====
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        // =========================
        initViews();
        setupListeners();

        taskViewModel = new TaskViewModel(getApplication());
        selectedCalendar = Calendar.getInstance();
        selectedStartCalendar = Calendar.getInstance();

        // 如果是编辑模式，加载任务数据
        if (isEditMode && editingTaskId != -1) {
            loadTaskData(editingTaskId);
        }
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
        spinnerRemindOffset = findViewById(R.id.spinner_remind_offset);
        btnSelectStartDate = findViewById(R.id.btn_select_start_date);
        tvSelectedStartDate = findViewById(R.id.tv_selected_start_date);
        btnSave = findViewById(R.id.btn_save);

        // 设置默认选中（只有在新增模式才设置）
        if (!isEditMode) {
            ((RadioButton) findViewById(R.id.rb_work)).setChecked(true);
            ((RadioButton) findViewById(R.id.rb_medium)).setChecked(true);
        }

        // 初始化Spinner
        initSpinnerData();
        initRemindOffsetSpinner();
    }

    private void initSpinnerData() {
        String[] recurrencePatterns = {
                "每天",
                "每周",
                "每月",
                "每年"
        };

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                recurrencePatterns
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRecurrence.setAdapter(adapter);
    }

    private void initRemindOffsetSpinner() {
        String[] offsets = {
                "到期时提醒",
                "提前5分钟",
                "提前10分钟",
                "提前30分钟",
                "提前1小时"
        };
        int[] offsetMinutes = {0, 5, 10, 30, 60};

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                offsets
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRemindOffset.setAdapter(adapter);

        // 默认选“到期时提醒”
        spinnerRemindOffset.setSelection(0);

        // 记录到 view 的 tag，保存时读取
        spinnerRemindOffset.setTag(offsetMinutes);
    }

    private void setupListeners() {
        cbRecurring.setOnCheckedChangeListener((buttonView, isChecked) -> {
            spinnerRecurrence.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        btnSelectStartDate.setOnClickListener(v -> showStartDateTimePicker());
        btnSelectDate.setOnClickListener(v -> showDueDateTimePicker());

        btnSave.setOnClickListener(v -> saveTask());
    }

    private void showStartDateTimePicker() {
        if (selectedStartCalendar == null) {
            selectedStartCalendar = Calendar.getInstance();
        }
        Calendar calendar = selectedStartCalendar;

        // 日期选择器
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedStartCalendar.set(Calendar.YEAR, year);
                    selectedStartCalendar.set(Calendar.MONTH, month);
                    selectedStartCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // 时间选择器
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                selectedStartCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedStartCalendar.set(Calendar.MINUTE, minute);
                                selectedStartCalendar.set(Calendar.SECOND, 0);

                                updateSelectedStartDateText();
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true);
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showDueDateTimePicker() {
        if (selectedCalendar == null) {
            selectedCalendar = Calendar.getInstance();
        }
        Calendar calendar = selectedCalendar;

        // 日期选择器
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, month);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // 时间选择器
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedCalendar.set(Calendar.MINUTE, minute);
                                selectedCalendar.set(Calendar.SECOND, 0);

                                updateSelectedDateText();
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true);
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateSelectedStartDateText() {
        String dateStr = android.text.format.DateFormat.format(
                "yyyy-MM-dd HH:mm", selectedStartCalendar).toString();
        tvSelectedStartDate.setText(dateStr);
    }

    private void updateSelectedDateText() {
        String dateStr = android.text.format.DateFormat.format(
                "yyyy-MM-dd HH:mm", selectedCalendar).toString();
        tvSelectedDate.setText(dateStr);
    }

    private void loadTaskData(int taskId) {
        new Thread(() -> {
            Task task = taskViewModel.getTaskByIdSync(taskId);
            if (task != null) {
                editingTask = task;

                runOnUiThread(() -> {
                    // 填充数据到表单
                    etTitle.setText(task.getTitle());
                    etDescription.setText(task.getDescription());

                    // 设置分类
                    switch (task.getCategory()) {
                        case "工作":
                            ((RadioButton)findViewById(R.id.rb_work)).setChecked(true);
                            break;
                        case "学习":
                            ((RadioButton)findViewById(R.id.rb_study)).setChecked(true);
                            break;
                        case "生活":
                            ((RadioButton)findViewById(R.id.rb_life)).setChecked(true);
                            break;
                    }

                    // 设置优先级
                    switch (task.getPriority()) {
                        case 1:
                            ((RadioButton)findViewById(R.id.rb_low)).setChecked(true);
                            break;
                        case 2:
                            ((RadioButton)findViewById(R.id.rb_medium)).setChecked(true);
                            break;
                        case 3:
                            ((RadioButton)findViewById(R.id.rb_high)).setChecked(true);
                            break;
                    }

                    // 设置开始日期和截止日期
                    if (task.getStartDate() != null) {
                        selectedStartCalendar.setTime(task.getStartDate());
                        updateSelectedStartDateText();
                    }
                    if (task.getDueDate() != null) {
                        selectedCalendar.setTime(task.getDueDate());
                        updateSelectedDateText();
                    }

                    // 设置定期任务
                    if (task.isRecurring()) {
                        cbRecurring.setChecked(true);
                        spinnerRecurrence.setVisibility(View.VISIBLE);

                        // 设置Spinner选中项
                        String pattern = task.getRecurrencePattern();
                        if (pattern != null) {
                            for (int i = 0; i < spinnerRecurrence.getCount(); i++) {
                                if (spinnerRecurrence.getItemAtPosition(i).equals(pattern)) {
                                    spinnerRecurrence.setSelection(i);
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "请输入任务标题", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task;
        if (isEditMode && editingTask != null) {
            // 编辑模式：使用现有任务
            task = editingTask;
            task.setTitle(title);
        } else {
            // 新增模式：创建新任务
            task = new Task();
            task.setTitle(title);
        }

        task.setDescription(etDescription.getText().toString().trim());

        // 设置分类
        int selectedCategoryId = rgCategory.getCheckedRadioButtonId();
        if (selectedCategoryId == R.id.rb_work) {
            task.setCategory("工作");
        } else if (selectedCategoryId == R.id.rb_study) {
            task.setCategory("学习");
        } else if (selectedCategoryId == R.id.rb_life) {
            task.setCategory("生活");
        }

        // 设置优先级
        int selectedPriorityId = rgPriority.getCheckedRadioButtonId();
        if (selectedPriorityId == R.id.rb_low) {
            task.setPriority(1);
        } else if (selectedPriorityId == R.id.rb_medium) {
            task.setPriority(2);
        } else if (selectedPriorityId == R.id.rb_high) {
            task.setPriority(3);
        }

        // 设置开始时间和截止时间
        if (selectedStartCalendar != null) {
            task.setStartDate(selectedStartCalendar.getTime());
        }
        if (selectedCalendar != null) {
            task.setDueDate(selectedCalendar.getTime());
        }

        // 设置定期任务
        if (cbRecurring.isChecked()) {
            task.setRecurring(true);
            task.setRecurrencePattern(spinnerRecurrence.getSelectedItem().toString());
        } else {
            task.setRecurring(false);
            task.setRecurrencePattern(null);
        }

        // 提前提醒分钟数
        int[] offsetMinutes = (int[]) spinnerRemindOffset.getTag();
        int selectedOffset = offsetMinutes[spinnerRemindOffset.getSelectedItemPosition()];
        task.setRemindOffsetMinutes(selectedOffset);

        // 保存到数据库
        if (isEditMode) {
            taskViewModel.update(task);
            // 重新安排提醒
            ReminderScheduler.cancelReminder(this, task);
            ReminderScheduler.scheduleReminder(this, task);
            Toast.makeText(this, "任务已更新", Toast.LENGTH_SHORT).show();
        } else {
            taskViewModel.insert(task);
            // 为新任务安排提醒
            ReminderScheduler.scheduleReminder(this, task);
            Toast.makeText(this, "任务已保存", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

}