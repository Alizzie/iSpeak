package com.example.ispeak.Utils;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.ispeak.Interfaces.NotesObserver;
import com.example.ispeak.Models.Assessment;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.R;

public class BoDySNotesPopup extends PopupWindow {

    TextView title;
    String criteria;
    EditText userText;
    NotesObserver observer;
    public BoDySNotesPopup(NotesObserver observer) {
        super();
        this.observer = observer;
    }

    public void showPopup(Context context, View view, String criteria, String oldNotes) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popup = inflater.inflate(R.layout.popup_notes, null);

        this.criteria = criteria;

        PopupWindow popupWindow = initPopupWindow(popup, view);

        title = popupWindow.getContentView().findViewById(R.id.notesTitle);
        title.setText(context.getString(R.string.criteriaNotes, criteria));

        userText = popupWindow.getContentView().findViewById(R.id.notesEditText);
        userText.setText(oldNotes);

        setOnTouchClosePopupListener(popupWindow);
        blurBackground(popupWindow, context);

        popupWindow.setOutsideTouchable(false);
    }

    private PopupWindow initPopupWindow(View popup, View btnView){
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popup, width, height);

        popupWindow.showAtLocation(btnView, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    private void blurBackground(PopupWindow popupWindow, Context context){
        View container = (View) popupWindow.getContentView().getParent();
        container.setOnTouchListener((v, event) -> true);

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
        String note = userText.getText().toString();
        observer.onSaveNote(criteria, note);
        popupWindow.dismiss();
        return true;
    }
}
