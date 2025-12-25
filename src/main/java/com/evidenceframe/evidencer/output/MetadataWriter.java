package com.evidenceframe.evidencer.output;

import com.evidenceframe.evidencer.core.ExecutionContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class MetadataWriter {

    private static final DateTimeFormatter UTC_FORMAT =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);
    private static final ObjectMapper mapper = new ObjectMapper();

    public void write(ExecutionContext context) throws IOException {

        Path metadataDir = context.outputRoot().resolve("metadata");
        Files.createDirectories(metadataDir);

        Path metadataFile = metadataDir.resolve("run_metadata.json");

        ObjectNode root = mapper.createObjectNode();
        root.put("auditType", context.auditType());
        root.put("runMode", context.runMode().name());
        root.put("startedAtUtc", UTC_FORMAT.format(context.runStartedAt()));

        mapper.writeValue(metadataFile.toFile(), root);
    }
}
