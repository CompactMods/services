package dev.compactmods.services.impl.scope;

import dev.compactmods.services.ServiceDescriptors;
import dev.compactmods.services.resolution.IServiceDescriptor;
import dev.compactmods.services.IServiceScope;
import dev.compactmods.services.lifecycle.ScopeDisposalCallback;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

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
        if (disposalHandler.isDisposed)
            return;

        disposalHandler.dispose(self -> {
            // TODO: Disposal of references
        });
    }

    public <TSingletonSrv> void registerSingletonResource(TSingletonSrv instance) {
        var descriptor = ServiceDescriptors.singleton(instance);
        this.activeServices.put(descriptor, instance);
    }

    public static final class ServiceScopeDisposalHandler {

        private final WeakReference<IServiceScope> serviceRef;

        private boolean isDisposed = false;
        private final Set<ScopeDisposalCallback> preDisposalCallbacks;
        private final Set<ScopeDisposalCallback> postDisposalCallbacks;

        public ServiceScopeDisposalHandler(IServiceScope scope) {
            this.serviceRef = new WeakReference<>(scope);
            this.preDisposalCallbacks = new HashSet<>();
            this.postDisposalCallbacks = new HashSet<>();
        }

        public void beforeDisposed(ScopeDisposalCallback callback) {
            this.preDisposalCallbacks.add(callback);
        }

        public void afterDisposed(ScopeDisposalCallback callback) {
            this.postDisposalCallbacks.add(callback);
        }

        void dispose(Consumer<IServiceScope> disposingScope) {
            if(isDisposed)
                return;

            var ref = serviceRef.get();

            this.preDisposalCallbacks.forEach(c -> c.handle(ref));

            disposingScope.accept(ref);

            this.postDisposalCallbacks.forEach(c -> c.handle(ref));
            serviceRef.clear();
            this.isDisposed = true;
        }
    }
}
