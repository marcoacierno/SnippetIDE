package com.besaba.revonline.snippetide.api.plugins;

import org.junit.Test;

import static org.junit.Assert.*;

public class VersionTest {

  @Test
  public void testParseVersionWithMajorAndMinorOnly() throws Exception {
    final String versionString = "1.5";
    final Version version = Version.parse(versionString);

    assertEquals(1, version.getMajor());
    assertEquals(5, version.getMinor());
  }

  @Test
  public void testParseVersionWithMajorMinorAndRevision() throws Exception {
    final String versionString = "2.6.1";
    final Version version = Version.parse(versionString);

    assertEquals(2, version.getMajor());
    assertEquals(6, version.getMinor());
    assertEquals(1, version.getRevision());
  }

  @Test
  public void testParseVersionWithMajorMinorRevisionAndMetadata() throws Exception {
    final String versionString = "0.1.1+alpha";
    final Version version = Version.parse(versionString);

    assertEquals(0, version.getMajor());
    assertEquals(1, version.getMinor());
    assertEquals(1, version.getRevision());
    assertEquals("alpha", version.getMetadata());
  }

  @Test
  public void testParseVersionWithMajorMinorAndMetadata() throws Exception {
    final String versionString = "3.0+beta";
    final Version version = Version.parse(versionString);

    assertEquals(3, version.getMajor());
    assertEquals(0, version.getMinor());
    assertEquals("beta", version.getMetadata());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidVersionOnlyWithMajor() throws Exception {
    final String versionString = "1";
    final Version version = Version.parse(versionString);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseWithInvalidVersion() throws Exception {
    final String str = ".5";
    final Version version = Version.parse(str);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseWithADotAndNothingAfterMinor() throws Exception {
    final String str ="6.1.";
    final Version version = Version.parse(str);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseWithADotAfterMinorAndMetadata() throws Exception {
    final String str = "1.7.+metadata";
    final Version version = Version.parse(str);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidVersionWithMajorAndMetadata() throws Exception {
    final String versionString = "1+eap32";
    final Version version = Version.parse(versionString);
  }

  @Test
  public void testEqualsWithTwoDifferentVersions() throws Exception {
    final Version vFiveTwoThree = Version.parse("5.2.3");
    final Version vOneOneThree = Version.parse("1.1.3");

    assertNotEquals(vFiveTwoThree, vOneOneThree);
  }

  @Test
  public void testCompareToWithTwoDifferentVersions() throws Exception {
    final Version vFiveZeroOne = Version.parse("5.0.1");
    final Version vThreeTwoTwo = Version.parse("3.2.2");

    assertEquals(1, vFiveZeroOne.compareTo(vThreeTwoTwo));
    assertEquals(-1, vThreeTwoTwo.compareTo(vFiveZeroOne));
  }

  @Test
  public void testCompareToWithTheSameVersion() throws Exception {
    final Version vTwoTwoTwo = Version.parse("2.2.2");
    final Version vTwoTwoTwo2 = Version.parse("2.2.2");

    assertEquals(0, vTwoTwoTwo.compareTo(vTwoTwoTwo2));
    assertEquals(0, vTwoTwoTwo2.compareTo(vTwoTwoTwo));
  }

  @Test
  public void testCompareToWithSameVersionButOneWithoutMetadataOtherWith() throws Exception {
    final Version vThreeZeroOneWithMetadata = Version.parse("3.0.1+rc1");
    final Version vThreeZeroOneWithoutMetadata = Version.parse("3.0.1");

    assertEquals(0, vThreeZeroOneWithMetadata.compareTo(vThreeZeroOneWithoutMetadata));
    assertEquals(0, vThreeZeroOneWithoutMetadata.compareTo(vThreeZeroOneWithMetadata));
  }

  @Test
  public void testToStringVersionWithMetadata() throws Exception {
    final Version version = Version.parse("6.1+beta1");
    assertEquals("6.1+beta1", version.toString());
  }

  @Test
  public void testToStringWithVersionComplete() throws Exception {
    final Version version = Version.parse("7.2.1+alpha1");
    assertEquals("7.2.1+alpha1", version.toString());
  }
}