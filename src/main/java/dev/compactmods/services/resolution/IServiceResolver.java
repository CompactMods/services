package dev.compactmods.services.resolution;

import dev.compactmods.services.IServiceProvider;

@FunctionalInterface
public interface IServiceResolver<TSrv> {
    TSrv resolve(IServiceProvider services);
}
