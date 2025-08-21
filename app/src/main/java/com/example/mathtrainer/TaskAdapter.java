package com.example.mathtrainer;

import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnSubmitAnswerListener {
        void onSubmitAnswer(int position, @NonNull String input);
    }

    private static final int VIEW_TASK   = 1;
    private static final int VIEW_FOOTER = 2;

    private final List<Task> items;
    private final OnSubmitAnswerListener listener;

    private String footerResult = "Баллы: 0 — Время: 00:00";
    private String footerCoeff  = "Коэффициент: 0,000";

    public TaskAdapter(List<Task> items, OnSubmitAnswerListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateFooter(String resultLine, String coeffLine) {
        footerResult = resultLine;
        footerCoeff = coeffLine;
        notifyItemChanged(getItemCount() - 1);
    }

    @Override public int getItemViewType(int position) {
        return (position == getItemCount() - 1) ? VIEW_FOOTER : VIEW_TASK;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_FOOTER) {
            View v = inf.inflate(R.layout.item_result, parent, false);
            return new FooterVH(v);
        } else {
            View v = inf.inflate(R.layout.item_task, parent, false);
            return new TaskVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_FOOTER) {
            FooterVH f = (FooterVH) holder;
            f.resultText.setText(footerResult);
            f.coeffText.setText(footerCoeff);
        } else {
            TaskVH h = (TaskVH) holder;
            Task t = items.get(position);
            h.problem.setText(t.getText());
            h.answer.setText("");
            h.answer.setFilters(new InputFilter[]{new SignedNumberFilter()});
            h.submit.setOnClickListener(v -> {
                if (listener != null) {
                    int pos = h.getAdapterPosition();

                    listener.onSubmitAnswer(pos, h.answer.getText().toString());
                }
            });
        }
    }

    @Override public int getItemCount() { return items.size() + 1; } // + футер

    // --- holders ---
    static class TaskVH extends RecyclerView.ViewHolder {
        TextView problem;
        EditText answer;
        Button submit;
        TaskVH(@NonNull View itemView) {
            super(itemView);
            problem = itemView.findViewById(R.id.taskProblem);
            answer  = itemView.findViewById(R.id.answerInput);
            submit  = itemView.findViewById(R.id.submitBtn);
        }
    }

    static class FooterVH extends RecyclerView.ViewHolder {
        TextView resultText, coeffText;
        FooterVH(@NonNull View itemView) {
            super(itemView);
            resultText = itemView.findViewById(R.id.resultText);
            coeffText  = itemView.findViewById(R.id.coeffText);
        }
    }

    // только цифры и минус
    static class SignedNumberFilter implements InputFilter {
        @Override public CharSequence filter(CharSequence src, int start, int end, Spanned dst, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                char c = src.charAt(i);
                if (!(Character.isDigit(c) || c == '-')) return "";
            }
            return null;
        }
    }
}
