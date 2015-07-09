package com.besaba.revonline.snippetide.api.plugins;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class PluginTest {
  @Test
  public void testSupportsVersionWithAVersionOfTheIdeNotSupported() throws Exception {
    final Plugin plugin = new Plugin("Fake", "Fake", Version.parse("3.5"), Version.parse("0.5"), new String[0], Collections.emptyList());
    final Version ideVersion = Version.parse("0.1");

    // IDE version 0.1 cannot run a plugin which requires at least 0.5
    assertFalse(plugin.supports(ideVersion));
  }
}