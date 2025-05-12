package dev.compactmods.services.impl.resolver;

import dev.compactmods.services.IServiceScope;
import dev.compactmods.services.resolution.IServiceResolver;

import java.util.function.Supplier;

public record SimpleSupplierServiceResolver<TSrv>(Supplier<TSrv> supplier) implements IServiceResolver<TSrv> {
    @Override
    public TSrv resolve(IServiceScope scopedServices) {
        return supplier.get();
    }
}
