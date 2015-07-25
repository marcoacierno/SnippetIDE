package com.besaba.revonline.snippetide.run;

import com.besaba.revonline.snippetide.api.datashare.StructureFieldInfo;
import org.controlsfx.control.PropertySheet;

import java.util.function.Predicate;

public class SimplePropertySheetItem implements PropertySheet.Item {
  private final String name;
  private final Class<?> clazz;
  private final String description;
  private Object value;
  private Predicate validator;

  public SimplePropertySheetItem(final String name, final StructureFieldInfo structureFieldInfo) {
    this.name = name;
    this.validator = structureFieldInfo.getValidator();
    this.clazz = structureFieldInfo.getType();
    this.value = structureFieldInfo.getDefaultValue();
    this.description = structureFieldInfo.getDescription();
  }

  @SuppressWarnings("unchecked")
  public boolean check(final Object other) {
    return validator.test(clazz.cast(other));
  }

  @Override
  public Class<?> getType() {
    return clazz;
  }

  @Override
  public String getCategory() {
    return "Run";
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Object getValue() {
    return value;
  }

  @Override
  public void setValue(final Object o) {
    this.value = o;
  }
}
