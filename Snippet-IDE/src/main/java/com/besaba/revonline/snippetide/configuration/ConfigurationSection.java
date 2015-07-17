package com.besaba.revonline.snippetide.configuration;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

public interface ConfigurationSection {
  void load(@NotNull final InputStream inputStream);
  void save(@NotNull final OutputStream outputStream) throws IOException;
  <T> Optional<T> get(@NotNull final String name);
  <T> void set(@NotNull final String name, @NotNull final T value);
  boolean remove(@NotNull final String name);
}
