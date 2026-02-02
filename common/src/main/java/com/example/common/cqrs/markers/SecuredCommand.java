package com.example.common.cqrs.markers;

public interface SecuredCommand {
    String requiredRole();
}
