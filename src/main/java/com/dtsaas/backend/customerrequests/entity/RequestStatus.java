package com.dtsaas.backend.customerrequests.entity;

import java.util.List;

public enum RequestStatus {
    NEW, SEEN, ACCEPTED, REJECTED, COMPLETED, CANCELLED;

    public List<RequestStatus> allowedTransitions() {
        return switch (this) {
            case NEW -> List.of(SEEN, ACCEPTED, REJECTED, CANCELLED);
            case SEEN -> List.of(ACCEPTED, REJECTED, CANCELLED);
            case ACCEPTED -> List.of(COMPLETED, CANCELLED);
            case REJECTED, COMPLETED, CANCELLED -> List.of();
        };
    }

    public boolean canTransitionTo(RequestStatus next) {
        return allowedTransitions().contains(next);
    }
}
