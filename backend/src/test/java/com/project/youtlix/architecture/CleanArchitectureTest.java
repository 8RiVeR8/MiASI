package com.project.youtlix.architecture;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

/** Architecture tests enforcing the modular ports-and-adapters structure. */
class CleanArchitectureTest {
    private static final Set<String> MODULES = Set.of("authentication", "contentlibrary", "recommendation", "videoplayback", "common");

    @Test
    void productionCodeUsesOnlyKnownTopLevelModules() throws IOException {
        Path root = Path.of("src/main/java/com/project/youtlix");
        try (var paths = Files.walk(root)) {
            paths.filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !path.getFileName().toString().equals("YoutlixApplication.java"))
                    .forEach(path -> assertTrue(MODULES.contains(root.relativize(path).iterator().next().toString()), path.toString()));
        }
    }

    @Test
    void domainLayerDoesNotDependOnFrameworkApplicationOrInfrastructure() throws IOException {
        Path root = Path.of("src/main/java/com/project/youtlix");
        try (var paths = Files.walk(root)) {
            paths.filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> path.toString().replace('\\', '/').contains("/domain/"))
                    .forEach(CleanArchitectureTest::assertPureDomainFile);
        }
    }

    @Test
    void applicationLayerDoesNotDependOnInfrastructureAdapters() throws IOException {
        Path root = Path.of("src/main/java/com/project/youtlix");
        try (var paths = Files.walk(root)) {
            paths.filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> path.toString().replace('\\', '/').contains("/application/"))
                    .forEach(path -> assertFileDoesNotContain(path, ".infrastructure."));
        }
    }

    private static void assertPureDomainFile(Path path) {
        assertFileDoesNotContain(path, ".application.");
        assertFileDoesNotContain(path, ".infrastructure.");
        assertFileDoesNotContain(path, "org.springframework");
        assertFileDoesNotContain(path, "jakarta.persistence");
    }

    private static void assertFileDoesNotContain(Path path, String forbiddenText) {
        try {
            String text = Files.readString(path);
            assertFalse(text.contains(forbiddenText), path + " contains forbidden dependency " + forbiddenText);
        } catch (IOException exception) {
            fail(exception);
        }
    }
}
