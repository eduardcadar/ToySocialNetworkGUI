package com.toysocialnetworkgui.repository.observer;

import java.util.ArrayList;
import java.util.List;

public class Observable {
    private List<Observer> observers = new ArrayList<>();

    /**
     * Adds an observer
     * @param obs observer
     */
    public void addObserver(Observer obs) { observers.add(obs); }

    /**
     * Removes an observer
     * @param obs observer
     */
    public void removeObserver(Observer obs) { observers.remove(obs); }

    /**
     * Removes all observers
     */
    public void removeObservers() { observers.clear(); }

    /**
     * Notifies the observers
     */
    public void notifyObservers() { observers.forEach(obs -> obs.update(this)); }
}
