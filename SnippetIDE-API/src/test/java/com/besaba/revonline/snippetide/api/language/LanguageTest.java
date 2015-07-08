package com.besaba.revonline.snippetide.api.language;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class LanguageTest {

  @Test
  public void testGetName() throws Exception {
    final Language language = new LanguageMock();

    // name should not be null
    assertNotNull(language.getName());
    assertEquals("Mock", language.getName());
  }

  @Test
  public void testGetExtensions() throws Exception {
    final Language language = new LanguageMock();

    assertThat(language.getExtensions().length, is(1));
  }

  @Test
  public void testRegisterToEvents() throws Exception {

  }

  @Test
  public void testReceiveEvent() throws Exception {

  }
}