# Embed API
## EmbedDataSource
In JDA, you define Embeds by using the <EmbedBuilder>
which eventually gets built into a <MessageEmbed>.
`MessageEmbeds` can be serialized into (and deserialized from) `JSON`. 

!!! tip inline end
    For details on the `JSON` format of Embeds, see the [Discord Documentation](https://discord.com/developers/docs/resources/message#embed-object)
    or use one the various [Online Embed Builders](https://glitchii.github.io/embedbuilder/).

JDA-Commands takes advantage of this and provides the <EmbedDataSource>
interface to load Embeds from a `JSON` object. It supports the following sources by default:

- a raw `JSON` String
- a `*.json` file either located externally or inside the `resources` folder
- an <InputStream>
- JDAs <DataObject>

You can also provide your own sources by implementing the <EmbedDataSource>
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

When configuring JDA-Commands, you can then use the <EmbedConfig>
to add one or multiple [EmbedDataSources][[EmbedDataSource]]
to later load Embeds from at various places.
!!! example
    ```java
    JDACommands.builder(jda, Main.class)
        .embeds(config -> config.sources(EmbedDataSource.file(Path.of("embeds.json")))
        .start();
    ```

!!! tip
    [EmbedDataSources][[EmbedDataSource]] can also be loaded from [extensions](../misc/extension/overview.md) by providing
    values for [Property#EMBED_SOURCES]

## Loading Embeds
You can either use the <JDACommands>
or the <ReplyableEvent>
class to load embeds into an object.  

Embeds don't get loaded directly into a JDA <MessageEmbed>
or <EmbedBuilder> object. Instead, JDA-Commands provides
a subclass of <EmbedBuilder>, simply called <Embed>. 
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
    <JDACommands#findEmbed(String)> method, which
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

Use <Embed#locale(Locale)>
to set the locale the Embed should be localized with. When replying to interactions, this is done automatically by using
<GenericInteractionCreateEvent#getUserLocale()>
to retrieve the user locale. The Embed will be localized once <Embed#build()>
is called. 

See the [Localization Section](localization.md) of this wiki for details.

## Placeholders and emojis (message resolution)
You can add placeholders to your Embeds just like in many other places in this framework. JDA-Commands will resolve the
content of embeds according to the documentation [here](overview.md).

!!! example
    ```json title="embeds.json"
    {
        "welcome": {
            "title": "Hello", 
            "description": "Greetings { $user }" 
        }
    }
    ```
You can then use the <Embed#placeholders(Entry...)>
method to insert the placeholder values.
!!! example
    ```java
    Embed embed = ...;
    embed.placeholders(Entry.entry("user", "Kaktushose"));
    ```

You can of course also use your placeholders regularly inside the localization files. 

### Global Placeholders
A special feature of the Embed API is that you can define global placeholders for your Embeds using the <EmbedConfig>.
!!! example
    ```java
    JDACommands.builder(jda, Main.class)
        .embeds(config -> config.placeholders(Entry.entry("bot-name", "JDA-Commands Bot")))
        .start();
    ``` 
Global placeholders can be used anywhere inside your Embeds.
