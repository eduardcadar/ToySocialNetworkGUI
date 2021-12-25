package com.toysocialnetworkgui.repository;

import com.toysocialnetworkgui.domain.Event;

import java.util.List;

public interface EventRepository {
    void save(Event e) throws RepoException;
    Event getEvent(String name) throws RepoException;
    void remove(String name) throws RepoException;
    int size();
    void clear();
    List<Event> getAll();
    boolean isEmpty();
    void update(Event event);
}
