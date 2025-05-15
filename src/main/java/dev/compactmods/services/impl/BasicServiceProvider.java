package dev.compactmods.services.impl;

import dev.compactmods.services.IServiceScopeProvider;
import dev.compactmods.services.IServiceScope;
import dev.compactmods.services.impl.scope.DisposableServiceScope;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;

public class BasicServiceProvider implements IServiceScopeProvider, AutoCloseable {

    private final Map<UUID, IServiceScope> serviceScopes;
    private final Map<Object, UUID> keyedScopeMap;

    private static final UUID DEFAULT_SCOPE = new UUID(0, 0);

    private BasicServiceProvider() {
        this.serviceScopes = new Object2ReferenceOpenHashMap<>();
        this.keyedScopeMap = new Reference2ObjectOpenHashMap<>();
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
        if(!keyedScopeMap.containsKey(key)) {
            locatedServiceKey = UUID.randomUUID();

            var scope = new DisposableServiceScope(locatedServiceKey);
            scope.registerSingletonResource(key);

            scope.disposalHandler().afterDisposed(disposing -> {
                //noinspection resource
                this.serviceScopes.remove(disposing.id());
                this.keyedScopeMap.remove(key);
            });

            this.serviceScopes.put(locatedServiceKey, scope);
            this.keyedScopeMap.put(key, locatedServiceKey);

            return scope;
        }

        locatedServiceKey = keyedScopeMap.get(key);
        return serviceScopes.get(locatedServiceKey);
    }

    @Override
    public void close() throws Exception {
        Set<UUID> scopes = serviceScopes.keySet();
        try(var execs = Executors.newFixedThreadPool(scopes.size())) {
            for (var scope : scopes) {
                IServiceScope iServiceScope = serviceScopes.get(scope);
                execs.submit(() -> {
                    try {
                        iServiceScope.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            var ignored = execs.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS);
        }
    }
}
