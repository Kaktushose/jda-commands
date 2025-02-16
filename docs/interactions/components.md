# Components
!!! note
    This section only covers how you define components. See the [Reply API section](./reply.md) to learn how to use them
    in replies.
## Buttons
Buttons are defined by annotating a method with [`@Button`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/annotations/interactions/Button.html). 
The first parameter must always be a [`ComponentEvent`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/events/interactions/ComponentEvent.html).
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
    [`ButtonStyle.PREMIUM`](https://docs.jda.wiki/net/dv8tion/jda/api/interactions/components/buttons/ButtonStyle.html#PREMIUM) 
    is not supported by JDA-Commands.

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
    public void onButton(ComponentEvent event) {...}
    ```

## Select Menus
### String Select Menus
String Select Menus are defined by annotating a method with [`@StringSelectMenu`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/annotations/interactions/StringSelectMenu.html).
The first parameter must always be a [`ComponentEvent`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/events/interactions/ComponentEvent.html).
The second parameter must be a [`List`](https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/util/List.html).

The placeholder and other metadata of the String Select Menu is passed to the annotation. 

Select Options are defined by annotating the method with
[`@SelectOption`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/annotations/interactions/SelectOption.html).

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
Entity Select Menus are defined by annotating a method with [`@EntitySelectMenu`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/annotations/interactions/EntitySelectMenu.html).
The first parameter must always be a [`ComponentEvent`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/events/interactions/ComponentEvent.html).
The second parameter must be of type [`Mentions`](https://docs.jda.wiki/net/dv8tion/jda/api/entities/Mentions.html).

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
You can set the [default values](https://docs.jda.wiki/net/dv8tion/jda/api/interactions/components/selections/EntitySelectMenu.DefaultValue.html)
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