# Overview
!!! info
    Please make yourself familiar with our [Runtime Concept](../start/runtime.md) before you proceed. This is a 
    centerpiece of JDA-Commands and a requirement for understanding the following parts.

## Structure
In JDA-Commands you define interactions as methods. These methods must be contained in a class annotated with 
<io.github.kaktushose.jdac.annotations.interactions.Interaction>,
which is also referred to as the _interaction controller_. 

Each method controls one interaction. The interaction data gets defined by annotations and sometimes by the method signature. 
JDA-Commands will call the method when the interaction gets executed by a user. 

!!! tip
    It is recommended that you define one _conversation_ per class. By conversation, we mean a logical sequence of
    interactions, for example a Slash Command that is replied to with a Button followed by a Modal.   

```java
@Interaction//(1)!
public class GreetCommand {
    
    @Command(value = "greet user", desc = "Play cookie clicker")//(2)!
    public void onGreet(CommandEvent event, @Param("The user you want to greet") User user) {//(3)!
        event.reply("Hello %s", user.getAsMention());
    }
}
```

1. This marks the `GreetCommand` class as an interaction controller.
2. This defines the Slash Command and also tells JDA-Commands to call the `onGreet` method for this command.
3. In that case the method signature also defines part of the interaction.


## Runtime Scoped Instances
JDA-Commands will create one instance of the interaction controller class per conversation, which is stored in the corresponding Runtime.
That way you don't need to worry about the scope of your variables. Even if multiple users execute your interaction simultaneously, they cannot affect
the state of other executions. 

Let's say we have the following code:
```java
@Interaction
public class CookieClicker {

    private int counter;
    
    @Command(value = "cookie clicker", desc = "Play cookie clicker")//(1)!
    public void onClicker(CommandEvent event) {
        event.with().components("onCookie").reply("You've got %s cookie(s)!", counter);
    }
    
    @Button(value = "Collect", emoji = "🍪", style = ButtonStyle.SUCCESS)//(2)!
    public void onCookie(ComponentEvent event) {
        event.reply("You've got %s cookie(s)!", ++counter);
    }
    
}
```

1. This will be a command called `/cookie clicker`
2. This will be a button labeled with `Collect 🍪` 

Let's see what's going on here:

- The `/cookie clicker` command is an entrypoint for starting a new conversation. Everytime the command gets executed
JDA will hand over a [`SlashCommandInteractionEvent`][[SlashCommandInteractionEvent]]
to JDA-Commands, which is used to create a new [Runtime](../start/runtime.md). 

- This [Runtime](../start/runtime.md) will then create a new instance of the `CookieClicker` class. This instance is used
to execute the `onClicker` method.

- When the `Collect 🍪` button gets clicked the same [Runtime](../start/runtime.md) and thus the same instance of the 
`CookieClicker` class will be used to execute the `onCookie` method.

- This also means the `Collect 🍪` button is only usable as long as the [Runtime](../start/runtime.md) is alive and thus the instance of 
the `CookieClicker` class exists. You can circumvent this by making the `Collect 🍪` button [independent](../start/runtime.md#independent).
