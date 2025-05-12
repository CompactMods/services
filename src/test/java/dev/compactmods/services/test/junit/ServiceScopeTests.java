package dev.compactmods.services.test.junit;

import dev.compactmods.services.impl.BasicServiceProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ServiceScopeTests {

    @Test
    public void canBeginDefaultScope() throws Exception {
        var provider = BasicServiceProvider.create();
        var scopedServices = provider.defaultScope();
        Assertions.assertNotNull(scopedServices);

        provider.close();
    }

    @Test
    public void resolvingSameScopeKeyReturnsSameScope() throws Exception {
        var provider = BasicServiceProvider.create();

        UUID scopeKey = UUID.randomUUID();

        var scopedServices = provider.scope(scopeKey);
        Assertions.assertNotNull(scopedServices);

        var scopedServices2 = provider.scope(scopeKey);
        Assertions.assertNotNull(scopedServices2);

        Assertions.assertSame(scopedServices, scopedServices2);

        provider.close();
    }
}
