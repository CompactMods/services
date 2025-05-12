package dev.compactmods.services.resolution;

import dev.compactmods.services.impl.ServiceLifetime;

/**
 * A service descriptor describes the absolute basics of a resolvable service - its lifetime and type description.
 *
 * @param <TSrv> The class definition of the service.
 */
public interface IServiceDescriptor<TSrv> {
    ServiceLifetime lifetime();
    Class<TSrv> type();
    IServiceResolver<TSrv> resolver();
}
