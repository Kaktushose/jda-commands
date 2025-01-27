# Overview
!!! info
    Please make yourself familiar with our [Runtime Concept](../start/runtime.md) before you proceed. This is a 
    centerpiece of JDA-Commands and a requirement for understanding the following parts.

## Structure
In JDA-Commands you define interactions as methods. These methods must be contained in a class annotated with 
[`@Interaction`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/annotations/interactions/Interaction.html),
which is also referred to as the _interaction controller_. 

Each method controls one interaction. The interaction data gets defined by annotations and sometimes by the method signature. 
JDA-Commands will call the method when the interaction gets executed by a user. 

!!! tip
    It is recommended that you define one _conversation_ per class. By conversation, we mean a logical sequence of
    interactions, for example a Slash Command that is replied to with a Button followed by a Modal.   

```java
@Interaction//(1)!
public class GreetCommand {
    
    @SlashCommand(value = "greet user", desc = "Play cookie clicker")//(2)!
    public void onGreet(CommandEvent event, @Param("The user you want to greet") User user) {//(3)!
        event.reply("Hello %s", user.getAsMention());
    }
}
```

1. This marks the `GreetCommand` class as an interaction controller.
2. This defines the Slash Command and also tells JDA-Commands to call the `onGreet` method for this command.
3. In that case the method signature also defines part of the interaction.


## Runtime Scoped Instances
JDA-Commands will create one instance of the interaction controller per conversation. That way you don't need to worry
about the scope of your variables. Even if multiple users execute your interaction simultaneously, they cannot affect
the state of other executions. 

Let's say we have the following code:
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
    
    @SlashCommand(value = "greet") //(1)!
    public void onGreet(CommandEvent event) {
        event.reply("Hello World!");
    }
    
}
```

1. This is bad practise, you shouldn't mix conversations. This is only for explaining purposes.

Let's see what's going on here:

- Both `onClicker` and `onGreet` are an entrypoint for starting a new conversation. Everytime they get executed a new
[Runtime](../start/runtime.md) will be started that will also create an instance of `CookieClicker`. This also means 
that you cannot exchange values between these two commands using class variables.

- `onCookie` is linked the `onClicker` and will use the same instance of `CookieClicker` as on `onClicker`.

- `onCookie` is only executable as long as the [Runtime](../start/runtime.md) is alive and thus the instance of 
`CookieClicker` exists. You can circumvent this by making `onCookie` [independent](../start/runtime.md#independent).
