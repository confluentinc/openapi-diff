package com.qdesrame.openapi.test;

import static com.qdesrame.openapi.test.TestUtils.assertOpenApiBackwardCompatible;
import static com.qdesrame.openapi.test.TestUtils.assertOpenApiBackwardIncompatible;

import com.qdesrame.openapi.diff.OpenApiCompare;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * Created by adarsh.sharma on 24/12/17.
 */
public class BackwardCompatibilityTest {
  private final String OPENAPI_DOC1 = "backwardCompatibility/bc_1.yaml";
  private final String OPENAPI_DOC2 = "backwardCompatibility/bc_2.yaml";
  private final String OPENAPI_DOC3 = "backwardCompatibility/bc_3.yaml";
  private final String OPENAPI_DOC4 = "backwardCompatibility/bc_4.yaml";
  private final String OPENAPI_DOC5 = "backwardCompatibility/bc_5.yaml";

  @Test
  public void testNoChange() {
    assertOpenApiBackwardCompatible(OPENAPI_DOC1, OPENAPI_DOC1, false);
  }

  @Test
  public void testApiAdded() {
    assertOpenApiBackwardCompatible(OPENAPI_DOC1, OPENAPI_DOC2, true);
  }

  @Test
  public void testApiMissing() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC2, OPENAPI_DOC1);
  }

  @Test
  public void testApiChangedOperationAdded() {
    assertOpenApiBackwardCompatible(OPENAPI_DOC2, OPENAPI_DOC3, true);
  }

  @Test
  public void testApiChangedOperationMissing() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC3, OPENAPI_DOC2);
  }

  @Test
  public void testApiOperationChanged() {
    assertOpenApiBackwardCompatible(OPENAPI_DOC2, OPENAPI_DOC4, true);
  }

  @Test
  public void testApiReadWriteOnlyPropertiesChanged() {
    assertOpenApiBackwardCompatible(OPENAPI_DOC1, OPENAPI_DOC5, true);
  }

  @Test
  public void testSchemaMaximumValueAdded() {
    OpenAPI spec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC5);
    OpenAPI specDiffMaxValue = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC5);
    setMockMaximumValue(specDiffMaxValue, 123);
    assertOpenApiBackwardIncompatible(spec, specDiffMaxValue);
  }

  @Test
  public void testSchemaMaximumValueChanged() {
    OpenAPI spec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC5);
    setMockMaximumValue(spec, 123);
    OpenAPI specDiffMaxValue = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC5);
    setMockMaximumValue(specDiffMaxValue, 456);
    assertOpenApiBackwardIncompatible(spec, specDiffMaxValue);
  }

  @Test
  public void testSchemaMaximumValueRemoved() {
    OpenAPI spec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC5);
    OpenAPI specDiffMaxValue = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC5);
    setMockMaximumValue(specDiffMaxValue, 123);
    assertOpenApiBackwardIncompatible(specDiffMaxValue, spec);
  }

  @Test
  public void testSchemaMinimumValueAdded() {
    OpenAPI spec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC5);
    OpenAPI specDiffMaxValue = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC5);
    setMockMinimumValue(specDiffMaxValue, 123);
    assertOpenApiBackwardIncompatible(spec, specDiffMaxValue);
  }

  @Test
  public void testSchemaMinimumValueRemoved() {
    OpenAPI spec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC5);
    OpenAPI specDiffMaxValue = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC5);
    setMockMinimumValue(specDiffMaxValue, 123);
    assertOpenApiBackwardIncompatible(specDiffMaxValue, spec);
  }
  
  @Test
  public void testSchemaMinimumValueChanged() {
    OpenAPI spec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC5);
    setMockMinimumValue(spec, 123);
    OpenAPI specDiffMaxValue = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC5);
    setMockMinimumValue(specDiffMaxValue, 456);
    assertOpenApiBackwardIncompatible(spec, specDiffMaxValue);
  }

  private void setMockMaximumValue(OpenAPI spec, int maxValue) {
    spec.getComponents()
            .getSchemas()
            .get("Dog")
            .setMaximum(new BigDecimal(maxValue));
  }

  private void setMockMinimumValue(OpenAPI spec, int minValue) {
    spec.getComponents()
            .getSchemas()
            .get("Dog")
            .setMinimum(new BigDecimal(minValue));
  }
}

