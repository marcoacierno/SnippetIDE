package com.besaba.revonline.snippetide.api.language;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Define a language.
 *
 * The IDE is event-based for every action, for example when
 * the user wants to compile a snippet the application will
 * send an event that you should receive, execute the action
 * and send back the result using an event.
 *
 * It uses Guava's EventBus (wrapped inside an EventManager)
 * inside your class you should create a method with the
 * \@Subscribe annotation and with only one parameter
 * which should be the event you want to receive.
 *
 * For example, if you want to know if a user wants
 * to compile the snippet you can use this code
 *
 * <code>
 *   @-Subscribe
 *   public void compileSnippetEvent(final CompileStartEvent event) {
 *     // You will receive every event, but you should check if it's
 *     // for you.
 *
 *     if (event.getTarget() != this) {
 *       return;
 *     }
 *
 *     // ..
 *     // compile the source (CompileStartEvent has everything you need)
 *     // ..
 *
 *     // send CompileFinished event to inform the IDE
 *     eventManager.post(new CompileFinishedEvent(this));
 *   }
 * </code>
 *
 * The method compileSnippetEvent will be invoked when the
 * CompileStartEvent is propagated.
 *
 * @see com.google.common.eventbus.Subscribe
 * @see com.besaba.revonline.snippetide.api.events.manager.EventManager
 */
public interface Language {
  /**
   * @return The name of the language. Used by the IDE in the GUI
   *         when the user wants to create a snippet or in other similar
   *         places.
   *         You cannot return null.
   */
  @NotNull
  String getName();

  /**
   * @return The extensions supported by the language.
   *         Remember to pass just the name of the extension
   *         without the dot (".").
   *
   *         For example in Java you can return:
   *
   *         <code>
   *           return new String[] {"java"};
   *         </code>
   *
   *         You cannot return null from this method.
   */
  @NotNull
  String[] getExtensions();
}
