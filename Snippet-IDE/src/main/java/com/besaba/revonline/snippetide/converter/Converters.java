package com.besaba.revonline.snippetide.converter;

import com.google.common.collect.ImmutableTable;
import org.reflections.Reflections;

import java.util.Set;

public class Converters {
  private final static ImmutableTable<Class<?>, Class<?>, Converter<?, ?>> converters;

  static {
    final ImmutableTable.Builder<Class<?>, Class<?>, Converter<?, ?>> builder = ImmutableTable.builder();
    final Reflections reflections = new Reflections();
    final Set<Class<? extends Converter>> subtypes = reflections.getSubTypesOf(Converter.class);

    for (final Class<? extends Converter> subtype : subtypes) {
      try {
        final Converter<?, ?> converter = subtype.newInstance();
        builder.put(converter.getSourceClass(), converter.getDestinationClass(), converter);
      } catch (InstantiationException | IllegalAccessException e) {
        // todo skip log it
      }
    }

    converters = builder.build();
  }

  @SuppressWarnings("unchecked")
  public <S, D> D convert(final Class<S> source, final Class<D> destination, final S value) {
    final Converter<S, D> converter = (Converter) converters.get(source, destination);
    return destination.cast(converter.convert(source.cast(value)));
  }
}
