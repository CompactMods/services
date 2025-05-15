package dev.compactmods.services.impl.scope;

import dev.compactmods.services.IServiceScope;
import dev.compactmods.services.lifecycle.ScopeCloseHandler;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class ServiceScopeDisposalHandler {

    private final WeakReference<IServiceScope> serviceRef;

    private boolean isDisposed = false;
    private final Set<ScopeCloseHandler> preDisposalCallbacks;
    private final Set<ScopeCloseHandler> postDisposalCallbacks;

    public ServiceScopeDisposalHandler(IServiceScope scope) {
        this.serviceRef = new WeakReference<>(scope);
        this.preDisposalCallbacks = new ObjectOpenHashSet<>();
        this.postDisposalCallbacks = new ObjectOpenHashSet<>();
    }

    public boolean isDisposed() {
        return isDisposed;
    }

    public void addBeforeCloseHandler(ScopeCloseHandler callback) {
        this.preDisposalCallbacks.add(callback);
    }

    public void addAfterCloseHandler(ScopeCloseHandler callback) {
        this.postDisposalCallbacks.add(callback);
    }

    public void removeBeforeCloseHandler(ScopeCloseHandler callback) {
        this.preDisposalCallbacks.remove(callback);
    }

    public void removeAfterCloseHandler(ScopeCloseHandler callback) {
        this.postDisposalCallbacks.remove(callback);
    }

    void dispose(Consumer<IServiceScope> disposingScope) {
        if (isDisposed)
            return;

        var ref = serviceRef.get();

        this.preDisposalCallbacks.forEach(c -> c.handle(ref));

        disposingScope.accept(ref);

        this.postDisposalCallbacks.forEach(c -> c.handle(ref));
        serviceRef.clear();
        this.isDisposed = true;
    }

    public void clear(Predicate<ScopeCloseHandler> filter) {
        final var preRemoving = this.preDisposalCallbacks.stream()
                .filter(filter)
                .toList();

        final var postRemoving = this.postDisposalCallbacks.stream()
                .filter(filter)
                .toList();

        preRemoving.forEach(this.preDisposalCallbacks::remove);
        postRemoving.forEach(this.postDisposalCallbacks::remove);
    }
}
