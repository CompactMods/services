package dev.compactmods.services.impl.descriptor;

import dev.compactmods.services.impl.ServiceLifetime;
import dev.compactmods.services.resolution.IServiceDescriptor;
import dev.compactmods.services.resolution.IServiceResolver;

import java.util.function.Supplier;

public record ResolvableSingletonServiceDescriptor<TSrv>(Supplier<TSrv> supplier) implements IServiceDescriptor<TSrv> {

    @Override
    public ServiceLifetime lifetime() {
        return ServiceLifetime.Singleton;
    }

    @Override
    public Class<TSrv> type() {
        //noinspection unchecked
        return (Class<TSrv>) supplier.get().getClass();
    }

    @Override
    public IServiceResolver<TSrv> resolver() {
        return services -> supplier.get();
    }
}
