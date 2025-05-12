package dev.compactmods.services.lifecycle;

import dev.compactmods.services.IServiceScope;

@FunctionalInterface
public interface ScopeDisposalCallback {
    void handle(IServiceScope disposed);
}
