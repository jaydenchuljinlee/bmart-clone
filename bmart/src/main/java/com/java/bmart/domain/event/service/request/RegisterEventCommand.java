package com.java.bmart.domain.event.service.request;

public record RegisterEventCommand(String title, String description) {

    public static RegisterEventCommand of(final String title, final String description) {
        return new RegisterEventCommand(title, description);
    }
}
