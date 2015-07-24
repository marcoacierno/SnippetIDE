package com.besaba.revonline.snippetide.shareservices;

import com.besaba.revonline.snippetide.api.events.share.ShareRequestEvent;
import com.besaba.revonline.snippetide.api.shareservices.ShareService;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.UrlEscapers;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PastebinShareService implements ShareService {
  @NotNull
  @Override
  public String getServiceName() {
    return "Pastebin";
  }

  @NotNull
  @Override
  public Image getImage() {
    return new Image(PastebinShareService.class.getResourceAsStream("pastebin.png"));
  }

  @Override
  public void share(@NotNull final ShareRequestEvent event) throws IOException {
    if (event.getTarget() != this) {
      return;
    }

    final HttpURLConnection httpURLConnection
        = ((HttpURLConnection) new URL("http://pastebin.com/api/api_post.php").openConnection());
    httpURLConnection.setRequestMethod("POST");
    httpURLConnection.connect();

    final String data = generateData(event.getFileName(), event.getCode());

    try(final BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(httpURLConnection.getOutputStream(), StandardCharsets.UTF_8))
    ) {
      writer.append(data);
    } finally {
      httpURLConnection.disconnect();
    }
  }

  private static String generateData(final String fileName, final String code) {
    return ImmutableMap.<String, String>builder()
        .put("api_option", "paste")
        .put("api_paste_private", String.valueOf(1))
        .put("api_paste_name", UrlEscapers.urlPathSegmentEscaper().escape(fileName))
        .put("api_paste_expire_date", "N")
        .put("api_paste_code", UrlEscapers.urlPathSegmentEscaper().escape(code))
        .put("api_dev_key", "") // how to pass dev key?
        .build()
        .entrySet()
        .stream()
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .reduce("", (acc, nxt) -> {
          return acc + "&" + nxt;
        });
  }
}
