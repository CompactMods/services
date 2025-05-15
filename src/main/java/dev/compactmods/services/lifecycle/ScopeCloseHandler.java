package dev.compactmods.services.lifecycle;

import dev.compactmods.services.IServiceScope;

@FunctionalInterface
public interface ScopeCloseHandler {
    void handle(IServiceScope disposed);
}
