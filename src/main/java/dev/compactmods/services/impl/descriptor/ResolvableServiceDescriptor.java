package dev.compactmods.services.impl.descriptor;

import dev.compactmods.services.impl.ServiceLifetime;
import dev.compactmods.services.resolution.IServiceDescriptor;
import dev.compactmods.services.resolution.IServiceResolver;

public record ResolvableServiceDescriptor<TSrv>(Class<TSrv> type, IServiceResolver<TSrv> resolver, ServiceLifetime lifetime)
        implements IServiceDescriptor<TSrv> {
}
