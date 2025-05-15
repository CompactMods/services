package dev.compactmods.services.impl.scope;

import dev.compactmods.services.ServiceDescriptors;
import dev.compactmods.services.resolution.IServiceDescriptor;
import dev.compactmods.services.IServiceScope;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.Map;
import java.util.UUID;

public class DisposableServiceScope implements IServiceScope, AutoCloseable {

    private final UUID uniqueId;
    private final ServiceScopeDisposalHandler disposalHandler;
    private final Map<IServiceDescriptor<?>, Object> activeServices;

    public DisposableServiceScope(UUID id) {
        this.uniqueId = id;
        this.disposalHandler = new ServiceScopeDisposalHandler(this);
        this.activeServices = new Reference2ObjectOpenHashMap<>();
    }

    @Override
    public UUID id() {
        return uniqueId;
    }

    @Override
    public <T extends AutoCloseable> T service(IServiceDescriptor<T> descriptor) {
        if(activeServices.containsKey(descriptor)) {
            var resolved = activeServices.get(descriptor);
            if(descriptor.type().isInstance(resolved))
                return descriptor.type().cast(resolved);
        }

        var newInstance = descriptor.resolver().resolve(this);
        activeServices.put(descriptor, newInstance);
        return newInstance;
    }

    @Override
    public ServiceScopeDisposalHandler disposalHandler() {
        return this.disposalHandler;
    }

    @Override
    public void close() throws Exception {
        if (disposalHandler.isDisposed())
            return;

        disposalHandler.dispose(self -> {
            // TODO: Disposal of references
        });
    }

    public <TSingletonSrv> void registerSingletonResource(TSingletonSrv instance) {
        var descriptor = ServiceDescriptors.singleton(instance);
        this.activeServices.put(descriptor, instance);
    }
}
