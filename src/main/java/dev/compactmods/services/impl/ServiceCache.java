package dev.compactmods.services.impl;

import dev.compactmods.services.IServiceProvider;
import dev.compactmods.services.resolution.IServiceDescriptor;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class ServiceCache implements IServiceProvider {

    private final Map<IServiceDescriptor<?>, Object> activeServices;

    private ServiceCache() {
        this.activeServices = new Reference2ObjectOpenHashMap<>();
    }

    public static ServiceCache create() {
        return new ServiceCache();
    }

    @Override
    public <T> Stream<T> servicesOfType(Class<T> type) {
        return activeServices.values()
                .stream()
                .filter(type::isInstance)
                .map(type::cast);
    }

    @Override
    public <T> Optional<T> service(IServiceDescriptor<T> descriptor) {
        if (!activeServices.containsKey(descriptor))
            return Optional.empty();

        var resolved = activeServices.get(descriptor);
        if (descriptor.type().isInstance(resolved)) {
            final var casted = descriptor.type().cast(resolved);
            return Optional.of(casted);
        }

        return Optional.empty();
    }

    public <T> T getOrCreateService(IServiceDescriptor<T> descriptor) {
        if (activeServices.containsKey(descriptor)) {
            var resolved = activeServices.get(descriptor);
            if (descriptor.type().isInstance(resolved))
                return descriptor.type().cast(resolved);
        }

        var newInstance = descriptor.resolver().resolve(this);
        activeServices.put(descriptor, newInstance);
        return newInstance;
    }

    public void clear() {
        activeServices.values()
                .stream()
                .filter(AutoCloseable.class::isInstance)
                .map(AutoCloseable.class::cast)
                .forEach(service -> {
                    try {
                        service.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        activeServices.clear();
    }
}
