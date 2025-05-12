package dev.compactmods.services;

import dev.compactmods.services.impl.ServiceLifetime;
import dev.compactmods.services.impl.descriptor.ClassBasedServiceDescriptor;
import dev.compactmods.services.impl.descriptor.JavaServiceLoaderServiceDescriptor;
import dev.compactmods.services.impl.descriptor.ResolvableServiceDescriptor;
import dev.compactmods.services.impl.descriptor.SingletonInstanceServiceDescriptor;
import dev.compactmods.services.resolution.IServiceDescriptor;
import dev.compactmods.services.resolution.IServiceResolver;

public abstract class ServiceDescriptors {

    public static <TSrv> IServiceDescriptor<TSrv> scoped(Class<TSrv> serviceClass, IServiceResolver<TSrv> resolver) {
        return new ResolvableServiceDescriptor<>(serviceClass, resolver, ServiceLifetime.Scoped);
    }

    public static <TSrv> IServiceDescriptor<TSrv> singleton(TSrv instance) {
        return new SingletonInstanceServiceDescriptor<>(instance);
    }

    public static <TSrv> IServiceDescriptor<TSrv> singleton(Class<TSrv> serviceClass, IServiceResolver<TSrv> resolver) {
        return new ResolvableServiceDescriptor<>(serviceClass, resolver, ServiceLifetime.Singleton);
    }

    public static <TSrv> IServiceDescriptor<TSrv> singleton(Class<TSrv> serviceClass) {
        return new ClassBasedServiceDescriptor<>(serviceClass);
    }

    public static <TSrv> IServiceDescriptor<TSrv> javaLocated(Class<TSrv> resourceClass) {
        return new JavaServiceLoaderServiceDescriptor<>(resourceClass);
    }
}
