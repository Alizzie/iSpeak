package fr.thomas.menard.ispeak.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.Hold;

import java.util.List;

import fr.thomas.menard.ispeak.Interfaces.OnItemClickListener;
import fr.thomas.menard.ispeak.Interfaces.PostLabelButtonInterface;
import fr.thomas.menard.ispeak.Interfaces.PostLabelTitleInterface;
import fr.thomas.menard.ispeak.R;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHoldder> {

    List<Event> eventList;
    List<String> listPostLabel;
    Context context;
    boolean visible = false;

    public EventAdapter(List<Event> eventList, Context context, List<String> listPostLabel){
        this.eventList = eventList;
        this.context = context;
        this.listPostLabel = listPostLabel;
    }

    private OnItemClickListener listener = null;
    private PostLabelTitleInterface listener1 = null;

    private PostLabelButtonInterface listenerPostLabel = null;

    public void setOnClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setOnTitleListener(PostLabelTitleInterface listener1){
        this.listener1 = listener1;
    }

    public void setOnLabelSelected(PostLabelButtonInterface listenerPostLabel){
        this.listenerPostLabel = listenerPostLabel;
    }

    @NonNull
    @Override
    public MyViewHoldder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_postlabel, parent, false);
        MyViewHoldder holder = new MyViewHoldder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.MyViewHoldder holder, int position) {
        Log.d("TEST",""+ eventList.get(position).getId());
        holder.txtCategorie.setText(eventList.get(position).getCategorie());
        holder.txtTask.setText(eventList.get(position).getTask());
        holder.number_event.setText(String.valueOf(eventList.get(position).getId()));
        holder.timeStart.setText(String.valueOf(eventList.get(position).getTimeStart()));
        holder.timeEnd.setText(String.valueOf(eventList.get(position).getTimeEnd()));
        holder.label.setText(listPostLabel.toString());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(visible) {
                    visible = false;
                }
                else {
                    visible = true;
                }
                if(listener != null)
                    listener.onItemClick(visible);
                if(listener1 != null)
                    listener1.onTitleItemClick(eventList.get(position).getId(),
                            eventList.get(position).getCategorie(),
                            eventList.get(position).getTask());

            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class MyViewHoldder  extends RecyclerView.ViewHolder{

        TextView txtTask, txtCategorie, number_event, label, timeStart, timeEnd;
        ConstraintLayout layout;

        public MyViewHoldder(@NonNull View itemView) {
            super(itemView);

            //postLabel = itemView.getRootView().findViewById(R.id.layoutPostLabel);
            layout = itemView.findViewById(R.id.itemPostLabel);

            number_event = itemView.getRootView().findViewById(R.id.postlabel_txt_compensation);
            txtCategorie = itemView.getRootView().findViewById(R.id.postLabel_txt_categorie);
            txtTask = itemView.getRootView().findViewById(R.id.postLabel_txt_task);
            timeStart = itemView.getRootView().findViewById(R.id.txt_timeStart);
            timeEnd = itemView.getRootView().findViewById(R.id.txt_timeEnd);
            label = itemView.findViewById(R.id.txt_postlabel_precise);
        }
    }
}
