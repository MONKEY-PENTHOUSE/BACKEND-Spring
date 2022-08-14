package com.monkeypenthouse.core.loggging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class LoggingFilter extends Filter<ILoggingEvent> {

    private String levels;
    private Level[] level;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (level == null && levels != null) {
            setLevels();
        }

        if (level != null) {
            for (Level lev : level) {
                if (lev == event.getLevel()) {
                    return FilterReply.ACCEPT;
                }
            }
        }
        return FilterReply.DENY;
    }

    private void setLevels() {
        if (!levels.isEmpty()) {
            level = new Level[levels.split("\\|").length];
            for (int i = 0; i < level.length; i++) {
                level[i] = Level.valueOf(levels.split("\\|")[i]);
            }
        }
    }
}
