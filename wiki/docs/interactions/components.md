# Components
!!! note
    This section only covers how you define components. See the [Reply API section](./reply.md) to learn how to use them
    in replies.

## Localization and Placeholders
To avoid hardcoded values, all string values of an annotation can be replaced by a localization key as supported by the
current used [Localization System](../message/localization.md).

Furthermore, it's possible to directly use placeholders.
For more information on how to use placeholders please visit [this page](../message/placeholder.md).

Also take a look at the general [message resolution documentation](../message/overview.md).

!!! example "Example (with Fluava)"
    ```java
    @Button(value = "my.localization.key", style = ButtonStyle.DANGER, link = "{ $jdac_link }")
    public void onButton(ComponentEvent event) {...}
    ```

!!! warning "The Dollar Sign ($)"
    The dollar sign is a reserved character for bundle name separation.
    In most cases that shouldn't bother you but if you encounter any problems,
    please read the notes [here](../message/localization.md#the-dollar-character).

## Unicode and application emojis
JDA-Commands has built in support for Unicode and application emoji aliases.
If you want to use them, just take a look [here](../message/emojis.md).

## Buttons
Buttons are defined by annotating a method with <com.github.kaktushose.jda.commands.annotations.interactions.Button>. 
The first parameter must always be a <com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent>.
The label and other metadata of the button is passed to the annotation.
```java
@Button("example")
public void onButton(ComponentEvent event) {...}
```

### style
Sets the style of a button.

![Button Styles](https://jda.wiki/assets/images/interactions/ButtonExamples.png)

!!! example
    ```java
    @Button(value = "example", style = ButtonStyle.DANGER)
    public void onButton(ComponentEvent event) {...}
    ```

!!! note
    <net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle#PREMIUM> is not supported by JDA-Commands.

### emoji
Sets the emoji of a button.
!!! example
    ```java
    @Button(value = "Emoji", emoji = "🤗")
    public void onButton(ComponentEvent event) {...}
    ```

### link
Buttons that have a link cannot be executed, but they are still defined like normal buttons.
!!! example
    ```java
    @Button(value = "JDA-Commands Wiki", link = "https://kaktushose.github.io/jda-commands/wiki/")
    public void onButton(ComponentEvent event) { }
    ```

## Select Menus
### String Select Menus
String Select Menus are defined by annotating a method with <com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu>
The first parameter must always be a <com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent>.
The second parameter must be a <java.util.List>.

The placeholder and other metadata of the String Select Menu is passed to the annotation. 

Select Options are defined by annotating the method with <com.github.kaktushose.jda.commands.annotations.interactions.SelectOption>.

!!! example
    ```java
    @SelectOption(label= "Pizza", value = "pizza")
    @SelectOption(label= "Hamburger", value = "hamburger")
    @SelectOption(label= "Sushi", value = "Sushi")
    @StringSelectMenu("What's your favourite food?")
    public void onMenu(ComponentEvent event, List<String> choices) { ... }
    ```

#### Min & Max Value
String Select Menus support up to 25 options. You can set the minimum and maximum value by using the `minValue` and 
`maxValue` fields.

!!! example
    ```java
    @SelectOption(label= "Pizza", value = "pizza")
    @SelectOption(label= "Hamburger", value = "hamburger")
    @SelectOption(label= "Sushi", value = "Sushi")
    ...
    @StringSelectMenu(value = "What's your favourite food?", minValue = 2, maxValue = 4)
    public void onMenu(ComponentEvent event, List<String> choices) { ... }
    ```

### Entity Select Menus
Entity Select Menus are defined by annotating a method with <com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu>.
The first parameter must always be a <com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent>.
The second parameter must be of type <net.dv8tion.jda.api.entities.Mentions>.

!!! example
    ```java
    @EntitySelectMenu(value = SelectTarget.USER, placeholder = "Who's your favourite user?")
    public void onMenu(ComponentEvent event, Mentions mentions) { ... }
    ```

#### Channel Types
When using `SelectTarget.CHANNEL` you can limit the selectable channel types with the `channelTypes` field.

!!! example
    ```java
    @EntitySelectMenu(
                value = SelectTarget.CHANNEL, 
                placeholder = "What's your favourite channel?", 
                channelTypes = {ChannelType.TEXT, ChannelType.VOICE}
    )
    public void onMenu(ComponentEvent event, Mentions mentions) { ... }
    ```

#### Default Values
You can set the [default values](net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.DefaultValue)
of the Entity Select Menu by using respectively the `defaultChannels`, `defaultRoles` or `defaultUsers` fields. 

!!! example
    ```java
    @EntitySelectMenu(
                value = SelectTarget.CHANNEL, 
                placeholder = "What's your favourite channel?",
                defaultChannels = {0123456789L, 9876543210L}
    )
    public void onMenu(ComponentEvent event, Mentions mentions) { ... }
    ```

#### Min & Max Value
Just as for String Select Menus you can set the minimum and maximum value by using the `minValue` and `maxValue` fields.