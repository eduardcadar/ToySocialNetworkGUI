package com.toysocialnetworkgui.domain;

import java.time.LocalDateTime;

/**
 * Class that manages an event. I think it should be a subject where users are observers to this object.
 * If it passed a day/(an hour for testing purposes) from the last notification you should notify all users.
 * Cycle through all events at the start of application. Have to extract that ToySocialNetwrokApp main  And notify all users.
 * Last notif for event A -> 13:00
 * User1 logs at 14:05 -> notify all observers
 * User2 logs at 14:30 -> no nothing, he is already notified
 * User3 logs at 15:10 -> notify all
 * Users have to implement the observer interface
 * Events has: - A name, a description, LocalDateTime start, LocalDateTime end, Participants(Observers)
 *  - TODO
 *      - Do something with image, in bd store the path to the image to event
 */
public class Event {
        private String name;
        private String description;
        private LocalDateTime start;
        private LocalDateTime end;
        private String location;
        // gen?

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public Event(String name, String description, LocalDateTime start, LocalDateTime end) {
        this.name = name;
        this.description = description;
        this.start = start;
        this.end = end;
    }
}
