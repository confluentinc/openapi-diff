package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.Change;
import com.qdesrame.openapi.diff.model.Changed;
import com.qdesrame.openapi.diff.model.DiffContext;

public interface ExtensionDiff<T> {

  ExtensionDiff<T> setOpenApiDiff(OpenApiDiff openApiDiff);

  String getName();

  Changed diff(Change<T> extension, DiffContext context);

  default boolean isParentApplicable(
      Change.Type type, T object, T extension, DiffContext context) {
    return true;
  }
}
