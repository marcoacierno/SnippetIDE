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
   * @param inputStream It's where you should read the settings.
   *                    Do not close the stream. You don't own it.
   */
  void load(@NotNull final InputStream inputStream);

  /**
   * Called when the configuration should save ALL the settings
   *
   * @param outputStream Write in this stream to save the content.
   *                     Do not close the stream. You don't own it.
   *
   * @throws IOException Throw if something went wrong during the
   *                     save process
   */
  void save(@NotNull final OutputStream outputStream) throws IOException;

  /**
   * Get a setting by name. Everything
   * is stored as a String so this method
   * is more for internal usage
   * than public usage.
   *
   * It could be removed
   * in a new revision
   * of the api.
   *
   * @param name The name of the setting.
   *             Example:
   *             <code>
   *              .get("User.FavouriteLanguage");
   *             </code>
   * @param <T> The type. Example string.
   * @return The parameter
   */
  @NotNull
  <T> Optional<T> get(@NotNull final String name);

  /**
   * @param name The setting name
   * @return The setting as integer (if it's an integer)
   */
  @NotNull
  OptionalInt getAsInt(@NotNull final String name);

  /**
   * @param name The setting name
   * @return The setting as double (if it's a double)
   */
  @NotNull
  OptionalDouble getAsDouble(@NotNull final String name);

  /**
   * @param name The setting name
   * @return The setting as long (if it's a long)
   */
  @NotNull
  OptionalLong getAsLong(@NotNull final String name);

  /**
   * @param name The setting name
   * @return The setting as string (if it's a string)
   */
  @NotNull
  Optional<String> getAsString(@NotNull final String name);

  /**
   * @param name The setting name
   * @return The setting as boolean (if it's a boolean)
   */
  @NotNull
  Optional<Boolean> getAsBoolean(@NotNull final String name);

  /**
   * Set the value
   *
   * @param name The name of the setting
   * @param value The value to save
   * @param <T> The type of the value
   */
  <T> void set(@NotNull final String name, @NotNull final T value);
}
