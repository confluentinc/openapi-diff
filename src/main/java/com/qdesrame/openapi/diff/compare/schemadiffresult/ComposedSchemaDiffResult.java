package com.qdesrame.openapi.diff.compare.schemadiffresult;

import com.qdesrame.openapi.diff.compare.MapKeyDiff;
import com.qdesrame.openapi.diff.compare.OpenApiDiff;
import com.qdesrame.openapi.diff.compare.SchemaDiff;
import com.qdesrame.openapi.diff.model.ChangedOneOfSchema;
import com.qdesrame.openapi.diff.model.ChangedSchema;
import com.qdesrame.openapi.diff.model.DiffContext;
import com.qdesrame.openapi.diff.utils.RefPointer;
import com.qdesrame.openapi.diff.utils.RefType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.Schema;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

/**
 * Created by adarsh.sharma on 20/12/17.
 */
public class ComposedSchemaDiffResult extends SchemaDiffResult {
  private static RefPointer<Schema> refPointer = new RefPointer<>(RefType.SCHEMAS);

  public ComposedSchemaDiffResult(OpenApiDiff openApiDiff) {
    super(openApiDiff);
  }

  @Override
  public <T extends Schema<X>, X> Optional<ChangedSchema> diff(
      HashSet<String> refSet,
      Components leftComponents,
      Components rightComponents,
      T left,
      T right,
      DiffContext context) {
    if (left instanceof ComposedSchema) {
      ComposedSchema leftComposedSchema = (ComposedSchema) left;
      ComposedSchema rightComposedSchema = (ComposedSchema) right;
      if (CollectionUtils.isNotEmpty(leftComposedSchema.getOneOf())
          || CollectionUtils.isNotEmpty(rightComposedSchema.getOneOf())) {

        Discriminator leftDis = leftComposedSchema.getDiscriminator();
        Discriminator rightDis = rightComposedSchema.getDiscriminator();
        if (leftDis != null && leftDis.getPropertyName() == null) {
          throw new IllegalArgumentException("discriminator in old schema should have required property 'propertyName'");
        }
        if (rightDis != null && rightDis.getPropertyName() == null) {
          throw new IllegalArgumentException("discriminator in new schema should have required property 'propertyName'");
        }
        if (leftDis == null && rightDis != null ||
            leftDis != null && rightDis == null ||
            leftDis != null && !Objects.equals(leftDis.getPropertyName(), rightDis.getPropertyName())) {
          changedSchema.setDiscriminatorPropertyChanged(true);
          changedSchema.setOldSchema(left);
          changedSchema.setNewSchema(right);
          changedSchema.setContext(context);
          return Optional.of(changedSchema);
        }
        ChangedOneOfSchema changedOneOfSchema;
        if (leftDis == null & rightDis == null) {
          changedOneOfSchema = compareOneOfWithNoDiscriminators(leftComponents, rightComponents,
              leftComposedSchema, rightComposedSchema, refSet, context);
        } else {
          // Both discriminators are set.
          changedOneOfSchema = compareOneOfWithDiscriminators(leftComponents, rightComponents,
              leftComposedSchema, rightComposedSchema, refSet, context);
        }
        changedSchema.setOneOfSchema(changedOneOfSchema);
      }
      return super.diff(refSet, leftComponents, rightComponents, left, right, context);
    } else {
      return openApiDiff.getSchemaDiff().getTypeChangedSchema(left, right, context);
    }
  }

