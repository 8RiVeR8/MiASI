package com.project.youtlix.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CleanArchitectureTest {

    private static final Path MAIN_PACKAGE = Path.of("src/main/java/com/project/youtlix");

    @Test
    void domainLayerDoesNotDependOnFrameworkApplicationOrInfrastructure() throws IOException {
        try (Stream<Path> files = javaFiles()) {
            files.filter(path -> normalized(path).contains("/domain/"))
                    .forEach(path -> assertThat(read(path))
                            .describedAs(path.toString())
                            .doesNotContain("org.springframework")
                            .doesNotContain("jakarta.persistence")
                            .doesNotContain(".application.")
                            .doesNotContain(".infrastructure."));
        }
    }

    @Test
    void applicationLayerDoesNotDependOnInfrastructureAdapters() throws IOException {
        try (Stream<Path> files = javaFiles()) {
            files.filter(path -> normalized(path).contains("/application/"))
                    .forEach(path -> assertThat(read(path))
                            .describedAs(path.toString())
                            .doesNotContain(".infrastructure.")
                            .doesNotContain("jakarta.persistence")
                            .doesNotContain("org.springframework.web"));
        }
    }

    @Test
    void productionCodeUsesOnlyKnownTopLevelModules() throws IOException {
        Set<String> allowed = Set.of(
                "authentication",
                "common",
                "contentlibrary",
                "recommendation",
                "videoplayback"
        );

        try (Stream<Path> paths = Files.list(MAIN_PACKAGE)) {
            Set<String> directories = paths
                    .filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .collect(java.util.stream.Collectors.toSet());

            assertThat(directories).containsExactlyInAnyOrderElementsOf(allowed);
        }
    }

    @Test
    void authenticationModuleDoesNotContainLocalAccountAggregate() throws IOException {
        Path authPath = MAIN_PACKAGE.resolve("authentication");
        try (Stream<Path> files = Files.walk(authPath)) {
            assertThat(files.filter(path -> path.toString().endsWith(".java"))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.contains("Account") || name.contains("Password") || name.contains("Session"))
                    .toList())
                    .containsExactly("SupabaseSession.java");
        }
    }

    private Stream<Path> javaFiles() throws IOException {
        return Files.walk(MAIN_PACKAGE).filter(path -> path.toString().endsWith(".java"));
    }

    private String read(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private String normalized(Path path) {
        return path.toString().replace('\\', '/');
    }
}
