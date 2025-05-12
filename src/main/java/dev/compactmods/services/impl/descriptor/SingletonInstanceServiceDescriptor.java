package dev.compactmods.services.impl.descriptor;

import dev.compactmods.services.impl.ServiceLifetime;
import dev.compactmods.services.resolution.IServiceDescriptor;
import dev.compactmods.services.resolution.IServiceResolver;

public record SingletonInstanceServiceDescriptor<TSrv>(TSrv instance) implements IServiceDescriptor<TSrv> {
    @Override
    public ServiceLifetime lifetime() {
        return ServiceLifetime.Singleton;
    }

    @Override
    public Class<TSrv> type() {
        //noinspection unchecked
        return (Class<TSrv>) instance.getClass();
    }

    @Override
    public IServiceResolver<TSrv> resolver() {
        return service -> instance;
    }
}
