package com.example.common.dao;

import java.util.UUID;

/**
 * Simple shared DAO-style object (not a JPA entity).
 * Use per-service persistence models for actual DB mapping.
 */
public record UserDao(UUID id, String email) {
}
