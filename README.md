# SnippetIDE


Just an application to compile and run snippets. Uses Java 8. Screenshot beta layout, [here](http://i.imgur.com/pka5IYI.png)

It's still alpha and not complete.

- **SnippetIDE-IDE**: is the source code of the IDE itself. 
- **SnippetIDE-API**: are the IDE api. Used to create plugins for the IDE.
- **JavaLang**: is the plugin which adds the ability to compile and run the Java snippets

The IDE loads the plugins during the boot phase, from the IDEDir/plugins directory.
