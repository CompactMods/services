package dev.compactmods.services.test.services;

import java.util.UUID;

public class ExampleHostingObject implements AutoCloseable {

    private final UUID instanceID;
    private final String name;

    public ExampleHostingObject(String name) {
        this.instanceID = UUID.randomUUID();
        this.name = name;
    }

    public UUID getInstanceID() {
        return instanceID;
    }

    @Override
    public void close() throws Exception {

    }
}
