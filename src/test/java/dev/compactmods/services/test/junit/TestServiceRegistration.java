package dev.compactmods.services.test.junit;

import dev.compactmods.services.IServiceScope;
import dev.compactmods.services.impl.BasicServiceProvider;
import dev.compactmods.services.test.services.ExampleHostingObject;
import dev.compactmods.services.test.services.MyServices;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestServiceRegistration {

    @Test
    public void testRegistration() throws Exception {
         final var server = new ExampleHostingObject("Server 1");

        var provider = BasicServiceProvider.create();

        IServiceScope serverScopedServices = provider.scope(server);
        try(var example = serverScopedServices.service(MyServices.EXAMPLE)) {
            // do stuff
            var numRegistered = example.count();
            Assertions.assertEquals(0, numRegistered);

            var numRegistered2 = example.count();
            Assertions.assertEquals(1, numRegistered2);
        }

        try(var example2 = serverScopedServices.service(MyServices.EXAMPLE)) {
            // resuming from above, scope has not been disposed of yet
            var numRegistered = example2.count();
            Assertions.assertEquals(2, numRegistered);
        }
    }
}
