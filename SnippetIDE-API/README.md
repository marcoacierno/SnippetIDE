# SnippetIDE-API

Allows you to create plugins for the IDE. A plugin is a Jar file which contains a *manifest.json* and the source code
for the Languages that the plugins implements.

## manifest.json

Used to provide information about the plugin, in json format. It should in the root of the jar file.

An example of manifest:

```
{
  "name": "My plugin name",
  "description": "My description",
  "version": "1.0",
  "minSupportedVersion": "0.1",
  "authors": [
    "Marco Acierno"
  ],
  "languages": [
    "com.besaba.revonline.snippetide.javalang"
  ]
}
```

- **name**: Is the name of the plugin. Plugin names are unique so if two plugins have different names, only one will be
loaded.
- **description**: Description of the plugin
- **version**: The version of the plugin
- **minSupportedVersion**: The minimum version of the IDE which the plugin can be used. For example, if you set
1.0 the plugin can be used in any version above or equal to 1.0 but not in 0.9 etc.
- **authors**: An array of strings which contains who created the plugin
- **languages**: The reference(s) to the classes which implements the Language interface (in other words, the languages
the IDE provides)