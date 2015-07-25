package com.besaba.revonline.snippetide.shareservices;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.*;

public class GistShareServiceTest {
  private final class MyHttpUrlConnection extends HttpURLConnection {
    protected MyHttpUrlConnection() {
      super(null);
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean usingProxy() {
      return false;
    }

    @Override
    public void connect() throws IOException {

    }
  };

  @Test
  public void testShare() throws Exception {
  }

  @Test
  public void testEscapeForJson() throws Exception {
    assertEquals("\"hello\"", GistShareService.escapeForJson("hello"));
    assertEquals("\"hell\\\"ol!\"", GistShareService.escapeForJson("hell\"ol!"));
  }
}