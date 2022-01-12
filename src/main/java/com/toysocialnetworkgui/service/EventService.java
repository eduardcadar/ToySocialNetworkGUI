package com.toysocialnetworkgui.service;

import com.toysocialnetworkgui.domain.Event;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.EventRepository;
import com.toysocialnetworkgui.repository.db.EventDbRepo;
import com.toysocialnetworkgui.repository.db.EventsSubscriptionDbRepo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventService {

    private EventDbRepo eventRepo;
    private EventsSubscriptionDbRepo eventsSubscriptionRepo;

    public EventDbRepo getEventRepo() { return eventRepo; }

    public EventsSubscriptionDbRepo getEventsSubscriptionRepo() { return eventsSubscriptionRepo; }

    public EventService(EventDbRepo repo, EventsSubscriptionDbRepo eventsSubscriptionDbRepo) {
        this.eventRepo = repo;
        this.eventsSubscriptionRepo = eventsSubscriptionDbRepo;
    }

    public int getEventsSize() {
        return eventRepo.size();
    }

    public int getUserEventsSize(String email) {
        return eventsSubscriptionRepo.getUserEventsSize(email);
    }

    /**
     * Returns a page with events
     * @param firstrow how many events to skip
     * @param rowcount how many events to return
     * @return list of events
     */
    public List<Event> getEventsPage(int firstrow, int rowcount) {
        return eventRepo.getEventsPage(firstrow, rowcount);
    }

    /**
     * Returns a page with events a user is subscribed to
     * @param firstrow how many events to skip
     * @param rowcount how many events to return
     * @return list of subscribed events
     */
    public List<Event> getUserEventsPage(String email, int firstrow, int rowcount) {
        List<Event> events = new ArrayList<>();
        List<Integer> eventsIds = eventsSubscriptionRepo.getUserEventsPage(email, firstrow, rowcount);
        eventsIds.forEach(id -> events.add(eventRepo.getEvent(id)));
        return events;
    }

    public void updateEvent(String name, String location, String description, LocalDate startDate, LocalDate endDate) {
    }

    public void remove(){}

    public List<Event> getAllEvents() {
        return eventRepo.getAll();
    }

    public List<Event> getEventsForUser(String userEmail) {
        List<Integer> eventsId = eventsSubscriptionRepo.getEventsForUser(userEmail);
        List<Event> events = new ArrayList<>();
        eventsId.forEach(id -> {
            Event event = getEvent(id);
            events.add(event);
        });
        return events;
    }

    public List<User> getUsersForEvent(Event ev){

        return null;
    }

    public void saveEvent(String creator, String name, String category, String  location, String description, LocalDate startDate, LocalDate endDate, String eventPhotoPath) {
        eventRepo.save(new Event(name,creator,location,category, description, startDate, endDate, eventPhotoPath));
    }


    public void removeEvent(String name, String location, String description, LocalDate startDate, LocalDate endDate) {
    }

    /**
     * Adds a new subscriber to the event
     * @param eventId - Integer, ID of an existing event
     * @param userEmail - String, email of an existing user
     */
    public void subscribeUserToEvent(Integer eventId, String userEmail){
            eventsSubscriptionRepo.addSubscriber(eventId, userEmail);
    }

    /**
     * Gets an event from repo
     * @param id - integer
     * @return Returns the event with id same as parameter id; NULL if it doesn't exists
     *
     */
    public Event getEvent(Integer id) {
        return eventRepo.getEvent(id);
    }

    public void unsubscribeUserFromEvent(Integer eventId, String userEmail) {
        eventsSubscriptionRepo.removeSubscriber(eventId, userEmail);
    }
}