  // Compare oneOf schemas with no discriminators.
  // TODO: Compare in an unordered way. The current comparison is ordered,
  //  meaning that oldOneOf[0] is compared to newOneOf[0], oldOneOf[1] is compared to newOneOf[1],
  //  and so on.
  private ChangedOneOfSchema compareOneOfWithNoDiscriminators(
      Components leftComponents,
      Components rightComponents,
      ComposedSchema leftComposedSchema,
      ComposedSchema rightComposedSchema,
      HashSet<String> refSet,
      DiffContext context) {
    Map<String, Schema> leftSchemas = getResolvedComposedSchemas(leftComponents, leftComposedSchema);
    Map<String, Schema> rightSchemas = getResolvedComposedSchemas(rightComponents, rightComposedSchema);
    MapKeyDiff<String, Schema> mapKeyDiff = MapKeyDiff.diff(leftSchemas, rightSchemas);
    Map<String, ChangedSchema> changedSchemas = new LinkedHashMap<>();
    for (String key : mapKeyDiff.getSharedKey()) {
      Schema leftSchema = leftSchemas.get(key);
      Schema rightSchema = rightSchemas.get(key);
      Optional<ChangedSchema> changedSchema =
          openApiDiff
              .getSchemaDiff()
              .diff(refSet, leftSchema, rightSchema, context.copyWithRequired(true));
      changedSchema.ifPresent(schema -> changedSchemas.put(key, schema));
    }
    return new ChangedOneOfSchema(null, null, context)
        .setIncreased(mapKeyDiff.getIncreased())
        .setMissing(mapKeyDiff.getMissing())
        .setChanged(changedSchemas);
  }

  private Map<String, Schema> getResolvedComposedSchemas(Components components, ComposedSchema composedSchema) {
    Map<String, Schema> schemas = new LinkedHashMap<>();
    for (int i = 0; i < composedSchema.getOneOf().size(); i++) {
      Schema schema = composedSchema.getOneOf().get(i);
      SchemaDiff.resolveComposedSchema(components, schema);
      // Hacky key, but I guess it works?
      schemas.put(String.valueOf(i), schema);
    }
    return schemas;
  }

  private ChangedOneOfSchema compareOneOfWithDiscriminators(
      Components leftComponents,
      Components rightComponents,
      ComposedSchema leftComposedSchema,
      ComposedSchema rightComposedSchema,
      HashSet<String> refSet,
      DiffContext context) {
    Map<String, String> leftMapping = getMapping(leftComposedSchema);
    Map<String, String> rightMapping = getMapping(rightComposedSchema);

    MapKeyDiff<String, Schema> mappingDiff =
        MapKeyDiff.diff(
            getSchema(leftComponents, leftMapping), getSchema(rightComponents, rightMapping));
    Map<String, ChangedSchema> changedMapping = new LinkedHashMap<>();
    for (String key : mappingDiff.getSharedKey()) {
      Schema leftSchema = new Schema();
      leftSchema.set$ref(leftMapping.get(key));
      Schema rightSchema = new Schema();
      rightSchema.set$ref(rightMapping.get(key));
      Optional<ChangedSchema> changedSchema =
          openApiDiff
              .getSchemaDiff()
              .diff(refSet, leftSchema, rightSchema, context.copyWithRequired(true));
      changedSchema.ifPresent(schema -> changedMapping.put(key, schema));
    }
    return new ChangedOneOfSchema(leftMapping, rightMapping, context)
        .setIncreased(mappingDiff.getIncreased())
        .setMissing(mappingDiff.getMissing())
        .setChanged(changedMapping);
  }

  private Map<String, Schema> getSchema(Components components, Map<String, String> mapping) {
    Map<String, Schema> result = new LinkedHashMap<>();
    mapping.forEach(
        (key, value) -> result.put(key, refPointer.resolveRef(components, new Schema(), value)));
    return result;
  }

  private Map<String, String> getMapping(ComposedSchema composedSchema) {
    Map<String, String> reverseMapping = new LinkedHashMap<>();
    for (Schema schema : composedSchema.getOneOf()) {
      String ref = schema.get$ref();
      if (ref == null) {
        throw new IllegalArgumentException("invalid oneOf schema");
      }
      String schemaName = refPointer.getRefName(ref);
      if (schemaName == null) {
        throw new IllegalArgumentException("invalid schema: " + ref);
      }
      reverseMapping.put(ref, schemaName);
    }

    if (composedSchema.getDiscriminator().getMapping() != null) {
      for (String ref : composedSchema.getDiscriminator().getMapping().keySet()) {
        reverseMapping.put(composedSchema.getDiscriminator().getMapping().get(ref), ref);
      }
    }

    return reverseMapping
        .entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
  }
}
