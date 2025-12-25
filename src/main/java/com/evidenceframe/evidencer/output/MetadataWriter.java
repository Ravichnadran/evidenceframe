package com.evidenceframe.evidencer.output;

import com.evidenceframe.evidencer.core.ExecutionContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class MetadataWriter {

    private static final DateTimeFormatter UTC_FORMAT =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    public void write(ExecutionContext context) throws IOException {

        Path metadataDir = context.outputRoot().resolve("metadata");
        Files.createDirectories(metadataDir);

        Path metadataFile = metadataDir.resolve("run_metadata.json");

        String json = "{\n" +
                "  \"auditType\": \"" + context.auditType() + "\",\n" +
                "  \"runMode\": \"" + context.runMode() + "\",\n" +
                "  \"startedAtUtc\": \"" + UTC_FORMAT.format(context.runStartedAt()) + "\"\n" +
                "}";

        Files.writeString(metadataFile, json);
    }
}
