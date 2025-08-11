# Modals

## Overview
Modals are defined by annotating a method with [`@Modal`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/interactions/Modal.html).
The first parameter must always be a [`ModalEvent`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/events/interactions/ModalEvent.html).

```java
@Modal("My Modal")
public void onModal(ModalEvent event, @TextInput("Input") String input) { ... }
```

## Localization and placeholder
To avoid hardcoded values, all string values of an annotation can be replaced by a localization key as supported by the
current used [Localization System](../localization.md).

Furthermore, it's possible to directly insert a localization messages content. It will be treated exactly the same
as retrieved by a key. For more information on how to use the localization system please visit [this page](../localization.md).

!!! example key (with Fluava)
```java
@Modal("my.localization.key")
public void onModal(ModalEvent event, @TextInput("Input") String input) { ... }
```

!!! example content (with Fluava)
```java
@Modal("{ $modal_name }")
public void onModal(ModalEvent event, @TextInput("Input") String input) { ... }
```

## Text Inputs
You can add text inputs to a modal by adding String parameters annotated with [`@TextInput`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/interactions/TextInput.html).
The label and other metadata of the text input is passed to the annotation. 

!!! tip
    Just as for command options, the parameter name will be used for the label by default. However, this requires the 
    `-parameters` compiler flag to be [enabled](./commands.md#name-description). 

Text Inputs can be configured with the following fields:
### style
Sets the [`TextInputStyle`](https://docs.jda.wiki/net/dv8tion/jda/api/interactions/components/text/TextInputStyle.html). 
The default value is [`TextInputStyle.PARAGRAPH`](https://docs.jda.wiki/net/dv8tion/jda/api/interactions/components/text/TextInputStyle.html#PARAGRAPH).
!!! example
    ```java
    @Modal("Ban reason")
    public void onModal(ModalEvent event, @TextInput(value = "Reason", style = TextInputStyle.SHORT) String input) { ... }
    ```

### placeholder
Sets the placeholder of a text input.
!!! example
    ```java
    @Modal("Ban reason")
    public void onModal(ModalEvent event, @TextInput(value = "Reason", placeholder = "Please give a reason") String input) { ... }
    ```

### defaultValue
Sets the default value of a text input, which will pre-populate the text input field with the specified String. 
!!! example
    ```java
    @Modal("Ban reason")
    public void onModal(ModalEvent event, @TextInput(value = "Reason", defaultValue = "Rule Violation") String input) { ... }
    ```

### minValue & maxValue
Sets the minimum and maximum input length of a text input.
!!! example
    ```java
    @Modal("Ban reason")
    public void onModal(ModalEvent event, @TextInput(value = "Reason", maxValue = 1000) String input) { ... }
    ```

### required
Sets whether the text input is required. The default value is `true`.
!!! example
    ```java
    @Modal("Ban reason")
    public void onModal(ModalEvent event, @TextInput(value = "Reason", required = false) String input) { ... }
    ```

## Replying with Modals
You can reply to [`CommandEvents`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/events/interactions/CommandEvent.html)
and [`ComponentEvents`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/events/interactions/ComponentEvent.html)
with a Modal by calling [`replyModal(methodName)`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/events/ModalReplyableEvent.html#replyModal(java.lang.String))
on the event.

!!! example
    ```java 
    @Command("ban")
    public void onCommand(CommandEvent event, User target) {
        event.replyModal("onModal"); //(1)!
    }

    @Modal("Ban reason")
    public void onModal(ModalEvent event, @TextInput("Reason") String input) { ... }
    ```

    1. We reference the Modal we want to send via the method name.

### Dynamic Modals
Sometimes you want to modify a Modal dynamically at runtime. You can do so by calling
[`replyModal(methodName, function)`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/events/ModalReplyableEvent.html#replyModal(java.lang.String,java.util.function.Function)).

!!! example
    ```java
    event.replyModal("onModal", modal -> modal.title("Cool Title!"));
    ```

If you want to, you can also use the native JDA builder:
!!! example
    ```java
    event.replyModal("onModal", modal -> modal.modify(jdaBuilder -> jdaBuilder.setTitle("Boring Title!")));
    ```
