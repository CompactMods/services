package dev.compactmods.services.test.example.data;

import com.mojang.serialization.JsonOps;
import dev.compactmods.services.test.util.FileHelper;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PersonManagementService implements AutoCloseable {

    private final UUID serviceID;
    private final Path workingDir;
    private final Map<String, Person> trackedFiles;

    public PersonManagementService(Path directory) {
        this.serviceID = UUID.randomUUID();
        this.workingDir = directory;
        this.trackedFiles = new HashMap<>();
    }

    public UUID getServiceID() {
        return serviceID;
    }

    public Optional<Person> getPerson(String file) {
        var found = this.trackedFiles.computeIfAbsent(file, fn -> this.load(file).orElse(null));
        return Optional.ofNullable(found);
    }

    public Person track(String file, Person person) {
        this.trackedFiles.put(file, person);
        return person;
    }

    private Optional<Person> load(String file) {
        var path = workingDir.resolve(file);
        return FileHelper.readJsonFromFile(path).map(jsonobject -> {
            return Person.CODEC.parse(JsonOps.INSTANCE, jsonobject).result().orElseThrow();
        });
    }

    private void save(String file, Person person) {
        var dest = workingDir.resolve(file);
        try (Writer writer = Files.newBufferedWriter(dest, StandardCharsets.UTF_8)) {
            var data = Person.CODEC.encodeStart(JsonOps.INSTANCE, person).result().get();
            writer.write(data.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        for(final var file : trackedFiles.entrySet()) {
            save(file.getKey(), file.getValue());
        }
    }
}
