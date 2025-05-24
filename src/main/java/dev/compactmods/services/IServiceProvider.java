package dev.compactmods.services;

import dev.compactmods.services.resolution.IServiceDescriptor;

import java.util.Optional;
import java.util.stream.Stream;

public interface IServiceProvider {
    <T> Stream<T> servicesOfType(Class<T> type);

    <T> Optional<T> service(IServiceDescriptor<T> descriptor);

    <T> T getOrCreateService(IServiceDescriptor<T> descriptor);
}
