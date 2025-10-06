# Reply Building
!!! note
    All event types share the same Reply API. JDA-Commands will always acknowledge the interaction event for you.

## Localization and Placeholders
To avoid hardcoded values, all string properties of a component/the content of a message can be replaced by a localization key as supported by the
current used [Localization System](../message/localization.md).

Furthermore, it's possible to directly use placeholders.
For more information on how to use placeholders please visit [this page](../message/placeholder.md).

Also take a look at the general [message resolution documentation](../message/overview.md).

!!! example
    ```java
    event.with().ephemeral(true).reply("my.localizatoin.key");
    ```

## Unicode and application emojis
JDA-Commands has built in support for Unicode and application emoji aliases.
If you want to use them, just take a look [here](../message/emojis.md).

## Text Messages
The simplest way of sending a reply is using the <com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent#reply(java.lang.String, Entry...)>
method. This will send a non-ephemeral text message. If the event has already been replied to, this method will edit the 
original message instead of sending a new one by default.

The `reply()` method also has some useful overloads, you can find a full list [here](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/reply/Reply.html#method-detail).

## Reply Configuration
You can change this default behaviour by calling <com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent#with()>
before sending the reply. This will return a <com.github.kaktushose.jda.commands.dispatching.reply.ConfigurableReply>
object to you, you can use to modify settings:

!!! example "Ephemeral Reply"
    ```java
    event.with().ephemeral(true).reply("Hello World!");
    ```

!!! example "Edit Reply"
    ```java
    event.with().editReply(false).reply("Hello World!");
    ```

## Components
### Replying with Components
The <com.github.kaktushose.jda.commands.dispatching.reply.ConfigurableReply>
object is also used to attach components. You reference components by the name of the method they are defined with, just
like we did before with [modals](./modals.md#replying-with-modals).

!!! example
    ```java
    @Command("greet")
    public void onCommand(CommandEvent event) {
        event.with.components("onButton").reply("Hello World!"); //(1)!
    }

    @Button("Greet me!")
    public void onButton(ButtonEvent event) { 
        event.reply("Hello %s".formatted(event.getUser().getAsMention()));
    }
    ```

    1. We reference the Button we want to send via the method name.

You can also omit the text message and only send the component by calling `reply()` with no arguments.

### Action Rows
Every call to `components()` will create a new action row. If you want more than one action row you need to call
`components()` multiple times.

!!! example
    ```java
    event.with().components("firstButton").components("secondButton").reply();
    ```

If you want to add multiple components to the same action row, just pass the method names to the same `components()` call.

!!! example
    ```java
    event.with().components("firstButton", "secondButton").reply();
    ```

!!! note 
    One action row supports up to 5 buttons but only 1 select menu.

### Enabling & Disabling
By default, all components are enabled. If you want to attach a disabled component, you need to wrap it by calling
<com.github.kaktushose.jda.commands.dispatching.reply.Component#disabled(java.lang.String, Entry...)>


If you want to add multiple components to the same action row, with some of them enabled and some disabled, you need to
wrap all of them.

!!! example
    ```java
    event.with.components(Component.disabled("firstButton"), Component.enabled("secondButton")).reply();
    ```

### Keeping Components
When working with components and especially when building menus, e.g. a pagination with buttons, it is often needed to
keep the components attached, even when editing the original message multiple times. 

Normally, Discord would remove any 
components when sending a message edit, unless they are explicitly reattached.

JDA-Commands flips this behaviour and will keep your components attached by default. 

You can disable this by calling
[`keepComponents(false)`][[com.github.kaktushose.jda.commands.dispatching.reply.ConfigurableReply#keepComponents(boolean)]]:
!!! example
    ```java
    event.with().keepComponents(false).reply("Message edit!");
    ```

Alternatively you can call <com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent#removeComponents()>
which will remove all components attached to a message.

---
!!! example "Cookie Clicker Example"
    === "Code"
        ```java
        @Interaction
        public class CookieClicker {
        
            private int counter;
            
            @Command(value = "cookie clicker", desc = "Play cookie clicker")
            public void onClicker(CommandEvent event) {
                event.with().components("onCookie").reply("You've got %s cookie(s)!", counter);
            }
            
            @Button(value = "Collect", emoji = "🍪", style = ButtonStyle.SUCCESS)
            public void onCookie(ComponentEvent event) {
                event.reply("You've got %s cookie(s)!", ++counter);
            }
        }
        ```
          
    === "Execution"
        ![Cookie Clicker](../assets/cookie-clicker.gif)

### Foreign Components
You can attach components that were defined in a different class by using the <com.github.kaktushose.jda.commands.dispatching.reply.Component#enabled(java.lang.Class,java.lang.String, Entry...)>
class again. In addition to the method name, you must also pass the class reference in that case.

!!! example
    ```java
    event.with()
        .components(Component.enabled(ButtonHelpers.class, "onConfirm"), Component.enabled(ButtonHelpers.class, "onDeny"))
        .reply("Are you sure?");
    ```

The foreign component will use the original [Runtime](../start/runtime.md) just like any other component would. If no 
instance of the class the component is defined in (_`ButtonHelpers` in the example above_) exists yet, 
the [Runtime](../start/runtime.md) will create one instance (and store it for potential future method calls). 

### Lifetime
As discussed [earlier](../start/runtime.md#lifetime), Runtimes have a limited lifetime. By default, JDA-Commands will close
a Runtime after 15 minutes have passed. 

!!! danger "Component Lifetime"
    This means all components belonging to that Runtime will stop working after 15 minutes!

JDA-Commands will handle this case for you. This error message can be [customized](../misc/error-handling.md#error-messages).

![Expiration Message](../assets/expiration.png)

If you want to avoid this behaviour, you have to reply with components that are `runtime-independent`. They will create a
new `Runtime` everytime they are executed. These components will even work after a full bot restart! If you want them to not be usable anymore you need to remove
them on your own.

!!! info inline end
    Modals cannot be independent because they always need a parent interaction that triggers them!

!!! example
    ```java
    event.with().components(Component.independent("onButton")).reply("Hello World!");
    ```

### Dynamic Components
Just like with Modals, you can dynamically modify components too. Use the <com.github.kaktushose.jda.commands.dispatching.reply.Component#enabled(java.lang.Class,java.lang.String,Entry...)>
class to access a builder object, which wraps the JDA builder. Alternatively, you can access the native JDA builder by
calling `#modify`.

!!! example
    ```java
    event.with().components(Component.button("onButton").label("New Label")).reply("Hello World!");

    event.with().components(Component.stringMenu("onMenu").modify(jdaBuilder -> ...).reply("Hello World!");
    ```

## Embeds
JDA-Commands provides a rich Embed API to make working with Embeds easier. See the [Embed Section](../message/embeds.md) of this wiki for
setup instructions. Once you have your <com.github.kaktushose.jda.commands.embeds.EmbedDataSource>
configured you can start using Embeds in your replies. 

Use the <com.github.kaktushose.jda.commands.dispatching.reply.ConfigurableReply> object, which is accessed by calling 
<com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent#with()>, 
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
For modifying you can either use the <com.github.kaktushose.jda.commands.embeds.Embed> object or use a callback:
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

Placeholders can be passed not only to the <com.github.kaktushose.jda.commands.embeds.Embed>
object, but also directly to the `#embed(...)` method:
!!! example
    ```java
    event.with().embeds("welcome", Entry.entry("user", "Kaktushose")).reply();
    ```

## ReplyConfig
The [`@ReplyConfig`][[com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig]]
annotation provides a way to modify the default behaviour for the `editReply`, `ephemeral` and `keepComponents` settings. 
You can either annotate single methods or entire interaction controllers. 

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

Alternatively, you can set a [global reply config][[com.github.kaktushose.jda.commands.JDACBuilder#globalReplyConfig(com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition.ReplyConfig)]]
at the builder:

!!! example "Global ReplyConfig"
    ```java
    JDACommands.builder(jda, Main.class)
        .globalReplyConfig(new ReplyConfig(true, false, false))
        .start();
    ```

JDA-Commands will apply clashing ReplyConfigs in the following hierarchy:

1. `with()#...` calls 
2. `ReplyConfig` method annotation
3. `ReplyConfig` class annotation
4. global `ReplyConfig`
