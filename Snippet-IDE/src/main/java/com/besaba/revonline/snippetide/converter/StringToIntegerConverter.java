package com.besaba.revonline.snippetide.converter;

public class StringToIntegerConverter implements Converter<String, Integer> {
  @Override
  public Integer convert(final String source) {
    return Integer.parseInt(source);
  }

  @Override
  public Class<Integer> getDestinationClass() {
    return Integer.class;
  }

  @Override
  public Class<String> getSourceClass() {
    return String.class;
  }
}
