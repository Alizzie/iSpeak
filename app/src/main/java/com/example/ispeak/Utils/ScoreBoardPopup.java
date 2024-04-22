package com.example.ispeak.Utils;

import android.content.Context;
import android.view.View;
import android.widget.GridLayout;
import android.widget.PopupWindow;

import com.example.ispeak.Interfaces.IScoreBoardListener;
import com.example.ispeak.R;
import com.google.android.material.button.MaterialButton;

public class ScoreBoardPopup extends CustomPopupView {

    private IScoreBoardListener listener;

    public ScoreBoardPopup(IScoreBoardListener listener) {
        super(true);
        this.listener = listener;
    }

    public void showPopup(Context context, View view, String criteria) {
        PopupWindow popupWindow = initAndGetPopup(context, view, R.layout.popup_scoreboard);

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
                listener.onScoreBoardClicked(mainCriteria, score);
                dismissPopupWindow(popupWindow);
            });
        }
    }

    private void setOnTouchClosePopupListener(PopupWindow popupWindow){
        popupWindow.getContentView().findViewById(R.id.popupCloseBtn).setOnTouchListener((v, e) ->  dismissPopupWindow(popupWindow));
    }
}
