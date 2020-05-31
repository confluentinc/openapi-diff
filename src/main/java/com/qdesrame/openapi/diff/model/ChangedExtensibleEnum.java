package com.qdesrame.openapi.diff.model;

import java.util.List;

public class ChangedExtensibleEnum<T> extends ChangedList<T> {

  public ChangedExtensibleEnum(List<T> oldValue, List<T> newValue, DiffContext context) {
    super(oldValue, newValue, context);
  }

  @Override
  public DiffResult isItemsChanged() {
    // Only incompatible if an enum value is removed.
    if (this.getMissing().isEmpty()) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }
}
