package dev.compactmods.services.test.services;

import dev.compactmods.services.ServiceDescriptors;
import dev.compactmods.services.resolution.IServiceDescriptor;

public class MyServices {

    public static final IServiceDescriptor<ExampleHostingObject> SINGLETON_HOST = ServiceDescriptors.singleton(ExampleHostingObject.class);

    public static final IServiceDescriptor<CountingService> SCOPED_EXAMPLE = ServiceDescriptors.scoped(CountingService.class,
            services -> new CountingService());
}
