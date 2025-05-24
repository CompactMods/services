package dev.compactmods.services.impl.scope;

import dev.compactmods.services.IServiceScope;
import dev.compactmods.services.lifecycle.ScopeCloseHandler;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public final class ServiceScopeCloseCallbacks {

    private final WeakReference<IServiceScope> serviceRef;

    private final Set<ScopeCloseHandler> callbacks;

    public ServiceScopeCloseCallbacks(IServiceScope scope) {
        this.serviceRef = new WeakReference<>(scope);
        this.callbacks = new ObjectOpenHashSet<>();
    }

    public void addAfterCloseHandler(ScopeCloseHandler callback) {
        this.callbacks.add(callback);
    }

    public void removeAfterCloseHandler(ScopeCloseHandler callback) {
        this.callbacks.remove(callback);
    }

    CompletableFuture<Void> dispose() {
        return CompletableFuture.runAsync(() -> {
            var ref = serviceRef.get();
            this.callbacks.forEach(c -> c.handle(ref));
        });
    }

    public void clear(Predicate<ScopeCloseHandler> filter) {
        final var postRemoving = this.callbacks.stream()
                .filter(filter)
                .toList();

        postRemoving.forEach(this.callbacks::remove);
    }
}
