package dev.compactmods.services.test.junit;

import dev.compactmods.services.ServiceDescriptors;
import dev.compactmods.services.impl.BasicServiceProvider;
import dev.compactmods.services.resolution.IServiceDescriptor;
import dev.compactmods.services.test.example.data.PersonManagementService;
import dev.compactmods.services.test.util.FileHelper;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public class DataManagementTests {

    public static final IServiceDescriptor<Path> TMP_SAVE_DIR_SRV = ServiceDescriptors
            .singleton(() -> FileHelper.path(Path.of("scenarios", "basic-save-tmp")).orElseThrow());

    public static final IServiceDescriptor<PersonManagementService> PEOPLE = ServiceDescriptors.scoped(PersonManagementService.class,
            services -> {
                final Path path = TMP_SAVE_DIR_SRV.resolver().resolve(services);
                return new PersonManagementService(path);
            });

    @BeforeAll
    public static void setup() throws IOException {
        FileHelper.path(Path.of("scenarios", "basic-save-tmp")).ifPresent(d -> {
            FileUtils.deleteQuietly(d.toFile());
        });

        final var dir = FileHelper.path(Path.of("scenarios", "basic-save")).orElseThrow();
        FileUtils.copyDirectory(dir.toFile(), dir.getParent().resolve("basic-save-tmp").toFile());
    }

    @Test
    public void getsSameServiceWhileScopeNotDisposed() throws Exception {
        final var services = BasicServiceProvider.create();
        final var scope = services.defaultScope();

        UUID pass1, pass2;
        var peopleSrv1 = scope.getOrCreateService(PEOPLE);
        var peopleSrv2 = scope.getOrCreateService(PEOPLE);

        // Because we have not disposed of our scope, both of these should have resolved to the same service instance
        pass1 = peopleSrv1.getServiceID();
        pass2 = peopleSrv2.getServiceID();

        Assertions.assertEquals(pass1, pass2);

        peopleSrv1.close();
        peopleSrv2.close();
    }

    @Test
    public void testDataManagement() throws Exception {
        final var services = BasicServiceProvider.create();
        final var scope = services.defaultScope();

        // Scope 1: Open, Verify, and then Randomize Data
        UUID serviceIdFirstPass;
        try (var peopleSrv = scope.getOrCreateService(PEOPLE)) {
            serviceIdFirstPass = peopleSrv.getServiceID();
            peopleSrv.getPerson("john-smith.json").ifPresent(john -> {
                // Initial Data
                Assertions.assertNotNull(john);
                Assertions.assertEquals("John Smith", john.name());
                Assertions.assertEquals(25, john.age());
                Assertions.assertEquals("male", john.sex());

                john.randomize();
            });
        }

        // Scope 2: Verify data randomized
        UUID serviceIdSecondPass;
        try (var peopleSrv = scope.getOrCreateService(PEOPLE)) {
            serviceIdSecondPass = peopleSrv.getServiceID();
            peopleSrv.getPerson("john-smith.json").ifPresent(john -> {
                // New Data
                Assertions.assertNotNull(john);
                Assertions.assertNotEquals("John Smith", john.name());
            });
        }

        // Assert we truly instantiated twice, per scope rules
        Assertions.assertEquals(serviceIdFirstPass, serviceIdSecondPass);

        final var saveDir = FileHelper.path(Path.of("scenarios", "basic-save-tmp")).orElseThrow();
        Assertions.assertTrue(saveDir.resolve("john-smith.json").toFile().exists());

        services.close();
    }
}
