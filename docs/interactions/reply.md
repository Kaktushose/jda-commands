# Reply Building
!!! note
    All event types share the same Reply API. JDA-Commands will always acknowledge the interaction event for you.

## Text Messages
The simplest way of sending a reply is using the [`reply(String)`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/events/ReplyableEvent.html#reply(java.lang.String))
method. This will send a non-ephemeral text message. If the event has already been replied to, this method will edit the 
original message instead of sending a new one by default.

The `reply()` method also has some useful overloads, you can find a full list [here](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/reply/Reply.html#method-detail).

## Reply Configuration
You can change this default behaviour by calling [`with()`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/events/ReplyableEvent.html#with())
before sending the reply. This will return a [`ConfigurableReply`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/reply/ConfigurableReply.html)
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
The [`ConfigurableReply`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/reply/ConfigurableReply.html)
object is also used to attach components. You reference components by the name of the method they are defined with, just
like we did before with [modals](./modals.md#replying-with-modals).

!!! example
    ```java
    @SlashCommand("greet")
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
    event.with.components("firstButton").components("secondButton").reply();
    ```

If you want to add multiple components to the same action row, just pass the method names to the same `components()` call.

!!! example
    ```java
    event.with.components("firstButton", "secondButton").reply();
    ```

!!! note 
    One action row supports up to 5 buttons but only 1 select menu.

### Enabling & Disabling
By default, all components are enabled. If you want to attach a disabled component, you need to wrap it by calling
[`Component.disabled(methodName)`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/reply/Component.html#disabled(java.lang.String)).


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
[`keepComponents(false)`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/reply/ConfigurableReply.html#keepComponents(boolean)):
!!! example
    ```java
    event.with().keepComponents(false).reply("Message edit!");
    ```

Alternatively you can call [`removeComponents()`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/events/ReplyableEvent.html#removeComponents())
which will remove all components attached to a message.

---
!!! example "Cookie Clicker Example"
    === "Code"
        ```java
        @Interaction
        public class CookieClicker {
        
            private int counter;
            
            @SlashCommand(value = "cookie clicker", desc = "Play cookie clicker")
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
You can attach components that were defined in a different class by using the [`Component`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/reply/Component.html#enabled(java.lang.Class,java.lang.String))
class again.

### Lifetime

## Embeds
!!! failure
    The Embed API is currently refactored. This wiki will cover Embeds as soon as the refactoring is done.

## ReplyConfig
