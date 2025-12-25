package com.evidenceframe.evidencer.core;

import java.util.List;

public final class DummyCollector implements Collector {

    @Override
    public String name() {
        return "Dummy Collector";
    }

    @Override
    public CollectorResult collect(ExecutionContext context) {
        if (context.runMode() == RunMode.DRY_RUN) {
            return new CollectorResult(
                    name(),
                    CollectorResult.Status.SKIPPED,
                    List.of(),
                    "Dry run – no data collected"
            );
        }

        return new CollectorResult(
                name(),
                CollectorResult.Status.SUCCESS,
                List.of("dummy.txt"),
                "Dummy collector executed successfully"
        );
    }
}
