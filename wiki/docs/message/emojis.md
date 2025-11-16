# Emojis
JDA-Commands supports using emoji aliases for Unicode and application emojis in all places where [localization](localization.md#implicit-localization) is done implicitly.

This is done after [placeholder resolution](placeholder.md) and [localization](localization.md) took place.

JDA-Commands emoji aliases are very similar to the ones used by the discord client. 
They follow the same format `:emoji_name:` for both Unicode and application emojis. 
Setting the skintone like `:emoji_name::skin-tone-5:` is supported too.

Since emoji aliases are resolved after localization takes place, you can also use them in your
localization messages regardless of the used <Localizer>.

??? tip "Escaping the colon"
    Normally it shouldn't be necessary to escape the colons in messages (that shouldn't be an emoji alias), but in case any troubles occur you can
    just prefix it with `\` (in Java `\\`) to escape it.

## Unicode Emojis
If you want to avoid annoying encoding issues with hardcoded Unicode emojis, you can just use the known Discord
client aliases in JDA-Commands too.

!!! example "Unicode Emoji"
    ```java
    @Command("example")
    public void onCommand(CommandEvent event) {
        event.reply("Hi :smiley:");
    }
    ```
    
    This will reply with `Hi 😀` to the user.

!!! note
    We're using the amazing [JEmoji](https://github.com/felldo/jemoji) library to resolve Unicode Discord aliases.
    If something doesn't feel right with the used aliases/emojis please reach out to them [here](https://github.com/felldo/JEmoji/issues).

## Application Emojis
Application emojis can be used exactly the same as Unicode emojis in messages with the custom emoji name as the alias.
If an application emoji is called the same as a Unicode emoji alias, the application emoji takes precedence.

!!! warning
    JDA-Commands fetches and caches all application emojis upon startup. Therefore, JDA-Commands has to be restarted
    if changes are made to them.

!!! example "Application Emoji"
    ```java
    @Command("example")
    public void onCommand(CommandEvent event) {
        event.reply("Hi :my_custom_emoji:");
    }
    ```

    This will reply `Hi <you custom emoji here>` to the user.

### Automatic Application Emoji Registration
Adding application emojis manually in the webinterface is an annoying task, but for your luck JDA-Commands comes with tools
to register your emojis automatically upon startup. Please note that we cannot unregister application emojis due to api limits. 

Similar to how [ClassFinders](../misc/reflection.md#classfinder) work, JDA-Commands uses [EmojiSources](../misc/reflection.md#emojisource)
to load your application emojis from different places. Per default there are 3 types of `EmojiSources`:

- <EmojiSource#reflective(String...)>
  that searches the classpath (resources) for files in the stated directories/packages.
- <EmojiSource#fromUrl(String, URL)>
  that loads an emoji with the given name from this URL
- <EmojiSource#fromIcon(String, Icon)>
  that just works as an interop to JDAs own emoji api

---
If no [EmojiSources](../misc/reflection.md#emojisource) are set by the user in the JDA-Commands Builder, the resource directory `emojis` will be searched for application emojis per default.

!!! example
    If your resource directory looks like:
    ```
    src/
    ├─ main/
    │  ├─ resources/
    │  │  ├─ emojis/
    │  │  │  ├─ laughing.gif
    │  │  │  ├─ hola.png
    ```
    then JDA-Commands will register the application emojis "laughing" and "hola" upon startup.

If you want to use custom EmojiSources just take a look [here](../misc/reflection.md#emojisource).
    