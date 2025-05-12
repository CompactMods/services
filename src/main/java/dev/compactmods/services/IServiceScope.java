package dev.compactmods.services;

import dev.compactmods.services.impl.scope.DisposableServiceScope;
import dev.compactmods.services.resolution.IServiceDescriptor;

import java.util.UUID;

/**
 * Represents a scope from a given {@link IServiceScopeProvider}, that can be used to create and manage service scopes (lifetimes).
 */
public interface IServiceScope extends AutoCloseable {

    <T extends AutoCloseable> T service(IServiceDescriptor<T> descriptor);

    DisposableServiceScope.ServiceScopeDisposalHandler disposalHandler();

    UUID id();
}
