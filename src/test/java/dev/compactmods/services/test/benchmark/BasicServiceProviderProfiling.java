package dev.compactmods.services.test.benchmark;

import dev.compactmods.services.impl.BasicServiceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.infra.Blackhole;

import java.util.UUID;
import java.util.concurrent.Executors;

public class BasicServiceProviderProfiling {

    private BasicServiceProvider services;

    @BeforeEach
    public void setup() {
        services = BasicServiceProvider.create();
        for (int i = 0; i < 10_000; i++)
            services.scope(UUID.randomUUID());
    }

    @Test
    public void profileScopeRegistration() {

    }

    @Test
    public void profileScopeDisposal() throws Exception {
        services.close();
    }
}
