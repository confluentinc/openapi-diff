package com.qdesrame.openapi.test;

import com.qdesrame.openapi.diff.OpenApiCompare;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.qdesrame.openapi.test.TestUtils.assertOpenApiBackwardCompatible;
import static com.qdesrame.openapi.test.TestUtils.assertOpenApiBackwardIncompatible;

import io.swagger.v3.oas.models.media.StringSchema;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by adarsh.sharma on 24/12/17.
 */
public class BackwardCompatibilityTest {
  private final String OPENAPI_DOC1 = "backwardCompatibility/bc_1.yaml";
  private final String OPENAPI_DOC2 = "backwardCompatibility/bc_2.yaml";
  private final String OPENAPI_DOC3 = "backwardCompatibility/bc_3.yaml";
  private final String OPENAPI_DOC4 = "backwardCompatibility/bc_4.yaml";
  private final String OPENAPI_DOC5 = "backwardCompatibility/bc_5.yaml";
  private final String OPENAPI_DOC6 = "backwardCompatibility/bc_6.yaml";
  private final String OPENAPI_DOC7 = "backwardCompatibility/bc_7.yaml";
  private final String OPENAPI_DOC8 = "backwardCompatibility/bc_8.yaml";

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
  public void testApiReadWriteOnlyPropertiesChanged() {
    assertOpenApiBackwardCompatible(OPENAPI_DOC1, OPENAPI_DOC5, true);
  }

  @Test
  public void testSchemaDefaultChanged() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC4, OPENAPI_DOC6);
  }

  @Test
  public void testSchemaDefaultRemoved() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC4, OPENAPI_DOC7);
  }

  @Test
  public void testSchemaDefaultAdded() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC7, OPENAPI_DOC4);
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
  public void testApiResponseDefaultAdded() {
    OpenAPI spec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC5);
    OpenAPI specWithDefault = specWithDefaultResponse(OPENAPI_DOC5, new ApiResponse().description("hi"));
    assertOpenApiBackwardCompatible(spec, specWithDefault, true);
  }

  @Test
  public void testApiResponseDefaultRemoved() {
    OpenAPI spec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC5);
    OpenAPI specWithDefault = specWithDefaultResponse(OPENAPI_DOC5, new ApiResponse().description("hi"));
    assertOpenApiBackwardIncompatible(specWithDefault, spec);
  }

  @Test
  public void testApiResponseDefaultChanged() {
    Map<String, Header> headers = new HashMap<>();
    headers.put("hi", new Header().description("hi"));
    OpenAPI specWithDefault1 = specWithDefaultResponse(OPENAPI_DOC5, new ApiResponse().headers(headers));
    OpenAPI specWithDefault2 = specWithDefaultResponse(OPENAPI_DOC5, new ApiResponse());
    assertOpenApiBackwardIncompatible(specWithDefault1, specWithDefault2);
  }

  // Test to make sure changing description doesn't break compatibility.
  @Test
  public void testApiResponseDefaultDescriptionChanged() {
    OpenAPI specWithDefault1 = specWithDefaultResponse(OPENAPI_DOC5, new ApiResponse().description("hi"));
    OpenAPI specWithDefault2 = specWithDefaultResponse(OPENAPI_DOC5, new ApiResponse().description("hey"));
    assertOpenApiBackwardCompatible(specWithDefault1, specWithDefault2, true);
  }

  private OpenAPI specWithDefaultResponse(String baseSpecPath, ApiResponse defaultResponse) {
    OpenAPI specWithDefault = OpenApiCompare.getOpenApiParser().read(baseSpecPath);
    specWithDefault.getPaths().get("/pet/findByStatus")
            .getGet()
            .getResponses()
            .setDefault(defaultResponse);
    return specWithDefault;
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

  @Test
  public void testEnumAdded() {
    OpenAPI spec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC8);
    OpenAPI modSpec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC8);
    StringSchema status = getDogStatus(modSpec);
    status.addEnumItem("zombie");
    assertOpenApiBackwardIncompatible(spec, modSpec);
  }

  @Test
  public void testExtensibleEnumAdded() {
    OpenAPI spec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC8);
    StringSchema status = getDogStatus(spec);
    status.addExtension("x-extensible-enum", status.getEnum());
    status._enum(null);

    OpenAPI modSpec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC8);
    StringSchema modStatus = getDogStatus(modSpec);
    List<String> extendedEnum = modStatus.getEnum();
    extendedEnum.add("zombie");
    modStatus.addExtension("x-extensible-enum", extendedEnum);
    modStatus._enum(null);
    assertOpenApiBackwardCompatible(spec, modSpec, true);
  }

  private StringSchema getDogStatus(OpenAPI spec) {
    return (StringSchema) spec.getComponents()
            .getSchemas()
            .get("Dog")
            .getProperties()
            .get("status");
  }

  @Test
  public void testExtensibleEnumRemoved() {
    OpenAPI spec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC8);
    StringSchema status = getDogStatus(spec);
    status.addExtension("x-extensible-enum", status.getEnum());
    status._enum(null);

    OpenAPI modSpec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC8);
    StringSchema modStatus = getDogStatus(modSpec);
    List<String> extendedEnum = modStatus.getEnum();
    extendedEnum.remove(0);
    modStatus.addExtension("x-extensible-enum", extendedEnum);
    modStatus._enum(null);
    assertOpenApiBackwardIncompatible(spec, modSpec);
  }
}

