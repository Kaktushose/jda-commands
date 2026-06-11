!!! tip
    This section only covers how you define action components. You might also find the following resources useful:

    - Replying with the [Legacy Component System](./reply/legacy.md)
    - [Components V2 Guide](./reply/components.md)
    - [Component Utils](./reply/utils.md)

## String Select Menus
String Select Menus are defined by annotating a method with <io.github.kaktushose.jdac.annotations.interactions.StringMenu>
The first parameter must always be a <io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent>.
The second parameter must be a <java.util.List>.

The placeholder and other metadata of the String Select Menu is passed to the annotation.

Select Options are defined by annotating the method with <MenuOption>.

!!! example
    ```java
    @MenuOption(label= "Pizza", value = "pizza")
    @MenuOption(label= "Hamburger", value = "hamburger")
    @MenuOption(label= "Sushi", value = "Sushi")
    @StringMenu("What's your favourite food?")
    public void onMenu(ComponentEvent event, List<String> choices) { ... }
    ```

### Min & Max Value
String Select Menus support up to 25 options. You can set the minimum and maximum value by using the <StringMenu#minValue()> and <StringMenu#maxValue()>
fields.

!!! example
    ```java
    @SelectOption(label= "Pizza", value = "pizza")
    @SelectOption(label= "Hamburger", value = "hamburger")
    @SelectOption(label= "Sushi", value = "Sushi")
    ...
    @StringMenu(value = "What's your favourite food?", minValue = 2, maxValue = 4)
    public void onMenu(ComponentEvent event, List<String> choices) { ... }
    ```

### Required
For Modals, you can also configure whether the user must populate the select menu. You can do this directly via the annotation.
For example, you can have an optional select menu with the range set to [2 ; 5], meaning you accept either 0 options, or, at least 2 but at most 5.

!!! example
    ```java
    @SelectOption(...)
    @StringMenu(value = "What's your favourite food?", minValue = 2, maxValue = 4, required = true)
    public void onMenu(ComponentEvent event, List<String> choices) { ... }    
    ```

## Entity Select Menus
Entity Select Menus are defined by annotating a method with <io.github.kaktushose.jdac.annotations.interactions.EntityMenu>.
The first parameter must always be a <io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent>.
The second parameter must be of type <Mentions>.

!!! example
    ```java
    @EntityMenu(value = SelectTarget.USER, placeholder = "Who's your favourite user?")
    public void onMenu(ComponentEvent event, Mentions mentions) { ... }
    ```

### Channel Types
When using <SelectTarget#CHANNEL> you can limit the selectable channel types with the <EntityMenu#channelTypes()> field.

!!! example
    ```java
    @EntityMenu(
                value = SelectTarget.CHANNEL, 
                placeholder = "What's your favourite channel?", 
                channelTypes = {ChannelType.TEXT, ChannelType.VOICE}
    )
    public void onMenu(ComponentEvent event, Mentions mentions) { ... }
    ```

### Default Values
You can set the [default values][[EntitySelectMenu.DefaultValue]]
of the Entity Select Menu by using respectively the <EntityMenu#defaultChannels()>, <EntityMenu#defaultRoles()> or <EntityMenu#defaultUsers()> fields.

!!! example
    ```java
    @EntityMenu(
                value = SelectTarget.CHANNEL, 
                placeholder = "What's your favourite channel?",
                defaultChannels = {0123456789L, 9876543210L}
    )
    public void onMenu(ComponentEvent event, Mentions mentions) { ... }
    ```

### Min & Max Value
Just as for String Select Menus you can set the minimum and maximum value by using the <EntityMenu#minValue()> and <EntityMenu#maxValue()> fields.

### Required
Again, this is handled the same way as with String Select Menus.

## Localization and Emojis
To avoid hardcoded values, all string values of an annotation can be replaced by a localization key as supported by the
current used [Localization System](../../message/localization.md).

Furthermore, it's possible to directly use placeholders.
For more information on how to use placeholders please visit [this page](../../message/placeholder.md).

Also take a look at the general [message resolution documentation](../../message/overview.md).

!!! example "Example (with Fluava)"
    ```java
    @EntityMenu(value = SelectTarget.USER, placeholder = "{ $select-user-placeholder }")
    public void onMenu(ComponentEvent event, Mentions mentions) { ... }
    ```

!!! warning "The Dollar Sign ($)"
    The dollar sign is a reserved character for bundle name separation.
    In most cases that shouldn't bother you but if you encounter any problems,
    please read the notes [here](../../message/localization.md#the-dollar-character).

JDA-Commands has built in support for Unicode and application emoji aliases. If you want to use them, just take a look [here](../../message/emojis.md).