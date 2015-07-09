package com.besaba.revonline.snippetide.api.plugins;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A version of the application or a plugin.
 *
 * It supports format: [major].[minor].[revision]+[metadata]
 *
 * major, minor e revision should be integers meanwhile metadata
 * can be only letters and numbers.
 *
 * You cannot use negative numbers for major, minor and revision
 */
public final class Version implements Comparable<Version> {
  private final int major;
  private final int minor;
  private final int revision;
  @NotNull
  private final String metadata;
  @NotNull
  private String stringVersion;

  private final static Pattern pattern
      = Pattern.compile("(?<major>[0-9]+)\\.(?<minor>[0-9]+)(\\.(?<revision>[0-9]+))?(\\+(?<metadata>[0-9A-Za-z-]*))?");

  private Version(final int major,
                  final int minor,
                  final int revision,
                  @NotNull final String metadata,
                  @NotNull final String stringVersion) {
    this.major = major;
    this.minor = minor;
    this.revision = revision;
    this.metadata = metadata;
    this.stringVersion = stringVersion;
  }

  public static Version parse(final String parse) {
    final Matcher matcher = pattern.matcher(parse);

    if (!matcher.matches()) {
      throw new IllegalArgumentException(
          "Version " + parse + " is not compatible with the format [major].[minor].[revision]?+[metadata]?"
      );
    }

    final int major = Integer.parseInt(matcher.group("major"));

    if (major < 0) {
      throw new IllegalArgumentException("Major cannot be negative");
    }

    final int minor = Integer.parseInt(matcher.group("minor"));

    if (minor < 0) {
      throw new IllegalArgumentException("Minor cannot be negative");
    }

    final String tempRevision = matcher.group("revision");
    int revision = 0;

    if (tempRevision != null) {
      revision = Integer.parseInt(tempRevision);

      if (revision < 0) {
        throw new IllegalArgumentException("Revision cannot be negative");
      }
    } else {
      revision = -1;
    }

    final String metadata = matcher.group("metadata") == null ? "" : matcher.group("metadata");

    return new Version(major, minor, revision, metadata, parse);
  }

  public int getMajor() {
    return major;
  }

  public int getMinor() {
    return minor;
  }

  public int getRevision() {
    return revision;
  }

  @NotNull
  public String getMetadata() {
    return metadata;
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof Version)) {
      return false;
    }

    final Version other = ((Version) obj);
    return
            other.major == major &&
            other.minor == minor &&
            other.revision == revision &&
            other.metadata.equals(metadata);
  }

  @Override
  public int hashCode() {
    int result = major;
    result = 31 * result + minor;
    result = 31 * result + revision;
    result = 31 * result + metadata.hashCode();
    return result;
  }

  @Override
  public int compareTo(@NotNull final Version other) {
    if (major > other.major) {
      return 1;
    } else if (major < other.major) {
      return -1;
    }

    if (minor > other.minor) {
      return 1;
    } else if (minor < other.minor) {
      return -1;
    }

    if (revision > other.revision) {
      return 1;
    } else if (revision < other.revision) {
      return -1;
    }

    // should we implement metadata? it's used inside equals/hashCode but not here
    return 0;
  }

  @Override
  public String toString() {
    return stringVersion;
  }
}
