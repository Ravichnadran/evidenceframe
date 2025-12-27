package com.evidenceframe.evidencer.collector.github;

import com.evidenceframe.evidencer.core.Collector;
import com.evidenceframe.evidencer.core.CollectorResult;
import com.evidenceframe.evidencer.core.ExecutionContext;
import com.evidenceframe.evidencer.core.RunMode;

import java.util.List;

public final class GitHubCollector implements Collector {

    @Override
    public String name() {
        return "GitHub Repository Evidence";
    }

    @Override
    public CollectorResult collect(ExecutionContext context) {

        if (context.runMode() == RunMode.DRY_RUN) {
            return new CollectorResult(
                    name(),
                    CollectorResult.Status.SKIPPED,
                    List.of(),
                    "Dry run: skipping GitHub collector"
            );
        }

        GitHubConfig config;
        try {
            config = GitHubConfig.fromEnv();
        } catch (Exception e) {
            return new CollectorResult(
                    name(),
                    CollectorResult.Status.FAILED,
                    List.of(),
                    e.getMessage()
            );
        }

        GitHubClient client = new GitHubClient(config);
        GitHubResponseWriter writer =
                new GitHubResponseWriter(context.outputRoot());

        List<String> pages;
        try {
            pages = client.fetchRepositories();
        } catch (Exception e) {
            return new CollectorResult(
                    name(),
                    CollectorResult.Status.FAILED,
                    List.of(),
                    "Failed to fetch repositories: " + e.getMessage()
            );
        }

        try {
            writer.writeRepositoryPages(pages);
        } catch (Exception e) {
            return new CollectorResult(
                    name(),
                    CollectorResult.Status.PARTIAL,
                    List.of(),
                    "Failed to write some evidence files: " + e.getMessage()
            );
        }

        return new CollectorResult(
                name(),
                CollectorResult.Status.SUCCESS,
                List.of("github/repositories_page_*.json"),
                "GitHub repository evidence collected"
        );
    }
}
