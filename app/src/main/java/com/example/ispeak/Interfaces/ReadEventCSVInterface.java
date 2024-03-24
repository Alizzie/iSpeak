package com.example.ispeak.Interfaces;

import com.example.ispeak.Models.Event;

public interface ReadEventCSVInterface {
    Event onReadCSV(String[] line, int eventCount, int taskId);
}
