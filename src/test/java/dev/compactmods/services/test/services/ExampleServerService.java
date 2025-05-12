package dev.compactmods.services.test.services;

public class ExampleServerService implements AutoCloseable {

    private final ExampleHostingObject host;
    private int count = 0;

    public ExampleServerService(ExampleHostingObject host) {
        this.host = host;
    }

    @Override
    public void close() throws Exception {

    }

    public int count() {
        return this.count++;
    }
}
