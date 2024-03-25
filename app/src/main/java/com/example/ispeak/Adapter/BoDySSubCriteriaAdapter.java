package com.example.ispeak.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.BoDySSheet;
import com.example.ispeak.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BoDySSubCriteriaAdapter extends RecyclerView.Adapter<BoDySSubCriteriaAdapter.ViewHolder> {

    private String mainCriteria;
    private HashMap<String, Integer> criteria;
    private List<String> criteriaStrings;
    private BoDyS assessment;
    private Context context;

    public BoDySSubCriteriaAdapter(BoDyS assessment, String mainCriteria, HashMap<String, Integer> criteria, Context context) {
        this.mainCriteria = mainCriteria;
        this.criteria = criteria;
        this.assessment = assessment;
        this.context = context;
        this.criteriaStrings = new ArrayList<>(criteria.keySet());
        this.criteriaStrings = criteriaStrings.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

    @NonNull
    @Override
    public BoDySSubCriteriaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BoDySSubCriteriaAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bodys_small_frequency, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String criteriaShortcut = criteriaStrings.get(position);
        int resourceId = context.getResources().getIdentifier(criteriaShortcut, "string", context.getPackageName());
        holder.criteriaText.setText(context.getString(resourceId));
        holder.setFrequencyBar(criteriaShortcut);
    }

    @Override
    public int getItemCount() {
        return criteriaStrings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView criteriaText;
        GridLayout frequencyBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            criteriaText = itemView.findViewById(R.id.criteriaText);
            frequencyBar = itemView.findViewById(R.id.frequencyBar);
        }

        protected void setFrequencyBar(String criteria){
            int childCount = frequencyBar.getChildCount();

            for (int i = 0; i < childCount; i++) {
                View childView = frequencyBar.getChildAt(i);
                BoDySSheet sheet = assessment.getBoDySSheets()[i];

                if(!sheet.isPrefill() && hasMarking(sheet, criteria)) {
                    childView.setBackgroundColor(context.getColor(R.color.darkBlue));
                } else {
                    childView.setBackground(context.getDrawable(R.drawable.quadrat_with_border_lightblue));
                }
            }
        }

        private boolean hasMarking(BoDySSheet sheet, String criteria){
            int marking = sheet.getBoDySCriteria().get(mainCriteria).get(criteria);
            return marking == 1;
        }
    }
}