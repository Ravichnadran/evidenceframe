package com.evidenceframe.evidencer.core;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;

public final class ExecutionContext {

    private final String auditType;
    private final Path outputRoot;
    private final Instant runStartedAt;
    private final RunMode runMode;

    public ExecutionContext(
            String auditType,
            Path outputRoot,
            Instant runStartedAt,
            RunMode runMode
    ) {
        this.auditType = Objects.requireNonNull(auditType, "auditType must not be null");
        this.outputRoot = Objects.requireNonNull(outputRoot, "outputRoot must not be null");
        this.runStartedAt = Objects.requireNonNull(runStartedAt, "runStartedAt must not be null");
        this.runMode = Objects.requireNonNull(runMode, "runMode must not be null");
    }

    public String auditType() {
        return auditType;
    }

    public Path outputRoot() {
        return outputRoot;
    }

    public Instant runStartedAt() {
        return runStartedAt;
    }

    public RunMode runMode() {
        return runMode;
    }

    @Override
    public String toString() {
        return "ExecutionContext{" +
                "auditType='" + auditType + '\'' +
                ", outputRoot=" + outputRoot +
                ", runStartedAt=" + runStartedAt +
                ", runMode=" + runMode +
                '}';
    }
}
