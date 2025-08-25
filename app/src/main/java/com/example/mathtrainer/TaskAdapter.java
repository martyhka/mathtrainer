package com.example.mathtrainer;

import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskVH> {

    public interface OnAnswerListener {
        void onCorrectAnswered(int position);
        void onWrongAnswered(int position);
    }

    private final List<Task> items;
    private final OnAnswerListener listener;

    public TaskAdapter(@NonNull List<Task> items, OnAnswerListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public Task getItem(int position) { return items.get(position); }

    public void addItem(Task task) {
        items.add(task);
        notifyItemInserted(items.size() - 1);
    }

    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) return;
        if (fromPosition < 0 || fromPosition >= items.size()) return;
        if (toPosition < 0 || toPosition > items.size()) return;

        Task t = items.remove(fromPosition);
        items.add(toPosition, t);
        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(toPosition);
    }

    @NonNull
    @Override
    public TaskVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskVH(v, listener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskVH holder, int position) {
        boolean isActive = position == getItemCount() - 1; // активное поле ввода только у последней позиции
        holder.bind(items.get(position), isActive);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TaskVH extends RecyclerView.ViewHolder {
        private final TextView questionTv;
        private final EditText answerEt;

        TaskVH(@NonNull View itemView, OnAnswerListener listener, TaskAdapter adapter) {
            super(itemView);
            questionTv = findFirstTextView(itemView);
            answerEt   = findFirstEditText(itemView);

            if (questionTv != null) {
                questionTv.setTextColor(Color.WHITE);
                questionTv.setBackground(null);
                questionTv.setTextSize(28f);
            }

            if (answerEt != null) {
                answerEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                answerEt.setImeOptions(EditorInfo.IME_ACTION_DONE);
                answerEt.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override public void afterTextChanged(Editable s) { answerEt.setError(null); }
                });

                answerEt.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE ||
                            (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                                    event.getAction() == KeyEvent.ACTION_UP)) {

                        int pos = getAdapterPosition();
                        if (pos == RecyclerView.NO_POSITION) return true;

                        Task t = adapter.getItem(pos);
                        String txt = answerEt.getText().toString().trim();
                        if (txt.isEmpty()) { answerEt.setError("Введите ответ"); return true; }

                        try {
                            int user = Integer.parseInt(txt);
                            if (user == t.getAnswer()) {
                                if (listener != null) listener.onCorrectAnswered(pos);
                                answerEt.setText("");
                            } else {
                                if (listener != null) listener.onWrongAnswered(pos);
                                answerEt.setError("Неверно");
                            }
                        } catch (NumberFormatException e) {
                            answerEt.setError("Только числа");
                        }
                        return true;
                    }
                    return false;
                });
            }
        }

        void bind(Task t, boolean isActive) {
            if (questionTv != null) {
                questionTv.setText(t.getText());
            }
            if (answerEt != null) {
                if (isActive) {
                    answerEt.setVisibility(View.VISIBLE);
                    answerEt.setEnabled(true);
                    answerEt.setFocusable(true);
                    answerEt.setFocusableInTouchMode(true);
                    if (answerEt.getText().length() == 0) answerEt.requestFocus();
                } else {
                    answerEt.setText("");
                    answerEt.setError(null);
                    answerEt.setEnabled(false);
                    answerEt.setFocusable(false);
                    answerEt.setVisibility(View.GONE);
                }
            }
        }

        private static TextView findFirstTextView(View root) {
            if (root instanceof TextView) return (TextView) root;
            Deque<View> stack = new ArrayDeque<>();
            stack.push(root);
            while (!stack.isEmpty()) {
                View v = stack.pop();
                if (v instanceof TextView) return (TextView) v;
                if (v instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) v;
                    for (int i = 0; i < group.getChildCount(); i++)
                        stack.push(group.getChildAt(i));
                }
            }
            return null;
        }

        private static EditText findFirstEditText(View root) {
            if (root instanceof EditText) return (EditText) root;
            Deque<View> stack = new ArrayDeque<>();
            stack.push(root);
            while (!stack.isEmpty()) {
                View v = stack.pop();
                if (v instanceof EditText) return (EditText) v;
                if (v instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) v;
                    for (int i = 0; i < group.getChildCount(); i++)
                        stack.push(group.getChildAt(i));
                }
            }
            return null;
        }
    }
}