package com.toysocialnetworkgui.domain;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Class that manages an event. I think it should be a subject where users are observers to this object.
 * If it passed a day/(an hour for testing purposes) from the last notification you should notify all users.
 * Cycle through all events at the start of application. Have to extract that ToySocialNetwrokApp main  And notify all users.
 * Last notif for event A -> 13:00
 * User1 logs at 14:05 -> notify all observers
 * User2 logs at 14:30 -> no nothing, he is already notified
 * User3 logs at 15:10 -> notify all
 * Users have to implement the observer interface
 * Events has: - A name, a description, LocalDate start, LocalDate end, Participants(Observers)
 *  - TODO
 *      - Do something with image, in bd store the path to the image to event
 */
public class Event {


    private Integer id;
    private String name;
    private String description;
    private LocalDate start;
    private LocalDate end;
    private String location;
    private String category;
    private String organizer;
    private String photoPath;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public Event(String name, String organizer, String location, String category, String description, LocalDate start, LocalDate end, String eventPath) {
        this.organizer = organizer;
        this.name = name;
        this.description = description;
        this.start = start;
        this.end = end;
        this.location = location;
        this.category = category;
        this.photoPath = eventPath;
    }

    public Event(Integer id, String name, String organizer, String location, String category, String description, LocalDate start, LocalDate end, String photoPath) {
        this.id = id;
        this.organizer = organizer;
        this.name = name;
        this.description = description;
        this.start = start;
        this.end = end;
        this.location = location;
        this.category = category;
        this.photoPath = photoPath;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Two events are the same if they have the same name. Sorry for this eduard :(
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(name, event.name);
        }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, start, end, location, category);
    }

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

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public String getPhotoPath() {
        return photoPath;
    }
}
