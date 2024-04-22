package com.example.ispeak.Utils;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.ispeak.Interfaces.INotesListener;
import com.example.ispeak.R;

public class BoDySNotesPopup extends CustomPopupView {

    private TextView title;
    private String criteria;
    private EditText userText;
    private INotesListener observer;
    private boolean readOnly;
    public BoDySNotesPopup(INotesListener observer, boolean readOnly) {
        super(false);
        this.observer = observer;
        this.readOnly = readOnly;
    }

    public void showPopup(Context context, View view, String criteria, String oldNotes) {
        PopupWindow popupWindow = super.initAndGetPopup(context, view, R.layout.popup_notes);
        this.criteria = criteria;

        title = popupWindow.getContentView().findViewById(R.id.notesTitle);
        title.setText(context.getString(R.string.criteriaNotes, criteria));

        userText = popupWindow.getContentView().findViewById(R.id.notesEditText);
        userText.setText(oldNotes);

        setOnTouchClosePopupListener(popupWindow);
        popupWindow.setOutsideTouchable(false);

        if(readOnly){
            userText.setClickable(false);
            userText.setEnabled(false);
        } else {
            setOnTouchResetPopupListener(popupWindow);
            setOnTouchConfirmBtn(popupWindow);
        }
    }

    private void setOnTouchClosePopupListener(PopupWindow popupWindow){
        popupWindow.getContentView().findViewById(R.id.popupCloseBtn).setOnTouchListener((v, e) ->  dismissPopupWindow(popupWindow));
    }

    private void setOnTouchResetPopupListener(PopupWindow popupWindow) {
        popupWindow.getContentView().findViewById(R.id.popupResetBtn).setOnTouchListener((v, e) -> resetEditText());
    }

    private void setOnTouchConfirmBtn(PopupWindow popupWindow) {
        popupWindow.getContentView().findViewById(R.id.popupConfirmBtn).setOnTouchListener((v, e) -> saveEditText(popupWindow));
    }

    private boolean resetEditText(){
        String emptyString = "";
        userText.setText(emptyString);
        observer.onSaveNote(criteria, emptyString);
        return true;
    }

    private boolean saveEditText(PopupWindow popupWindow){
        String note = userText.getText().toString();
        observer.onSaveNote(criteria, note);
        popupWindow.dismiss();
        return true;
    }
}
