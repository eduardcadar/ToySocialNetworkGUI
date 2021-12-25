package com.toysocialnetworkgui.service;

import com.toysocialnetworkgui.domain.Event;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.EventRepository;

import java.time.LocalDate;
import java.util.List;

public class EventService {

    private EventRepository eventRepo;

    public EventRepository getEventRepo() { return eventRepo; }

    public EventService(EventRepository repo) {
        this.eventRepo = repo;
    }



    public void updateEvent(String name, String location, String description, LocalDate startDate, LocalDate endDate) {
    }

    public void remove(){}

    public List<Event> getAllEvents(){
        return eventRepo.getAll();
    }

    public List<Event> getEventsForUser(String userEmail) {
        //  " SELECT * FROM USERS_EVENTS WHERE userId = @userEmail";
        return null;
    }

    public List<User> getUsersForEvent(Event ev){

        return null;
    }

    public void saveEvent(String name, String location, String description, LocalDate startDate, LocalDate endDate) {
    }


    public void removeEvent(String name, String location, String description, LocalDate startDate, LocalDate endDate) {
    }
}
