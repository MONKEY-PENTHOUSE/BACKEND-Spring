package com.monkeypenthouse.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Log {
    default Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }
}
