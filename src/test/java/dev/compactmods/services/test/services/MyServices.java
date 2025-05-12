package dev.compactmods.services.test.services;

import dev.compactmods.services.ServiceDescriptors;
import dev.compactmods.services.resolution.IServiceDescriptor;

public class MyServices {

    public static final IServiceDescriptor<ExampleHostingObject> SERVER = ServiceDescriptors.singleton(ExampleHostingObject.class);

    public static final IServiceDescriptor<ExampleServerService> EXAMPLE = ServiceDescriptors.scoped(ExampleServerService.class, services -> {
        var server = services.service(SERVER);
        return new ExampleServerService(server);
    });
}
