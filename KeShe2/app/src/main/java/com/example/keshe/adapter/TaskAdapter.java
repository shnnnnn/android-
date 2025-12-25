package com.example.keshe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keshe.R;
import com.example.keshe.entity.Task;
import com.example.keshe.viewmodel.TaskViewModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private TaskViewModel taskViewModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onEditClick(Task task);
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public void setViewModel(TaskViewModel viewModel) {
        this.taskViewModel = viewModel;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);

        holder.tvTitle.setText(task.getTitle());
        holder.tvDescription.setText(task.getDescription());
        holder.tvCategory.setText(task.getCategory());

        if (task.getDueDate() != null) {
            holder.tvDueDate.setText(dateFormat.format(task.getDueDate()));
        } else {
            holder.tvDueDate.setText("无截止时间");
        }

        // 设置状态
        String statusText = "";
        int statusColor = 0;
        switch (task.getStatus()) {
            case 0:
                statusText = "未开始";
                statusColor = android.R.color.darker_gray;
                break;
            case 1:
                statusText = "进行中";
                statusColor = R.color.blue;
                break;
            case 2:
                statusText = "已完成";
                statusColor = R.color.green;
                break;
        }
        holder.tvStatus.setText(statusText);
        holder.tvStatus.setBackgroundResource(statusColor);

        // 设置优先级颜色
        int priorityColor = R.color.gray;
        switch (task.getPriority()) {
            case 1:
                priorityColor = R.color.green;
                break;
            case 2:
                priorityColor = R.color.yellow;
                break;
            case 3:
                priorityColor = R.color.red;
                break;
        }
        holder.priorityIndicator.setBackgroundResource(priorityColor);

        // 设置分类颜色
        int categoryColor = R.color.gray;
        switch (task.getCategory()) {
            case "工作":
                categoryColor = R.color.blue;
                break;
            case "学习":
                categoryColor = R.color.green;
                break;
            case "生活":
                categoryColor = R.color.orange;
                break;
        }
        holder.tvCategory.setBackgroundResource(categoryColor);

        // 完成按钮点击事件
        holder.btnComplete.setOnClickListener(v -> {
            if (taskViewModel != null) {
                int newStatus = task.getStatus() == 2 ? 0 : 2;
                task.setStatus(newStatus);
                taskViewModel.update(task);
            }
        });

        // 删除按钮点击事件
        holder.btnDelete.setOnClickListener(v -> {
            if (taskViewModel != null) {
                taskViewModel.delete(task);
            }
        });

        // 点击任务项
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(task);
            }
        });

        // 长按编辑
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(task);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return tasks == null ? 0 : tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvCategory, tvDueDate, tvStatus;
        View priorityIndicator;
        ImageButton btnComplete, btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvDueDate = itemView.findViewById(R.id.tv_due_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            priorityIndicator = itemView.findViewById(R.id.priority_indicator);
            btnComplete = itemView.findViewById(R.id.btn_complete);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}