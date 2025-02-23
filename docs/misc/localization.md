# Localization
## LocalizationFunction (JDA)
JDA uses the [`LocalizationFunction`](https://docs.jda.wiki/net/dv8tion/jda/api/interactions/commands/localization/ResourceBundleLocalizationFunction.html) for localizing slash commands. 
You can pass it to the JDA-Commands Builder:

```java
LocalizationFunction localizationFunction = ResourceBundleLocalizationFunction...;

JDACommands.builder(jda, Main.class)
    .localizationFunction(localizationFunction);
    .start();
```

See the [JDA Docs](https://github.com/discord-jda/JDA/blob/master/src/examples/java/LocalizationExample.java) for details.

## Localization (JDA-Commands)
!!! failure
    Localization of components, modals and replies is currently worked on.  