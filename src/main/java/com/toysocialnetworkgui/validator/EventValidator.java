package com.toysocialnetworkgui.validator;

import com.toysocialnetworkgui.domain.Event;

public class EventValidator implements  Validator<Event>{

    @Override
    public void validate(Event event) throws ValidatorException {
        if (event.getName().isEmpty() ||
            event.getDescription().isEmpty() ||
            event.getLocation().isEmpty() ||
            event.getCategory().isEmpty() ||
            event.getStart() == null ||
            event.getEnd() == null) throw  new ValidatorException("Fill all fields!");
    }
}
