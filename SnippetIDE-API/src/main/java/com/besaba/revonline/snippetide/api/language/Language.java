package com.besaba.revonline.snippetide.api.language;

import com.besaba.revonline.snippetide.api.events.Event;
import com.besaba.revonline.snippetide.api.events.EventKind;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Define a language.
 *
 * The IDE supports the compiling of a snippet and the executing of it.
 */
public interface Language {
  /**
   * @return The name of the language. Used by the IDE in the GUI
   *         when the user wants to create a snippet or in other similar
   *         places.
   *         You cannot return null,
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
   *           return Collections.singletonList("java");
   *         </code>
   *
   *         You cannot return null from this method.
   */
  @NotNull
  String[] getExtensions();

  /**
   * The entire IDE uses events to communicate
   * between his parts. Every action of the user
   * is derived using events.
   *
   * You should use this method to return a set
   * which contains all the event-classes
   * that you want to receive.
   *
   * Remember to include in the Set only
   * the classes which are inside the
   * events pacakge!
   *
   * @return The set with the events the language should listen
   */
  @NotNull
  Set<EventKind> listenTo();

  /**
   * Called when the EventManager receives receives an event
   * and this language is registered to receive it.
   *
   * Here you should do:
   *
   *  <ol>
   *    <li>Check what is the type of the event (by using the getType method)</li>
   *    <li>
   *      Check if it's for you. You will receive the event even if the user
   *      is not writing code with this language.
   *    </li>
   *    <li>
   *      If it's for you, continue by casting the event parameter
   *      to the correct type for the event.
   *
   *      For example, if it's a COMPILE_START event you should cast
   *      event to CompileStartEvent. By casting it you will get
   *      specific information about the event. In the
   *      COMPILE_START event you could have the location of the
   *      source file etc.
   *
   *      Otherwise, just stop here and return false.
   *    </li>
   *    <li>Now you have the information about the action you need to do, just do the job.</li>
   *
   *  </ol>
   *
   * @param event The event sended
   * @return If you have handled correctly the event return true
   */
  boolean receiveEvent(final Event event);
}
