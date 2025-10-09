# Modals

## Overview
Modals are defined by annotating a method with <com.github.kaktushose.jda.commands.annotations.interactions.Modal>.
The first parameter must always be a <ModalEvent>.

```java
@Modal("My Modal")
public void onModal(ModalEvent event, @TextInput("Input") String input) { ... }
```

## Localization and Placeholders
To avoid hardcoded values, all string values of an annotation can be replaced by a localization key as supported by the
current used [Localization System](../message/localization.md).

Furthermore, it's possible to directly use placeholders.
For more information on how to use placeholders please visit [this page](../message/placeholder.md).

Also take a look at the general [message resolution documentation](../message/overview.md).

!!! example "Key Example (with Fluava)"
    ```java
    @Modal("my.localization.key")
    public void onModal(ModalEvent event, @TextInput("Input") String input) { ... }
    ```

!!! example "Placeholder Example" 
    ```java
    @Modal("{ $modal_name }")
    public void onModal(ModalEvent event, @TextInput("Input") String input) { ... }
    ```

## Unicode and application emojis
JDA-Commands has built in support for Unicode and application emoji aliases.
If you want to use them, just take a look [here](../message/emojis.md).

## Text Inputs
You can add text inputs to a modal by adding String parameters annotated with <com.github.kaktushose.jda.commands.annotations.interactions.TextInput>.
The label and other metadata of the text input is passed to the annotation. 

!!! tip
    Just as for command options, the parameter name will be used for the label by default. However, this requires the 
    `-parameters` compiler flag to be [enabled](./commands.md#name-description). 

Text Inputs can be configured with the following fields:
### style
Sets the <net.dv8tion.jda.api.components.textinput.TextInputStyle>. 
The default value is <TextInputStyle#PARAGRAPH>.
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

You can reply to [`CommandEvents`][[CommandEvent]] 
and [`ComponentEvents`][[com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent]]
with a Modal by calling <ModalReplyableEvent#replyModal(java.lang.String,Entry...)>
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
<ModalReplyableEvent#replyModal(java.lang.String,java.util.function.Function)>

!!! example
    ```java
    event.replyModal("onModal", modal -> modal.title("Cool Title!"));
    ```

If you want to, you can also use the native JDA builder:
!!! example
    ```java
    event.replyModal("onModal", modal -> modal.modify(jdaBuilder -> jdaBuilder.setTitle("Boring Title!")));
    ```
