package com.toysocialnetworkgui.repository.observer;

import java.util.ArrayList;
import java.util.List;

public interface Observable {
    List<Observer> observers = new ArrayList<>();

    /**
     * Adds an observer
     * @param obs observer
     */
    default void addObserver(Observer obs) { observers.add(obs); }

    /**
     * Removes an observer
     * @param obs observer
     */
    default void removeObserver(Observer obs) { observers.remove(obs); }

    /**
     * Removes all observers
     */
    default void removeObservers() { observers.clear(); }

    /**
     * Notifies the observers
     */
    default void notifyObservers() { observers.forEach(obs -> obs.update(this)); }

}
