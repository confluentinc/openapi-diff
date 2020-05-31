package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.Change;
import com.qdesrame.openapi.diff.model.Changed;
import com.qdesrame.openapi.diff.model.ChangedExtensibleEnum;
import com.qdesrame.openapi.diff.model.DiffContext;

import java.util.List;

public class ExtensibleEnumDiff<T> implements ExtensionDiff<List<T>> {

  OpenApiDiff openApiDiff;

  @Override
  public ExtensionDiff<List<T>> setOpenApiDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
    return this;
  }

  @Override
  public String getName() {
    return "extensible-enum";
  }

  @Override
  public Changed diff(Change<List<T>> extension, DiffContext context) {
    return ListDiff.diff(new ChangedExtensibleEnum<>(extension.getOldValue(), extension.getNewValue(), context));
  }

  @Override
  public boolean isParentApplicable(Change.Type type, List<T> object, List<T> extension, DiffContext context) {
    return false;
  }
}
