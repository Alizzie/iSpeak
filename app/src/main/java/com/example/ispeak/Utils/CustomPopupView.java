package com.example.ispeak.Utils;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class CustomPopupView extends PopupWindow {

    protected boolean focusable;
    CustomPopupView(boolean focusable) {
        this.focusable = focusable;
    }
    protected PopupWindow initPopupWindow(View popup, View btnView){
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow;
        if(focusable) {
            popupWindow = new PopupWindow(popup, width, height, focusable);
            popupWindow.setBackgroundDrawable(null);
        } else {
            popupWindow = new PopupWindow(popup, width, height);
        }

        popupWindow.showAtLocation(btnView, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    protected void blurBackground(PopupWindow popupWindow, Context context){
        View container = (View) popupWindow.getContentView().getParent();
        container.setOnTouchListener((v, event) -> true);

        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, p);
    }

    protected PopupWindow initAndGetPopup(Context context, View view, int resource) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popup = inflater.inflate(resource, null);

        PopupWindow popupWindow = initPopupWindow(popup, view);
        blurBackground(popupWindow, context);
        return popupWindow;
    }

    protected boolean dismissPopupWindow(PopupWindow popupWindow){
        popupWindow.dismiss();
        return true;
    }
}
