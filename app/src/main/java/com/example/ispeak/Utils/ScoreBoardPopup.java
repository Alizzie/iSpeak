package com.example.ispeak.Utils;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.ispeak.Interfaces.ScoreBoardObserver;
import com.example.ispeak.Models.Assessment;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.R;
import com.google.android.material.button.MaterialButton;

public class ScoreBoardPopup extends PopupWindow {

    private ScoreBoardObserver observer;

    public ScoreBoardPopup(ScoreBoardObserver observer) {
        super();
        this.observer = observer;
    }

    public void showPopup(Context context, View view, String criteria) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popup = inflater.inflate(R.layout.popup_scoreboard, null);

        PopupWindow popupWindow = initPopupWindow(popup, view);
        blurBackground(popupWindow, context);
        setOnClickScoreBtnListener(popupWindow, criteria);
        setOnTouchClosePopupListener(popupWindow);
    }


    private void setOnClickScoreBtnListener(PopupWindow popupWindow, String mainCriteria){
        GridLayout scoreBoard = popupWindow.getContentView().findViewById(R.id.scoreBoardPopup);
        for (int i = 0; i < scoreBoard.getChildCount(); i++) {
            MaterialButton scoreBtn = (MaterialButton) scoreBoard.getChildAt(i);

            scoreBtn.setOnClickListener(view -> {
                MaterialButton btn = (MaterialButton) view;
                int score = Integer.parseInt(btn.getText().toString());
                observer.onScoreBoardClicked(mainCriteria, score);
                dismissPopupWindow(popupWindow);
            });
        }
    }

    private PopupWindow initPopupWindow(View scorePopupView, View btnView){
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(scorePopupView, width, height, focusable);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.showAtLocation(btnView, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    private void blurBackground(PopupWindow popupWindow, Context context){
        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, p);
    }

    private void setOnTouchClosePopupListener(PopupWindow popupWindow){
        popupWindow.getContentView().findViewById(R.id.popupCloseBtn).setOnTouchListener((v, e) ->  dismissPopupWindow(popupWindow));
    }

    private boolean dismissPopupWindow(PopupWindow popupWindow){
        popupWindow.dismiss();
        return true;
    }
}
