package com.evidenceframe.evidencer.collector.github;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class GitHubResponseWriter {

    private final Path githubRoot;

    public GitHubResponseWriter(Path evidenceRoot) {
        Objects.requireNonNull(evidenceRoot, "evidenceRoot must not be null");
        this.githubRoot = evidenceRoot.resolve("github");
    }

    public void writeRepositoryPages(List<String> pages) throws IOException {
        Files.createDirectories(githubRoot);

        int pageNumber = 1;
        for (String page : pages) {
            Path file = githubRoot.resolve(
                    "repositories_page_" + pageNumber + ".json"
            );

            if (Files.exists(file)) {
                throw new IOException(
                        "Refusing to overwrite existing file: " + file
                );
            }

            Files.writeString(file, page);
            pageNumber++;
        }
    }
}
