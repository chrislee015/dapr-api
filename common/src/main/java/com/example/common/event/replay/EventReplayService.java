package com.example.common.event.replay;

import java.time.Instant;
import java.util.UUID;

public interface EventReplayService {
    void replayAll(String tenantId);
    void replayFrom(String tenantId, Instant timestamp);
    void replayAggregate(String tenantId, UUID aggregateId);
}
