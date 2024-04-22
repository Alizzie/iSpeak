package com.example.ispeak.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.BoDySSheet;
import com.example.ispeak.R;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BoDySMainCriteriaAdapter extends RecyclerView.Adapter<BoDySMainCriteriaAdapter.ViewHolder> {

    private BoDyS assessment;
    private Context context;
    private List<String> mainKeys;
    private int openDropdownBox;
    private RecyclerView recyclerView;

    public BoDySMainCriteriaAdapter(BoDyS assessment, RecyclerView recyclerView, Context context) {
        this.assessment = assessment;
        this.mainKeys = assessment.getCurrentSheet().getMainCriteriaList().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public BoDySMainCriteriaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BoDySMainCriteriaAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bodys_frequency, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.criteriaTxt.setText(context.getString(R.string.boDySFrequencyMainCriteriaTitle, position+1, mainKeys.get(position)));
        holder.setFrequencyBar(mainKeys.get(position));

        holder.cardViewItem.setOnClickListener(view -> {

            if(openDropdownBox != holder.getAdapterPosition()) {
                notifyItemChanged(openDropdownBox);
                openDropdownBox = holder.getAdapterPosition();
            }

            toggleDownDropCriteria(holder);
            recyclerView.smoothScrollToPosition(openDropdownBox);
        });

        initFrequencyObservationRecyclerView(holder, mainKeys.get(position));
        initDropDownCriteria(holder);

    }

    private void initDropDownCriteria(ViewHolder holder) {
        holder.subCriteriaRecyclerView.setVisibility(View.GONE);

        Drawable arrowDown = ResourcesCompat.getDrawable(context.getResources(), R.drawable.keyboard_arrow_down_24, null);
        holder.displaySubCriteriaArrow.setImageDrawable(arrowDown);
        holder.displaySubCriteriaArrow.setTag("arrow down");
    }

    private void toggleDownDropCriteria(ViewHolder holder){
        Drawable arrowDown = ResourcesCompat.getDrawable(context.getResources(), R.drawable.keyboard_arrow_down_24, null);
        Drawable arrowUp = ResourcesCompat.getDrawable(context.getResources(), R.drawable.keyboard_arrow_up_24, null);

        if(holder.displaySubCriteriaArrow.getTag().equals("arrow down")) {
            holder.displaySubCriteriaArrow.setImageDrawable(arrowUp);
            holder.displaySubCriteriaArrow.setTag("arrow up");
            holder.subCriteriaRecyclerView.setVisibility(View.VISIBLE);
        } else{
            holder.displaySubCriteriaArrow.setImageDrawable(arrowDown);
            holder.displaySubCriteriaArrow.setTag("arrow down");
            holder.subCriteriaRecyclerView.setVisibility(View.GONE);
        }
    }

    private void initFrequencyObservationRecyclerView(ViewHolder holder, String mainCriteria){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context.getApplicationContext());
        holder.subCriteriaRecyclerView.setHasFixedSize(true);
        holder.subCriteriaRecyclerView.setLayoutManager(layoutManager);

        HashMap<String, Integer> criteria = assessment.getCurrentSheet().getBoDySCriteria().get(mainCriteria);
        BoDySCriteriaAdapter adapter = new BoDySCriteriaAdapter(assessment, mainCriteria, criteria, context.getApplicationContext());
        holder.subCriteriaRecyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return mainKeys.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardViewItem;
        TextView criteriaTxt;
        GridLayout frequencyBar;
        RecyclerView subCriteriaRecyclerView;
        ImageView displaySubCriteriaArrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewItem = itemView.findViewById(R.id.cardViewItem);
            criteriaTxt = itemView.findViewById(R.id.criteriaText);
            frequencyBar = itemView.findViewById(R.id.frequencyBar);
            subCriteriaRecyclerView = itemView.findViewById(R.id.subCriteriaRecyclerView);
            displaySubCriteriaArrow = itemView.findViewById(R.id.displaySubCriteriaArrow);
        }

        protected void setFrequencyBar(String mainCriteria){
            int childCount = frequencyBar.getChildCount();

            for (int i = 0; i < childCount; i++) {
                View childView = frequencyBar.getChildAt(i);
                BoDySSheet sheet = assessment.getBoDySSheets()[i];

                if(sheet.getStatus().isEvaluated() && hasMarking(sheet, mainCriteria)) {
                    childView.setBackgroundColor(context.getColor(R.color.darkGrayBlue));
                } else if (sheet.getStatus().isSkipped()) {
                    childView.setBackgroundColor(context.getColor(R.color.colorAccent));
                } else {
                    childView.setBackground(context.getDrawable(R.drawable.quadrat_with_border));
                }
            }
        }

        private boolean hasMarking(BoDySSheet sheet, String mainCriteria){
            HashMap<String, Integer> markings = sheet.getBoDySCriteria().get(mainCriteria);
            return markings != null && markings.containsValue(1);
        }
    }
}
