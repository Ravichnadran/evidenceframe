package com.evidenceframe.evidencer.cli;

import com.evidenceframe.evidencer.collector.github.GitHubCollector;
import com.evidenceframe.evidencer.core.*;
import com.evidenceframe.evidencer.output.*;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Command(
        name = "auditexport",
        mixinStandardHelpOptions = true,
        version = "auditexport 0.1.0",
        description = "Evidence collection tool for technical compliance audits"
)
public class MainCommand implements Runnable {

    @Option(
            names = "--audit",
            required = true,
            description = "Audit type (e.g. ISO27001)"
    )
    private String auditType;

    @Option(
            names = "--output",
            description = "Output directory (default: ./evidence)"
    )
    private Path outputDir = Path.of("evidence");

    @Option(
            names = "--dry-run",
            description = "Run without collecting any external evidence"
    )
    private boolean dryRun;

    @Option(
            names = "--github",
            description = "Enable GitHub evidence collection"
    )
    private boolean githubEnabled;

    @Override
    public void run() {
        RunMode runMode = dryRun ? RunMode.DRY_RUN : RunMode.NORMAL;

        ExecutionContext context = new ExecutionContext(
                auditType,
                outputDir,
                Instant.now(),
                runMode
        );

        List<Collector> collectors = new ArrayList<>();

        if (githubEnabled) {
            collectors.add(new GitHubCollector());
        }

        if (collectors.isEmpty()) {
            System.err.println("No collectors enabled. Nothing to do.");
            System.exit(ExitCode.FAILURE.code());
            return;
        }

        if (dryRun) {
            System.out.println("Mode: DRY-RUN");
            System.out.println("Collectors enabled:");
            for (Collector collector : collectors) {
                System.out.println("  - " + collector.name());
            }
            System.out.println();
            System.out.println("Network access: DISABLED");
            System.out.println("File writes: DISABLED");

            if (githubEnabled) {
                String token = System.getenv("GITHUB_TOKEN");
                boolean hasToken = token != null && !token.isBlank();
                System.out.println("Credentials: " + (hasToken ? "detected" : "not detected"));
            }

            System.out.println();
            System.out.println("Planned evidence:");
            if (githubEnabled) {
                System.out.println("  - Repository metadata");
            }
            System.out.println("  - Hashes");
            System.out.println("  - Evidence ZIP");
            System.out.println();
            System.out.println("Dry-run completed successfully.");
            System.out.println("No external calls were made.");
            System.exit(0);
            return;
        }

        try {
            if (Files.exists(outputDir)) {
                try (Stream<Path> entries = Files.list(outputDir)) {
                    if (entries.findAny().isPresent()) {
                        System.err.println(
                                "Output directory is not empty. Use --output <new-dir> or remove existing directory."
                        );
                        System.exit(ExitCode.FAILURE.code());
                        return;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to check output directory: " + e.getMessage());
            System.exit(ExitCode.FAILURE.code());
            return;
        }

        EvidenceRun run = new EvidenceRun(context, collectors);

        System.out.println("Starting evidence collection run...");
        run.execute();

        for (CollectorResult result : run.results()) {
            System.out.printf(
                    "- %s : %s (%s)%n",
                    result.collectorName(),
                    result.status(),
                    result.message()
            );
        }

        try {
            new SummaryWriter().write(context, run.results());
            new MetadataWriter().write(context);
            new HashWriter().write(context.outputRoot());
        } catch (Exception e) {
            System.err.println("Failed to write outputs: " + e.getMessage());
            System.exit(ExitCode.FAILURE.code());
            return;
        }

        try {
            Path zip = new ZipPackager().createZip(context.outputRoot());
            System.out.println("Evidence directory created: " + context.outputRoot());
            System.out.println("Final audit artifact: " + zip);
            System.out.println("Checksum: checksums.sha256");
        } catch (Exception e) {
            System.err.println("Failed to create ZIP: " + e.getMessage());
            System.exit(ExitCode.FAILURE.code());
            return;
        }

        ExitCode exitCode = ExitCode.fromResults(run.results());
        System.exit(exitCode.code());
    }

    public static void main(String[] args) {
        int exitCode = new picocli.CommandLine(new MainCommand()).execute(args);
        System.exit(exitCode);
    }
}
