package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.Change;
import com.qdesrame.openapi.diff.model.Changed;
import com.qdesrame.openapi.diff.model.ChangedMaxPageItems;
import com.qdesrame.openapi.diff.model.DiffContext;

public class MaxPageItemsDiff implements ExtensionDiff<Integer> {

    OpenApiDiff openApiDiff;

    @Override
    public ExtensionDiff<Integer> setOpenApiDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        return this;
    }

    @Override
    public String getName() {
        return "max-page-items";
    }

    @Override
    public Changed diff(Change<Integer> change, DiffContext context) {
        return new ChangedMaxPageItems(change.getOldValue(), change.getNewValue(), change.getType(), context);
    }

    @Override
    public boolean isParentApplicable(Change.Type type, Integer object, Integer extension, DiffContext context) {
        return false;
    }
}
