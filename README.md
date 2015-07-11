# SnippetIDE

Just an application to compile and run snippets. Uses Java 8.

- SnippetIDE-IDE is the source code of the IDE itself. 
- SnippetIDE-API are the IDE api. It's to create plugins
- JavaLang is the plugin which implements the ability to compile and run the Java lang

The IDE loads the plugins during the boot phase, from the IDEDir/plugins directory.