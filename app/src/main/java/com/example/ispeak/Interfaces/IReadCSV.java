package com.example.ispeak.Interfaces;

import com.example.ispeak.Models.Event;

public interface IReadCSV {
    Event onReadEventsCSV(String[] line, int eventCount, int taskId);
}
