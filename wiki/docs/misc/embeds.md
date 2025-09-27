# Embed API
## EmbedDataSource
In JDA, you define Embeds by using the [`EmbedBuilder`](https://docs.jda.wiki/net/dv8tion/jda/api/EmbedBuilder.html), 
which eventually gets built into a [`MessageEmbed`](https://docs.jda.wiki/net/dv8tion/jda/api/entities/MessageEmbed.html).
`MessageEmbeds` can be serialized into (and deserialized from) `JSON`. 

!!! tip inline end
    For details on the `JSON` format of Embeds, see the [Discord Documentation](https://discord.com/developers/docs/resources/message#embed-object)
    or use one the various [Online Embed Builders](https://glitchii.github.io/embedbuilder/).

JDA-Commands takes advantage of this and provides the [`EmbedDataSource`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/embeds/EmbedDataSource.html) 
interface to load Embeds from a `JSON` object. It supports the following sources by default:

- a raw `JSON` String
- a `*.json` file either located externally or inside the `resources` folder
- an [`InputStream`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/io/InputStream.html)
- JDAs [`DataObject`](https://docs.jda.wiki/net/dv8tion/jda/api/utils/data/DataObject.html)

You can also provide your own sources by implementing the [`EmbedDataSource`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/embeds/EmbedDataSource.html)
interface. 

The `JSON` object can contain an arbitrary amount of Embeds as child objects. The name of these child objects must be unique
and will be later used to load the respective Embed.
!!! example 
    ```json title="embeds.json"
    {
        "welcome": {
            "title": "Hello World",
            "description": "This will be an example"
        },
        "goodbye": {
            "title": "Bye World",
            "description": "This was an example"
        }
    }
    ```

When configuring JDA-Commands, you can then use the [`EmbedConfig`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/embeds/EmbedConfig.html#sources(com.github.kaktushose.jda.commands.embeds.EmbedDataSource...))
to add one or multiple [`EmbedDataSources`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/embeds/EmbedDataSource.html)
to later load Embeds from at various places.
!!! example
    ```java
    JDACommands.builder(jda, Main.class)
        .embeds(config -> config.sources(EmbedDataSource.file(Path.of("embeds.json")))
        .start();
    ```

## Loading Embeds
You can either use the [`JDACommands`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/JDACommands.html#embed(java.lang.String))
or the [`ReplyableEvent`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/events/ReplyableEvent.html#embed(java.lang.String))
class to load embeds into an object.  

Embeds don't get loaded directly into a JDA [`MessageEmbed`](https://docs.jda.wiki/net/dv8tion/jda/api/entities/MessageEmbed.html)
or [`EmbedBuilder`](https://docs.jda.wiki/net/dv8tion/jda/api/EmbedBuilder.html) object. Instead, JDA-Commands provides
a subclass of [`EmbedBuilder`](https://docs.jda.wiki/net/dv8tion/jda/api/EmbedBuilder.html), simply called [`Embed`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/embeds/Embed.html). 
It provides some additional utility, e.g. when dealing with fields, and is also required for the localization and 
placeholder feature. 

!!! example
    === "Event"
        ```java
        @Command("example")
        public void onCommand(CommandEvent event) {
            Embed embed = event.embed("welcome");
            embed.title("New title");
            MessageEmbed message = embed.build();
        }
        ```
    === "JDACommands"
        ```java
        Embed embed = JDACommands.embed("welcome");
        embed.title("New title");
        MessageEmbed message = embed.build();
        ```
    
!!! tip
    The `#embed(String)` method will throw an `IllegalArgumentException` if no Embed with the given name was found. Use the 
    [`#findEmbed(String)`](https://kaktushose.github.io/jda-commands/javadocs/4/search.html?q=findEmbed) method, which
    returns an Optional, if you cannot ensure that the Embed exists.

See the [Reply Section](../interactions/reply.md#embeds) of this wiki to learn more about on how to use Embeds for your
interaction replies.

## Localization
Just like any other feature of JDA-Commands, Embeds can be localized as well. For the default Localizer implementation, 
which uses Fluava, this could look like this:
!!! example
    ```json title="embeds.json"
    {
        "welcome": {
            "title": "welcome-title", // will load from the "default" bundle if no @Bundle annotation is present
            "description": "my_bundle#welcome-description" // will load from the "my_bundle" bundle
        }
    }
    ```

Use [`Embed#locale(Locale)`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/embeds/Embed.html#locale(java.util.Locale))
to set the locale the Embed should be localized with. When replying to interactions, this is done automatically by using
[`GenericInteractionCreateData#getUserLocale()`](https://docs.jda.wiki/net/dv8tion/jda/api/events/interaction/GenericInteractionCreateEvent.html#getUserLocale())
to retrieve the user locale. The Embed will be localized once [`#build()`](https://kaktushose.github.io/jda-commands/javadocs/snapshot/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/embeds/Embed.html#build())
is called. 

See the [Localization Section](../message/localization.md) of this wiki for details.

## Placeholders and emojis (message resolution)
You can add placeholders to your Embeds just like in many other places in this framework. JDA-Commands will resolve
embeds according to the documentation [here](../message/overview.md).

!!! example
    ```json title="embeds.json"
    {
        "welcome": {
            "title": "Hello", 
            "description": "Greetings { $user }" 
        }
    }
    ```
You can then use the [Embed#placeholders(...)](https://kaktushose.github.io/jda-commands/javadocs/snapshot/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/embeds/Embed.html#placeholders(com.github.kaktushose.jda.commands.i18n.I18n.Entry...))
method to insert the placeholder values.
!!! example
    ```java
    Embed embed = ...;
    embed.placeholders(Entry.entry("user", "Kaktushose"));
    ```

You can of course also use your placeholders regularly inside the localization files. 

### Global Placeholders
A special feature of the Embed API is that you can define global placeholders for your Embeds using the [`EmbedConfig`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/embeds/EmbedConfig.html#placeholders(com.github.kaktushose.jda.commands.i18n.I18n.Entry...)).
!!! example
    ```java
    JDACommands.builder(jda, Main.class)
        .embeds(config -> config.placeholders(Entry.entry("bot-name", "JDA-Commands Bot")))
        .start();
    ``` 
Global placeholders can be used anywhere inside your Embeds.
