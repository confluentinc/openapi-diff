package com.qdesrame.openapi.diff.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;

@Getter
@Setter
@Accessors(chain = true)
public class ChangedMaxPageItems implements Changed {

    private final Integer oldValue;
    private final Integer newValue;
    private final DiffContext context;
    private final Change.Type changeType;

    public ChangedMaxPageItems(Integer oldValue, Integer newValue, Change.Type changeType, DiffContext context) {
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changeType = changeType;
        this.context = context;
    }

    @Override
    public DiffResult isChanged() {
        switch (changeType) {
            case ADDED:
                return DiffResult.INCOMPATIBLE;
            case REMOVED:
                return DiffResult.COMPATIBLE;
            case CHANGED:
                if (oldValue.equals(newValue)) {
                    return DiffResult.NO_CHANGES;
                } else if (newValue < oldValue) {
                    return DiffResult.INCOMPATIBLE;
                } else {
                    return DiffResult.COMPATIBLE;
                }
            default:
                return DiffResult.UNKNOWN;
        }
    }
}
