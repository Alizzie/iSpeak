package com.example.ispeak.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ispeak.Models.Assessment;
import com.example.ispeak.R;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.List;
import java.util.Set;

public abstract class SimpleCheckboxAdapter  extends RecyclerView.Adapter<SimpleCheckboxAdapter.ViewHolder>{

    protected Assessment assessment;
    private List<String> options;
    private Set<String> checkedOptions;

    public SimpleCheckboxAdapter(Assessment assessment, List<String> options, Set<String> checkedOptions) {
        this.assessment = assessment;
        this.options = options;
        this.checkedOptions = checkedOptions;
    }

    public void addCheckedOptions(String option){
        checkedOptions.add(option);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimpleCheckboxAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_checkbox_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = options.get(position);
        holder.itemCheckbox.setText(text);
        holder.setCustomOnCheckedChangeListener(text);

        if(checkedOptions.contains(text)) {
            holder.itemCheckbox.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialCheckBox itemCheckbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCheckbox = itemView.findViewById(R.id.itemCheckbox);
        }

        private void setCustomOnCheckedChangeListener(String text) {
            itemCheckbox.setOnCheckedChangeListener((view, listener) -> addCheckboxListener(text, itemCheckbox.isChecked()));
        }
    }

    protected abstract void addCheckboxListener(String newInput, boolean isChecked);
}
