package com.qdesrame.openapi.test;

import static com.qdesrame.openapi.test.TestUtils.assertOpenApiAreEquals;
import static com.qdesrame.openapi.test.TestUtils.assertOpenApiBackwardIncompatible;
import static com.qdesrame.openapi.test.TestUtils.assertOpenApiChangedEndpoints;

import com.qdesrame.openapi.diff.OpenApiCompare;
import com.qdesrame.openapi.diff.model.ChangedOpenApi;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

/**
 * Created by adarsh.sharma on 19/12/17.
 */
public class OneOfDiffTest {

  private final String OPENAPI_DOC1 = "oneOf_diff_1.yaml";
  private final String OPENAPI_DOC2 = "oneOf_diff_2.yaml";
  private final String OPENAPI_DOC3 = "oneOf_diff_3.yaml";
  private final String OPENAPI_DOC4 = "composed_schema_1.yaml";
  private final String OPENAPI_DOC5 = "composed_schema_2.yaml";
  private final String OPENAPI_DOC6 = "oneOf_discriminator-changed_1.yaml";
  private final String OPENAPI_DOC7 = "oneOf_discriminator-changed_2.yaml";

  @Test
  public void testDiffSame() {
    assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
  }

  @Test
  public void testDiffDifferentMapping() {
    assertOpenApiChangedEndpoints(OPENAPI_DOC1, OPENAPI_DOC2);
  }

  @Test
  public void testDiffSameWithOneOf() {
    assertOpenApiAreEquals(OPENAPI_DOC2, OPENAPI_DOC3);
  }

  @Test
  public void testComposedSchema() {
    assertOpenApiBackwardIncompatible(OPENAPI_DOC4, OPENAPI_DOC5);
  }

  @Test
  public void testOneOfDiscrimitatorChanged() {
    // The oneOf 'discriminator' changed: 'realtype' -> 'othertype':
    assertOpenApiBackwardIncompatible(OPENAPI_DOC6, OPENAPI_DOC7);
  }

  @Test
  public void testOneOfWithNoDiscriminatorChanged() {
    OpenAPI oldSpec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC2);
    OpenAPI newSpec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC2);
    oldSpec.getComponents().getSchemas().get("Pet").setDiscriminator(null);
    newSpec.getComponents().getSchemas().get("Pet").setDiscriminator(null);
    Schema schema = newSpec.getComponents().getSchemas().get("Dog");
    schema.getProperties().remove("bark");
    assertOpenApiBackwardIncompatible(oldSpec, newSpec);
  }

  @Test
  public void testOldOneOfWithDiscriminatorWithNoPropertyName() {
    OpenAPI oldSpec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC2);
    OpenAPI newSpec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC2);
    Discriminator disc = newSpec.getComponents().getSchemas().get("Pet").getDiscriminator();
    disc.setPropertyName(null);
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      assertOpenApiBackwardIncompatible(oldSpec, newSpec);
    });
  }

  @Test
  public void testNewOneOfWithDiscriminatorWithNoPropertyName() {
    OpenAPI oldSpec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC2);
    OpenAPI newSpec = OpenApiCompare.getOpenApiParser().read(OPENAPI_DOC2);
    Discriminator disc = oldSpec.getComponents().getSchemas().get("Pet").getDiscriminator();
    disc.setPropertyName(null);
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      assertOpenApiBackwardIncompatible(oldSpec, newSpec);
    });
  }
}
