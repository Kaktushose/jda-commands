# Reply Building
!!! note
    All event types share the same Reply API. JDA-Commands will only acknowledge the interaction event just before sending the reply. If you need more time, e.g. for doing a database query, you can always manually acknowledge events by calling [`event#deferReply()`][[ReplyableEvent#deferReply()]].

## Text Messages
The simplest way of sending a reply is using the <ReplyableEvent#reply(java.lang.String, Entry...)>
method. This will send a non-ephemeral text message. If the event has already been replied to, this method will edit the 
original message instead of sending a new one by default.

The `reply()` method also has some useful overloads, you can find a full list [here](https://kaktushose.github.io/jda-commands/javadocs/JDAC_JAVADOC_VERSION/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/reply/Reply.html#method-detail).

## Embeds
JDA-Commands provides a rich Embed API to make working with Embeds easier. See the [Embed Section](../../message/embeds.md) of this wiki for
setup instructions. Once you have your <EmbedDataSource>
configured you can start using Embeds in your replies. 

Use the <ConfigurableReply> object, which is accessed by calling 
<ReplyableEvent#with()>, 
to attach Embeds to your reply. The easiest way of replying with an Embed is to simply pass the Embeds name:
!!! example
    ```java
    event.with().embeds("welcome").reply();
    ```
You can also pass multiple names or call the `#embed(...)` method multiple times:
!!! example
    ```java
    event.with().embeds("welcome", "goodbye").reply();
    
    event.with().embeds("welcome").embeds("goodbye").reply();
    ```
For modifying you can either use the <Embed> object or use a callback:
!!! example
    === "Embed Object"
        ```java
        Embed embed = event.embed("welcome");
        embed.title("New title");
        event.with().embeds(embed).reply();
        ```
    === "Callback"
        ```java
        event.with().embeds("welcome", embed -> embed.title("New title")).reply();
        ```

Placeholders can be passed not only to the <Embed>
object, but also directly to the `#embed(...)` method:
!!! example
    ```java
    event.with().embeds("welcome", Entry.entry("user", "Kaktushose")).reply();
    ```

## Components
You can also reply with components. Here is a list of useful resources for working with components:

- Defining [Buttons](../components/buttons.md) and [Select Menus](../components/menus.md)
- Replying with the [Legacy Component System](./legacy.md) (ActionRows)
- [Components V2 Guide](./components.md)
- [Component Utils](./utils.md)

## Localization and Emojis
To avoid hardcoded values, all string properties of a component/the content of a message can be replaced by a localization key as supported by the
current used [Localization System](../../message/localization.md).

Furthermore, it's possible to directly use placeholders.
For more information on how to use placeholders please visit [this page](../../message/placeholder.md).

Also take a look at the general [message resolution documentation](../../message/overview.md).

!!! example
```java
event.with().ephemeral(true).reply("my.localizatoin.key");
```

JDA-Commands has built in support for Unicode and application emoji aliases. If you want to use them, just take a look [here](../../message/emojis.md).

## Reply Configuration
You can change the default reply behavior by calling <ReplyableEvent#with()>
before sending the reply. This will return a <ConfigurableReply>
object to you, you can use to modify settings:

!!! example "Ephemeral Reply"
    ```java
    event.with().ephemeral(true).reply("Hello World!");
    ```

When calling <ComponentEvent#with()> you get a <EditableConfigurableReply> which allows for even more settings:

- [`editReply(boolean)`][[EditableConfigurableReply#editReply(boolean)]]: This will send a new message instead of editing the original one.
- [`keepComponents(boolean)`](./legacy.md#keeping-components): This will keep the original components
- [`keepSelections(boolean)`](./legacy.md#keeping-selections): This will keep the user selections of the original components

### <io.github.kaktushose.jdac.annotations.interactions.ReplyConfig> Annotation
The <io.github.kaktushose.jdac.annotations.interactions.ReplyConfig>
annotation provides a way to modify the default behavior of your replies. You can either annotate single methods or
entire interaction controllers. For a list of all possible configurations see the <ReplyConfig.Builder>.

!!! example "ReplyConfig Annotation"
    ```java
    @Interaction
    @ReplyConfig(ephemeral = true)
    public class InteractionController {

        @Command("example")
        @ReplyConfig(editReply = false)
        public void onCommand(CommandEvent) {...}
        
    }
    ```

### Global Reply Config
Alternatively, you can set a [global reply config][[JDACBuilder#globalReplyConfig(InteractionDefinition.ReplyConfig)]]
at the builder:

!!! example "Global ReplyConfig"
    ```java
    JDACommands.builder(jda, Main.class)
        .globalReplyConfig(ReplyConfig.of(config -> config.ephemeral(false)))
        .start();
    ```

JDA-Commands will apply clashing ReplyConfigs in the following hierarchy:

1. `with()#...` calls
2. `ReplyConfig` method annotation
3. `ReplyConfig` class annotation
4. global `ReplyConfig`
