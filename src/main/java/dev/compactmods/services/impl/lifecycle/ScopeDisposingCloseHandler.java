package dev.compactmods.services.impl.lifecycle;

import dev.compactmods.services.IServiceScope;
import dev.compactmods.services.lifecycle.ScopeCloseHandler;

import java.util.function.Consumer;

public record ScopeDisposingCloseHandler(Consumer<IServiceScope> unregisterHandler) implements ScopeCloseHandler {
    @Override
    public void handle(IServiceScope disposed) {
        unregisterHandler.accept(disposed);
    }
}
