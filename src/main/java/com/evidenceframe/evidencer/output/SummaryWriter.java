package com.evidenceframe.evidencer.output;

import com.evidenceframe.evidencer.core.CollectorResult;
import com.evidenceframe.evidencer.core.ExecutionContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class SummaryWriter {

    private static final DateTimeFormatter UTC_FORMAT =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    public void write(
            ExecutionContext context,
            List<CollectorResult> results
    ) throws IOException {

        Path summaryFile = context.outputRoot().resolve("summary.txt");

        Files.createDirectories(context.outputRoot());

        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("ISO/IEC 27001 — Evidence Collection Summary\n\n");

        // Run metadata
        sb.append("Run Date (UTC): ")
                .append(UTC_FORMAT.format(context.runStartedAt()))
                .append("\n");
        sb.append("Audit Type: ")
                .append(context.auditType())
                .append("\n");
        sb.append("Run Mode: ")
                .append(context.runMode())
                .append("\n\n");

        // Collected evidence
        sb.append("Evidence Collected:\n");
        boolean anyCollected = false;

        for (CollectorResult result : results) {
            if (result.status() == CollectorResult.Status.SUCCESS) {
                sb.append("✔ ")
                        .append(result.collectorName())
                        .append("\n");
                anyCollected = true;
            }
        }

        if (!anyCollected) {
            sb.append("None\n");
        }

        sb.append("\n");

        // Missing / skipped evidence
        sb.append("Evidence Not Collected:\n");
        boolean anyMissing = false;

        for (CollectorResult result : results) {
            if (result.status() != CollectorResult.Status.SUCCESS) {
                sb.append("✖ ")
                        .append(result.collectorName())
                        .append("\n");
                if (result.message() != null && !result.message().isBlank()) {
                    sb.append("  Reason: ")
                            .append(result.message())
                            .append("\n");
                }
                anyMissing = true;
            }
        }

        if (!anyMissing) {
            sb.append("None\n");
        }

        sb.append("\n");

        // Integrity notes
        sb.append("Integrity Notes:\n");
        sb.append("- Evidence was collected using read-only access.\n");
        sb.append("- No systems were modified during this run.\n");
        sb.append("- Missing evidence is reported explicitly.\n\n");

        sb.append("End of Summary\n");

        Files.writeString(summaryFile, sb.toString());
    }
}
