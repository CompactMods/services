package dev.compactmods.services.test.util;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FileHelper {

    public static final FileHelper INSTANCE = new FileHelper();

    private FileHelper() {}

    private Optional<Path> getFileInternal(String filename) {
        final var res = getClass().getClassLoader().getResource(filename);
        if(res == null) {
            return Optional.empty();
        }

        return Optional.of(Path.of(res.getFile()));
    }

    public static Optional<Path> path(Path relative) {
        return INSTANCE.getFileInternal(relative.toString());
    }

    public static Optional<JsonObject> readJsonFromFile(Path path) {
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                return Optional.of(GsonHelper.parse(reader));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return Optional.empty();
        }
    }

}
