package com.example.ispeak.Adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ispeak.Interfaces.IEventLabelingListener;
import com.example.ispeak.Models.Event;
import com.example.ispeak.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{
    private List<String> categoryList;
    private Event event;
    private Context context;
    private IEventLabelingListener observer;
    private boolean readOnly;

    public CategoryAdapter(List<String> categoryList, Context context, Event event, IEventLabelingListener observer, boolean readOnly){
        this.categoryList = categoryList;
        this.context = context;
        this.event = event;
        this.observer = observer;
        this.readOnly = readOnly;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.categoryItem.setText(categoryList.get(position));

        if(event == null){
            return;
        } else {
            holder.setUpLabels();
        }

        if(!readOnly){
            holder.categoryItemWrapper.setOnClickListener(view -> {
                ColorDrawable viewColor = (ColorDrawable) view.getBackground();
                int currentColor = viewColor.getColor();
                int clickedColor = context.getColor(R.color.lightBlue);
                String category = holder.categoryItem.getText().toString();

                if(clickedColor == currentColor){
                    view.setBackgroundColor(context.getColor(R.color.white));
                    event.removeEventLabel(category);
                } else{
                    view.setBackgroundColor(context.getColor(R.color.lightBlue));
                    event.addEventLabel(category);
                }

                observer.onCategoryClick();
            });
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryItem;
        LinearLayout categoryItemWrapper;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryItem = itemView.findViewById(R.id.categoryListItem);
            categoryItemWrapper = itemView.findViewById(R.id.categoryListItemWrapper);
        }

        private void setUpLabels(){
            if(event.getEventLabelsList().contains(categoryItem.getText())) {
                categoryItemWrapper.setBackgroundColor(context.getColor(R.color.lightBlue));
            }
        }

    }
}
