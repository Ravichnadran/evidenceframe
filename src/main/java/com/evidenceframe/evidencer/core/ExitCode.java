package com.evidenceframe.evidencer.core;

public enum ExitCode {

    SUCCESS(0),
    PARTIAL(2),
    FAILURE(3);

    private final int code;

    ExitCode(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static ExitCode fromResults(Iterable<CollectorResult> results) {
        boolean anyFailed = false;
        boolean anyPartial = false;

        for (CollectorResult result : results) {
            if (result.status() == CollectorResult.Status.FAILED) {
                anyFailed = true;
            } else if (result.status() == CollectorResult.Status.PARTIAL) {
                anyPartial = true;
            }
        }

        if (anyFailed) {
            return FAILURE;
        }
        if (anyPartial) {
            return PARTIAL;
        }
        return SUCCESS;
    }
}
