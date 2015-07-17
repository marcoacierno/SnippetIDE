package com.besaba.revonline.snippetide.api.configuration;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

public interface Configuration {
  /**
   * Called when the configuration should load the settings
   *
   * @param inputStream In this stream you can found the settings of
   *                    the user.
   *                    An implementation can decide what is inside this
   *                    stream.
   *                    Example: If the implementation
   *                    uses JSON to save and read settings, it should
   *                    assume that this stream contains a valid json.
   *                    If it doesn't, it is allowed to throw
   *                    any exception you want since it's not
   *                    your job to convert the stream to your
   *                    format.
   *                    Do not close the stream. You don't own it.
   * @throws ConfigurationLoadFailedException If something went wrong during the load process
   */
  void load(@NotNull final InputStream inputStream) throws ConfigurationLoadFailedException;

  /**
   * Called when the configuration should save ALL the settings
   *
   * @param outputStream Write in this stream the settings of the
   *                     user in the format of the implementation.
   *                     Do not close the stream. You don't own it.
   *
   * @throws ConfigurationSaveFailedException Throw if something went wrong during the
   *                     save process
   */
  void save(@NotNull final OutputStream outputStream) throws ConfigurationSaveFailedException;

  /**
   * @param name The setting name
   * @return The setting as integer (if it's an integer)
   *         or NumberFormatException if not
   */
  @NotNull
  OptionalInt getAsInt(@NotNull final String name);

  /**
   * @param name The setting name
   * @return The setting as double (if it's a double)
   *         or NumberFormatException if not
   */
  @NotNull
  OptionalDouble getAsDouble(@NotNull final String name);

  /**
   * @param name The setting name
   * @return The setting as long (if it's a long)
   *         or NumberFormatException if not
   */
  @NotNull
  OptionalLong getAsLong(@NotNull final String name);

  /**
   * @param name The setting name
   * @return The setting as string. (if it's a string)
   */
  @NotNull
  Optional<String> getAsString(@NotNull final String name);

  /**
   * @param name The setting name
   * @return The setting as boolean (if it's a boolean)
   *         or NumberFormatException if not
   */
  @NotNull
  Optional<Boolean> getAsBoolean(@NotNull final String name);

  /**
   * @param name The setting name
   * @return The setting as boolean (if it's a boolean)
   *         or NumberFormatException if not
   */
  @NotNull
  Optional<Float> getAsFloat(@NotNull final String name);

  Optional<String[]> getAsArray(@NotNull final String name);

  /**
   * Set the value
   *
   * @param name The name of the setting
   * @param value The value to save
   * @param <T> The type of the value
   */
  <T> void set(@NotNull final String name, @NotNull final T value);
}
