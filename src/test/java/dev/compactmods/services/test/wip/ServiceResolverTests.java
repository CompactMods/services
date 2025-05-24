package dev.compactmods.services.test.wip;

import dev.compactmods.services.ServiceDescriptors;
import dev.compactmods.services.impl.BasicServiceProvider;
import dev.compactmods.services.resolution.IServiceDescriptor;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceResolverTests {

    IServiceDescriptor<SingletonServiceForComplex> COMPLEX_SINGLETON = ServiceDescriptors
            .singleton(SingletonServiceForComplex::new);

    IServiceDescriptor<ScopedServiceForComplex> COMPLEX_SCOPED = ServiceDescriptors
            .scoped(ScopedServiceForComplex.class, services -> new ScopedServiceForComplex());

//    @Test
    public void canResolveComplexConstructor() throws Exception {
        var provider = BasicServiceProvider.create();
        var scopedServices = provider.defaultScope();
        Assertions.assertNotNull(scopedServices);

        var singleton = scopedServices.getOrCreateService(COMPLEX_SINGLETON);

        long complexSingletonCnt = scopedServices.servicesOfType(SingletonServiceForComplex.class).count();

        Assertions.assertEquals(1, complexSingletonCnt);

        List<ConstructorActivationScanResult<MyComplexService>> scanResults = new ArrayList<>();
        for(var ctor : MyComplexService.class.getConstructors()) {
            Map<Integer, ParameterResolverResult> parameterResolverResultMap = new HashMap<>();
            int currentIndex = 0;
            for(var param : ctor.getParameters()) {
                final var neededType = param.getType();
                final var possible = scopedServices.servicesOfType(neededType).toList();
                final var result = new ParameterResolverResult(currentIndex, param, possible);
                parameterResolverResultMap.put(currentIndex, result);
                currentIndex++;
            }

            var res = new ConstructorActivationScanResult<MyComplexService>((Constructor<MyComplexService>) ctor, parameterResolverResultMap);
            scanResults.add(res);
        }

        provider.close();
    }

    private record ConstructorActivationScanResult<T>(Constructor<T> ctor, Map<Integer, ParameterResolverResult> parameterResolverResults) {}

    private record ParameterResolverResult(int index, Parameter param, List<?> possibleResolved) {

    }

    private static class MyComplexService {
        public final SingletonServiceForComplex singleton;
        public final ScopedServiceForComplex scoped;

        public MyComplexService(SingletonServiceForComplex singleton, ScopedServiceForComplex scoped) {
            this.singleton = singleton;
            this.scoped = scoped;
        }
    }

    private class SingletonServiceForComplex {}

    private class ScopedServiceForComplex {}
}
