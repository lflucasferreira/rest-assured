package com.portfolio.petclinic.models;

import java.util.Map;

public class OwnerCreatedEvent {

    private final int ownerId;
    private final String firstName;
    private final String lastName;

    public OwnerCreatedEvent(int ownerId, String firstName, String lastName) {
        this.ownerId = ownerId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Map<String, Object> asPayload() {
        return Map.of(
                "event", "OWNER_CREATED",
                "ownerId", ownerId,
                "firstName", firstName,
                "lastName", lastName
        );
    }
}
