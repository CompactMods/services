package dev.compactmods.services.impl;

import dev.compactmods.services.IServiceProvider;
import dev.compactmods.services.IServiceScopeProvider;
import dev.compactmods.services.IServiceScope;
import dev.compactmods.services.impl.lifecycle.CloseScopeTask;
import dev.compactmods.services.impl.lifecycle.ScopeDisposingCloseHandler;
import dev.compactmods.services.impl.scope.DisposableServiceScope;
import dev.compactmods.services.lifecycle.ScopeCloseHandler;
import dev.compactmods.services.resolution.IServiceDescriptor;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;

public class BasicServiceProvider implements IServiceScopeProvider, AutoCloseable {

    private final Map<UUID, IServiceScope> serviceScopes;
    private final Map<UUID, ScopeCloseHandler> cleanupHandlers;
    private final Map<Object, UUID> keyedScopeMap;
    private final ServiceCache singletonServices;

    private static final UUID DEFAULT_SCOPE = new UUID(0, 0);

    private BasicServiceProvider() {
        this.serviceScopes = new Object2ReferenceOpenHashMap<>();
        this.cleanupHandlers = new Object2ReferenceOpenHashMap<>();
        this.keyedScopeMap = new Reference2ObjectOpenHashMap<>();
        this.singletonServices = ServiceCache.create();
    }

    public static BasicServiceProvider create() {
        return new BasicServiceProvider();
    }

    @Override
    public IServiceScope defaultScope() {
        return scope(DEFAULT_SCOPE);
    }

    @Override
    public <TScopeKey> IServiceScope scope(TScopeKey key) {
        UUID locatedServiceKey;
        if (!keyedScopeMap.containsKey(key)) {
            locatedServiceKey = UUID.randomUUID();

            var scope = new DisposableServiceScope(locatedServiceKey, singletonServices);

            var cleanupHandler = new ScopeDisposingCloseHandler(s -> {
                //noinspection resource
                this.serviceScopes.remove(s.id());
                this.keyedScopeMap.remove(s.id());
            });

            scope.disposalHandler().addAfterCloseHandler(cleanupHandler);

            this.serviceScopes.put(locatedServiceKey, scope);
            this.keyedScopeMap.put(key, locatedServiceKey);
            this.cleanupHandlers.put(scope.id(), cleanupHandler);

            return scope;
        }

        locatedServiceKey = keyedScopeMap.get(key);
        return serviceScopes.get(locatedServiceKey);
    }

    @Override
    public void close() throws Exception {
        final var execs = Executors.newCachedThreadPool();

        // Clear out all disposal callbacks that are registered on scope init
        serviceScopes.values().forEach(scope -> {
            var handler = cleanupHandlers.get(scope.id());
            scope.disposalHandler().removeAfterCloseHandler(handler);
        });

        try (execs) {
            final var closeables = new ObjectArraySet<CloseScopeTask>(serviceScopes.size());
            serviceScopes.values().forEach(scope -> {
                closeables.add(new CloseScopeTask(scope));
            });

            execs.invokeAll(closeables);
        }

        singletonServices.clear();

        // Clear out maps
        serviceScopes.clear();
        keyedScopeMap.clear();
    }
}
