package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.service.model.TimeEntry;

import java.util.List;
import java.util.UUID;

public interface TimeEntryService {

    TimeEntry createTimeEntry(UUID taskId, TimeEntry timeEntry);

    List<TimeEntry> getTimeEntries(UUID taskId);

    TimeEntry getTimeEntryById(UUID id);

    TimeEntry updateTimeEntry(UUID id, TimeEntry timeEntry);

    void deleteTimeEntry(UUID id);
}
