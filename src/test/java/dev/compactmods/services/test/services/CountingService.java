package dev.compactmods.services.test.services;

public class CountingService implements AutoCloseable {

    private int count = 0;

    @Override
    public void close() throws Exception {

    }

    public int count() {
        return this.count;
    }

    public int increment() {
        return ++this.count;
    }
}
