package com.evidenceframe.evidencer.core;

import java.util.List;
import java.util.Objects;

public final class CollectorResult {

    private final String collectorName;
    private final Status status;
    private final List<String> producedFiles;
    private final String message;

    public CollectorResult(
            String collectorName,
            Status status,
            List<String> producedFiles,
            String message
    ) {
        this.collectorName = Objects.requireNonNull(collectorName);
        this.status = Objects.requireNonNull(status);
        this.producedFiles = List.copyOf(
                producedFiles == null ? List.of() : producedFiles
        );
        this.message = message;
    }

    public String collectorName() {
        return collectorName;
    }

    public Status status() {
        return status;
    }

    public List<String> producedFiles() {
        return producedFiles;
    }

    public String message() {
        return message;
    }

    public enum Status {
        SUCCESS,
        PARTIAL,
        FAILED,
        SKIPPED
    }
}
