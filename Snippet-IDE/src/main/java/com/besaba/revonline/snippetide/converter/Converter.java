package com.besaba.revonline.snippetide.converter;

public interface Converter<S, D> {
  D convert(final S source);

  Class<D> getDestinationClass();
  Class<S> getSourceClass();
}
