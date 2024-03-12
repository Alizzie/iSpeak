package com.example.ispeak.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ispeak.Interfaces.EventLabelingObserver;
import com.example.ispeak.Models.Event;
import com.example.ispeak.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private List<Event> eventList;
    private Context context;
    private int checkPosition;
    private boolean deleteMode = false;
    private EventLabelingObserver observer;

    public EventAdapter(List<Event> eventList, Context context, EventLabelingObserver observer) {
        this.eventList = eventList;
        this.context = context;
        this.checkPosition = 0;
        this.observer = observer;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void toggleDeleteMode(){
        deleteMode = !deleteMode;
        notifyItemRangeChanged(0, getItemCount());
    }

    public void notifyAdapterItemInserted(int eventId) {
        notifyItemRangeChanged(0, getItemCount());
        checkPosition = eventId;
        notifyItemInserted(eventId);
        notifyListInsertedObserver(eventId);
    }

    private void deleteItem(int position) {
        if (position >= 0 && position < eventList.size()) {
            notifyListRemovedObserver(position);
            notifyItemRemoved(position);

            if (position == checkPosition) {
                checkPosition = RecyclerView.NO_POSITION;
            } else if (position < checkPosition) {
                checkPosition--;
            }
        }
    }

    private void notifyListRemovedObserver(int position) {
        observer.onListItemRemoved(position);
    }
    private void notifyListInsertedObserver(int position) {
        observer.onListItemInserted(position);
    }

    public void setDeleteMode(boolean mode){
        deleteMode = mode;
    }
    public boolean isDeleteMode(){return deleteMode;}

    public int getCheckPosition(){
        return checkPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bodys_event, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.eventNr.setText(holder.itemView.getContext().getString(R.string.eventNr, position + 1));
        holder.eventLabels.setText(eventList.get(position).getEventLabels());
        holder.eventTimestamp.setText(String.valueOf(eventList.get(position).getTimeStart()));

        holder.setDeleteBtnVisibility(deleteMode);
        holder.tickItem();

        holder.eventItem.setOnClickListener(view -> {
            if(checkPosition != holder.getAdapterPosition()) {
                notifyItemChanged(checkPosition);
                checkPosition = holder.getAdapterPosition();
            }

            if(!deleteMode){
                holder.tickItem();
            }

            observer.onEventClick(position);
        });

        holder.eventDeleteBtn.setOnClickListener(view -> {
            if(deleteMode){
                deleteItem(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventNr, eventLabels, eventTimestamp;
        ImageView eventDeleteBtn;
        CardView eventItem;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            eventNr = itemView.findViewById(R.id.eventNr);
            eventLabels = itemView.findViewById(R.id.eventLabels);
            eventTimestamp = itemView.findViewById(R.id.eventTimestamp);
            eventDeleteBtn = itemView.findViewById(R.id.eventDeleteBtn);
            eventItem = itemView.findViewById(R.id.eventItem);
        }

        private void tickItem(){
            if(checkPosition == RecyclerView.NO_POSITION) {
                return;
            }
            if(deleteMode) {
                eventItem.setBackgroundColor(context.getColor(R.color.white));
                return;
            }

            if(checkPosition == getAdapterPosition()) {
                eventItem.setBackgroundColor(context.getColor(R.color.lightBlue));
            }
            else {
                eventItem.setBackgroundColor(context.getColor(R.color.white));
            }
        }

        private void setDeleteBtnVisibility(boolean deleteMode){
            if(deleteMode && eventList.size() != 0){
                eventDeleteBtn.setVisibility(View.VISIBLE);
            } else{
                eventDeleteBtn.setVisibility(View.GONE);
            }
        }
    }
}
