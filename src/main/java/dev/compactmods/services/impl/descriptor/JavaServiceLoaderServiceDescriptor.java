package dev.compactmods.services.impl.descriptor;

import dev.compactmods.services.impl.ServiceLifetime;
import dev.compactmods.services.impl.resolver.SimpleSupplierServiceResolver;
import dev.compactmods.services.resolution.IServiceDescriptor;
import dev.compactmods.services.resolution.IServiceResolver;

import java.util.ServiceLoader;

public record JavaServiceLoaderServiceDescriptor<TSrv>(Class<TSrv> type) implements IServiceDescriptor<TSrv> {
    @Override
    public ServiceLifetime lifetime() {
        return ServiceLifetime.Scoped;
    }

    @Override
    public IServiceResolver<TSrv> resolver() {
        return new SimpleSupplierServiceResolver<>(() -> ServiceLoader.load(type).findFirst().orElse(null));
    }
}
