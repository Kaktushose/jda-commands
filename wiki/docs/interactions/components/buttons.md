!!! tip
    This section only covers how you define action components. You might also find the following resources useful:

    - Replying with the [Legacy Component System](../reply/legacy.md)
    - [Components V2 Guide](../reply/components.md)
    - [Component Utils](../reply/utils.md)

Buttons are defined by annotating a method with <io.github.kaktushose.jdac.annotations.interactions.Button>.
The first parameter must always be a <io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent>.
The label and other metadata of the button is passed to the annotation.
```java
@Button("example")
public void onButton(ComponentEvent event) {...}
```

## Style
Sets the style of a button.

![Button Styles](https://jda.wiki/assets/images/interactions/ButtonExamples.png)

!!! example
    ```java
    @Button(value = "example", style = ButtonStyle.DANGER)
    public void onButton(ComponentEvent event) {...}
    ```

!!! note
    <ButtonStyle#PREMIUM> is not supported by JDA-Commands.

## Emoji
Sets the emoji of a button.
!!! example
    ```java
    @Button(value = "Emoji", emoji = "🤗")
    public void onButton(ComponentEvent event) {...}
    ```

## Link
Buttons that have a link cannot be executed, but they are still defined like normal buttons.
!!! example
    ```java
    @Button(value = "JDA-Commands Wiki", link = "https://kaktushose.github.io/jda-commands/wiki/")
    public void onButton(ComponentEvent event) { }
    ```

## Localization and Emojis
To avoid hardcoded values, all string values of an annotation can be replaced by a localization key as supported by the
current used [Localization System](../../message/localization.md).

Furthermore, it's possible to directly use placeholders.
For more information on how to use placeholders please visit [this page](../../message/placeholder.md).

Also take a look at the general [message resolution documentation](../../message/overview.md).

!!! example "Example (with Fluava)"
    ```java
    @Button(value = "my.localization.key", style = ButtonStyle.DANGER, link = "{ $jdac_link }")
    public void onButton(ComponentEvent event) {...}
    ```

!!! warning "The Dollar Sign ($)"
    The dollar sign is a reserved character for bundle name separation.
    In most cases that shouldn't bother you but if you encounter any problems,
    please read the notes [here](../../message/localization.md#the-dollar-character).

JDA-Commands has built in support for Unicode and application emoji aliases. If you want to use them, just take a look [here](../../message/emojis.md).