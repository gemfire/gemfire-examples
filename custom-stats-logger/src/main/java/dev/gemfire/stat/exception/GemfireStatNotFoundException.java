package dev.gemfire.stat.exception;

import org.apache.geode.GemFireException;

public class GemfireStatNotFoundException  extends GemFireException {


    public GemfireStatNotFoundException(String message) {
        super(message);
    }
}
