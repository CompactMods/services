package dev.compactmods.services.test.junit;

import dev.compactmods.services.impl.BasicServiceProvider;
import dev.compactmods.services.test.services.ExampleHostingObject;
import dev.compactmods.services.test.services.CountingService;
import dev.compactmods.services.test.services.MyServices;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ScopedLifetimeTests {

    @Test
    public void canCreateScopedService() throws Exception {
        var provider = BasicServiceProvider.create();

        UUID scopeKey = UUID.randomUUID();

        var scopedServices = provider.scope(scopeKey);

        // Scope Access 1 - Initial State
        try(var example = scopedServices.service(MyServices.SCOPED_EXAMPLE)) {
            Assertions.assertNotNull(example);
            Assertions.assertInstanceOf(CountingService.class, example);
            Assertions.assertEquals(0, example.count());

            example.increment();
            Assertions.assertEquals(1, example.count());
        }

        provider.close();
    }

    @Test
    public void multipleAccessesReturnSameInstance() throws Exception {
        var provider = BasicServiceProvider.create();

        UUID scopeKey = UUID.randomUUID();

        var scopedServices = provider.scope(scopeKey);

        // Scope Access 1 - Initial State
        CountingService example = scopedServices.service(MyServices.SCOPED_EXAMPLE);
        try(example) {
            Assertions.assertNotNull(example);
            Assertions.assertInstanceOf(CountingService.class, example);
            Assertions.assertEquals(0, example.count());

            example.increment();
            Assertions.assertEquals(1, example.count());
        }

        // Scope Access 2 - Should Be Same Instance
        try(var example2 = scopedServices.service(MyServices.SCOPED_EXAMPLE)) {
            Assertions.assertEquals(1, example2.count());
            Assertions.assertSame(example, example2);
        }

        provider.close();
    }

}
