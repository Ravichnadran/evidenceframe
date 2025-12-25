package com.evidenceframe.evidencer.core;

public interface Collector {

    String name();

    CollectorResult collect(ExecutionContext context);
}
