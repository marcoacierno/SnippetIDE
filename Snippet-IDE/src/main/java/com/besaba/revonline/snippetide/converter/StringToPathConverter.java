package com.besaba.revonline.snippetide.converter;

import java.nio.file.Path;
import java.nio.file.Paths;

public class StringToPathConverter implements Converter<String, Path> {
  @Override
  public Path convert(final String source) {
    return Paths.get(source);
  }

  @Override
  public Class<Path> getDestinationClass() {
    return Path.class;
  }

  @Override
  public Class<String> getSourceClass() {
    return String.class;
  }
}
