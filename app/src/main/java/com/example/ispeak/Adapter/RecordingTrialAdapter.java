package com.example.ispeak.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ispeak.Models.Recording;
import com.example.ispeak.R;

import java.util.List;
public class RecordingTrialAdapter extends RecyclerView.Adapter<RecordingTrialAdapter.ViewHolder> {

    List<Recording> trialList;
    Context context;
    int checkPosition;

    public RecordingTrialAdapter(List<Recording> trialList, Context context) {
        this.trialList = trialList;
        this.context = context;

        checkPosition = trialList.size() == 0? 0 : trialList.size() - 1;
    }

    public int getCheckPosition(){
        return checkPosition;
    }

    public Recording getCheckedTrial(){
        if(trialList.size() == 0){
            return null;
        }
        return trialList.get(checkPosition);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recording_trial, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.trialLabelTxt.setText(context.getString(R.string.trialNumber, position+1));
        holder.patientTime.setText(context.getString(R.string.patientRecordingTime, trialList.get(position).getFormattedPatientTime()));
        holder.totalTime.setText(context.getString(R.string.totalRecordingTime, trialList.get(position).getFormattedTotalTime()));
        holder.numEvents.setText(String.valueOf(trialList.get(position).getEvents().size()));

//        if (checkPosition == holder.getAdapterPosition()) {
//            holder.tickItem();
//        }
//
//        holder.trialItem.setOnClickListener(view -> holder.tickItem());

        holder.tickItem();
        holder.trialItem.setOnClickListener(view -> {

            if(checkPosition != holder.getAdapterPosition()) {
                notifyItemChanged(checkPosition);
                checkPosition = holder.getAdapterPosition();
            }

            holder.tickItem();

        });
    }

    @Override
    public int getItemCount() {
        return trialList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView trialLabelTxt, patientTime, totalTime, numEvents;
        ImageView trialSelected;
        ConstraintLayout trialItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trialLabelTxt = itemView.findViewById(R.id.trialLabelTxt);
            patientTime = itemView.findViewById(R.id.patientTime);
            totalTime = itemView.findViewById(R.id.totalTimeTxt);
            numEvents = itemView.findViewById(R.id.numEvents);
            trialSelected = itemView.findViewById(R.id.trialSelection);
            trialItem = itemView.findViewById(R.id.recordingTrialInfo);
        }

        private void tickItem() {

            if(checkPosition == -1){
                trialSelected.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.outline_circle_24, null));

            }else{
                if(checkPosition == getAdapterPosition()) {
                    trialSelected.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.check_circle_outline_24, null));
                }
                else {
                    trialSelected.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.outline_circle_24, null));
                }
            }

//            String notSelected = itemView.getResources().getString(R.string.trialNotSelected);
//            String selected = itemView.getResources().getString(R.string.trialSelected);
//
//            if (trialSelected.getContentDescription().equals(notSelected)) {
//                trialSelected.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.check_circle_outline_24, null));
//                trialSelected.setContentDescription(selected);
//            } else {
//                trialSelected.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.outline_circle_24, null));
//                trialSelected.setContentDescription(notSelected);
//            }
        }
    }
}
