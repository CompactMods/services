package dev.compactmods.services.test.benchmark;

import dev.compactmods.services.impl.BasicServiceProvider;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class ProviderLifecycleBenchmarks {

    @Param({ "100", "400", "1000", "10000" })
    public int numberCreatedScopes;

    public BasicServiceProvider services;

    @Setup(Level.Iteration)
    public void setUp() {
        services = BasicServiceProvider.create();
        final int numScopesPerThread = numberCreatedScopes / 4;
        try(var exec = Executors.newFixedThreadPool(4)) {
            for(int i = 0; i < 4; i++) {
                exec.submit(() -> {
                    for (int i2 = 0; i2 < numScopesPerThread; i2++)
                        services.scope(UUID.randomUUID());
                });
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Fork(1)
    @Warmup(iterations = 3)
    public void disposeServices() throws Exception {
        services.close();
    }
}
