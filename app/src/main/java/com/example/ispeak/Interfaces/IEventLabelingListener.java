package com.example.ispeak.Interfaces;

public interface IEventLabelingListener {
    void onListItemRemoved(int position);
    void onListItemInserted(int position);
    void onEventClick(int position);
    void onCategoryClick();
}
