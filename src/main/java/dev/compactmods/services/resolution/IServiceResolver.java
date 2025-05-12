package dev.compactmods.services.resolution;

import dev.compactmods.services.IServiceScope;

@FunctionalInterface
public interface IServiceResolver<TSrv> {
    TSrv resolve(IServiceScope scopedServices);
}
