package com.codingnagger.adventofcode2023.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class InputLoader {
    public static List<String> Load(String filename) {
        return Load("src/main", filename);
    }

    public static List<String> LoadTest(String filename) {
        return Load("src/test", filename);
    }

    private static List<String> Load(String resourcesParent, String filename) {
        try {
            return Files.readAllLines(Paths.get(resourcesParent, "resources", filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
