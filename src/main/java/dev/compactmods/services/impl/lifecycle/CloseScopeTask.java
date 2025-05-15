package dev.compactmods.services.impl.lifecycle;

import dev.compactmods.services.IServiceScope;

import java.util.concurrent.Callable;

public record CloseScopeTask(IServiceScope scope) implements Callable<Void> {
    @Override
    public Void call() throws Exception {
        scope.close();
        return null;
    }
}
