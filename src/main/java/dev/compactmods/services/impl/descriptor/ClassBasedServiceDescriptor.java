package dev.compactmods.services.impl.descriptor;

import dev.compactmods.services.impl.ServiceLifetime;
import dev.compactmods.services.impl.resolver.ClassMatchingServiceResolver;
import dev.compactmods.services.resolution.IServiceDescriptor;
import dev.compactmods.services.resolution.IServiceResolver;

public record ClassBasedServiceDescriptor<TSrv>(Class<TSrv> clazz) implements IServiceDescriptor<TSrv> {

    @Override
    public ServiceLifetime lifetime() {
        return ServiceLifetime.Scoped;
    }

    @Override
    public Class<TSrv> type() {
        return clazz;
    }

    @Override
    public IServiceResolver<TSrv> resolver() {
        return new ClassMatchingServiceResolver<>(clazz);
    }
}
