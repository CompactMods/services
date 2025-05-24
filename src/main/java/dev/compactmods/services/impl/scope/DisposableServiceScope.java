package dev.compactmods.services.impl.scope;

import dev.compactmods.services.IServiceProvider;
import dev.compactmods.services.ServiceDescriptors;
import dev.compactmods.services.impl.ServiceCache;
import dev.compactmods.services.resolution.IServiceDescriptor;
import dev.compactmods.services.IServiceScope;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DisposableServiceScope implements IServiceScope, AutoCloseable {

    private final UUID uniqueId;
    private final IServiceProvider singletonProvider;
    private final ServiceScopeCloseCallbacks closeCallbacks;
    private final ServiceCache cache;

    public DisposableServiceScope(UUID id, IServiceProvider singletonProvider) {
        this.uniqueId = id;
        this.singletonProvider = singletonProvider;
        this.closeCallbacks = new ServiceScopeCloseCallbacks(this);
        this.cache = ServiceCache.create();
    }

    @Override
    public UUID id() {
        return uniqueId;
    }

    @Override
    public <T> Stream<T> servicesOfType(Class<T> type) {
        return Stream.concat(
                singletonProvider.servicesOfType(type),
                cache.servicesOfType(type)
        );
    }

    @Override
    public <T> Optional<T> service(IServiceDescriptor<T> descriptor) {
        return switch (descriptor.lifetime()) {
            case Singleton -> singletonProvider.service(descriptor);
            case Scoped -> cache.service(descriptor);
        };
    }

    @Override
    public <T> T getOrCreateService(IServiceDescriptor<T> descriptor) {
        return switch (descriptor.lifetime()) {
            case Singleton -> singletonProvider.getOrCreateService(descriptor);
            case Scoped -> cache.getOrCreateService(descriptor);
        };
    }

    @Override
    public ServiceScopeCloseCallbacks disposalHandler() {
        return this.closeCallbacks;
    }

    @Override
    public void close() throws Exception {
        cache.clear();
        closeCallbacks.dispose().join();
    }
}
