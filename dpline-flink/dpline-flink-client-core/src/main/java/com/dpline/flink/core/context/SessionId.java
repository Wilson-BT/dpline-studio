package com.dpline.flink.core.context;

import java.util.Objects;
import java.util.UUID;

public class SessionId {

    private final UUID identifier;

    public static SessionId create() {
        return new SessionId(UUID.randomUUID());
    }

    public SessionId(UUID identifier) {
        this.identifier = identifier;
    }

    public UUID getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SessionId)) {
            return false;
        }
        SessionId that = (SessionId) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return identifier.toString();
    }
}
