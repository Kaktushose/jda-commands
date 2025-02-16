# Modals
!!! note
    This section only covers how you define modals. See the [Reply API section](./reply.md) to learn how to use them
    in replies.

Modals are defined by annotating a method with [`@Modal`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/annotations/interactions/Modal.html).
The first parameter must always be a [`ModalEvent`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/events/interactions/ModalEvent.html).

```java
@Modal("My Modal")
public void onModal(ModalEvent event, @TextInput("Input") String input) { ... }
```

## Text Input
You can add text inputs to a modal by adding String parameters annotated with [`@TextInput`](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/interactions/TextInput.html).
The label and other metadata of the text input is passed to the annotation. 

!!! tip
    Just as for command options, the parameter name will be used as the label by default. However, this requires the 
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