package dev.compactmods.services.impl.resolver;

import dev.compactmods.services.IServiceScope;
import dev.compactmods.services.resolution.IServiceResolver;

public record ClassMatchingServiceResolver<TSrv>(Class<TSrv> serviceClass) implements IServiceResolver<TSrv> {
    @Override
    public TSrv resolve(IServiceScope scopedServices) {
        return null;
    }
}
