package com.toysocialnetworkgui.validator;

import com.toysocialnetworkgui.domain.Event;

public class EventValidator implements  Validator<Event>{

    @Override
    public void validate(Event event) throws ValidatorException {
        String error = "";
        if(event.getName().isEmpty())
            error += "Name can't be empty!\n";
        if(event.getDescription().isEmpty())
            error += "Description can't be empty!\n";
        if(event.getLocation().isEmpty())
            error += "Location can't be empty!\n";
        if(event.getCategory().isEmpty())
            error += "Category can't be empty!\n";
        if(event.getStart().toString().isEmpty())
            error += "Choose a start date!\n";
        if(event.getEnd().toString().isEmpty())
            error += "Choose an end date!\n";
        if(!error.isEmpty())
            throw  new ValidatorException(error);

    }
}
