package com.besaba.revonline.snippetide.api.shareservices;

import com.besaba.revonline.snippetide.api.datashare.StructureDataContainer;
import com.besaba.revonline.snippetide.api.events.share.ShareRequestEvent;
import com.google.common.eventbus.Subscribe;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * A ShareService is a service used by the IDE
 * to allow the user to share their code online.
 *
 * <p>A plugin can implement one or more this interface
 * and provide multiple services</p>
 *
 * <p>getServiceName and getImage are used when the
 * user opens the "Share on..." menu in the IDE.
 * share() is invoked when the user wants to share
 * the code. It's execute in a new thread, so you don't
 * need to worry about HTTP requests in the main thread.</p>
 */
public interface ShareService {
  /**
   * @return The name of the service.
   *         For example "Pastebin" or "GitHub Gist"
   */
  @NotNull
  String getServiceName();

  /**
   * @return Service small icon (16x16)
   */
  @NotNull
  Image getImage();

  /**
   * Invoked when the user wants to share
   * the code with the service.
   *
   * <p>If possible, post the code as anonymous
   * we don't support login because we don't
   * have a reliable way to keep users' information safe.</p>
   *
   * <p>We force every implementation of ShareService
   * to have this method to receive the event. Remember
   * to check if the event is for you by using .getTarget() != this</p>
   *
   * <p>Remember to add @Subscribe annotation!</p>
   *
   * @param event The event with the information about the share.
   * @since 1.5
   */
  @Subscribe
  void share(@NotNull final ShareRequestEvent event) throws IOException;

  @NotNull
  StructureDataContainer[] getShareParameters();
}
