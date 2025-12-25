package com.evidenceframe.evidencer.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class EvidenceRun {

    private final ExecutionContext context;
    private final List<Collector> collectors;
    private final List<CollectorResult> results = new ArrayList<>();

    public EvidenceRun(ExecutionContext context, List<Collector> collectors) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.collectors = List.copyOf(
                Objects.requireNonNull(collectors, "collectors must not be null")
        );
    }

    public void execute() {
        for (Collector collector : collectors) {
            try {
                CollectorResult result = collector.collect(context);
                results.add(result);
            } catch (Exception ex) {
                results.add(
                        new CollectorResult(
                                collector.name(),
                                CollectorResult.Status.FAILED,
                                List.of(),
                                "Unexpected error: " + ex.getMessage()
                        )
                );
            }
        }
    }

    public ExecutionContext context() {
        return context;
    }

    public List<CollectorResult> results() {
        return List.copyOf(results);
    }
}
