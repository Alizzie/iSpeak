package fr.thomas.menard.ispeak.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.thomas.menard.ispeak.Interfaces.OnItemClickListener;
import fr.thomas.menard.ispeak.Interfaces.SelectedTrialInterface;
import fr.thomas.menard.ispeak.Interfaces.TrialInterface;
import fr.thomas.menard.ispeak.R;
import fr.thomas.menard.ispeak.Utils.Trial;

public class TrialAdapter extends RecyclerView.Adapter<TrialAdapter.MyViewHolder> {

    List<Trial> trialList;
    String s_score;
    Context context;
    int checkPosition;
    int selected;
    boolean visible = false;

    private TrialInterface listenerTrial = null;
    private SelectedTrialInterface listenerSelected = null;

    public TrialAdapter(List<Trial> trialList, Context context) {
        this.trialList = trialList;
        this.context = context;
        checkPosition = trialList.size()-1;
    }

    public String getScore() {
        return s_score;
    }

    public void setScore(String score) {
        this.s_score = score;
    }

    public int getCheckPosition(){
        return checkPosition;
    }

    public int getSelected(){
        return selected;
    }


    public void onItemTrialClick(TrialInterface listenerTrial){
        this.listenerTrial = listenerTrial;
    }

    public void setSelectedClick(SelectedTrialInterface listenerSelected){ this.listenerSelected=listenerSelected; }
    private OnItemClickListener listener = null;

    public void setOnClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trial, parent, false);
        MyViewHolder holder = new MyViewHolder(view);


        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.bind();
        holder.categorie.setText(trialList.get(position).getCategorie());
        holder.task.setText(trialList.get(position).getTask());
        holder.num.setText(trialList.get(position).getNumber());
        holder.number_compens.setText(trialList.get(position).getNumber_compens());
        holder.sec.setText(trialList.get(position).getSec());
        holder.milisec.setText(trialList.get(position).getMili());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.imTick.setVisibility(View.VISIBLE);
                if(checkPosition != holder.getAdapterPosition())
                {
                    notifyItemChanged(checkPosition);
                    checkPosition = holder.getAdapterPosition();

                }

                if(visible) {
                    visible = false;
                }
                else {
                    visible = true;
                }
                if(listener != null)
                    listener.onItemClick(visible);
                if(listenerTrial != null) {
                    listenerTrial.onItemTrialClick(trialList.get(position), holder.getAdapterPosition());
                }
                if(listenerSelected != null)
                    listenerSelected.onSelectedTrial(holder.getAdapterPosition());
            }

        });

    }

    @Override
    public int getItemCount() {
        return trialList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView categorie, task, side, num, score, number_compens, sec, milisec;
        ImageView imTick;
        ConstraintLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imTick = itemView.findViewById(R.id.imTick);
            layout = itemView.findViewById(R.id.itemCompens);
            categorie = itemView.findViewById(R.id.txt_categorie);
            task = itemView.findViewById(R.id.txt_task);
            number_compens = itemView.findViewById(R.id.txt_compensation);
            num = itemView.findViewById(R.id.txt_id_compensation);
            sec = itemView.findViewById(R.id.txt_time_sec);
            milisec = itemView.findViewById(R.id.txt_time_milisec);

        }

        void bind(){
            if(checkPosition == -1){
                imTick.setVisibility(View.INVISIBLE);

            }else{
                if(checkPosition == getAdapterPosition()) {
                    imTick.setVisibility(View.VISIBLE);
                }
                else {
                    imTick.setVisibility(View.INVISIBLE);
                }

            }
        }

    }
}
