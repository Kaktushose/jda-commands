# Quick Start Guide
## Entrypoint
```java title="Main.java"
public class Main {

   public static void main(String[] args) {
        JDA jda = yourJDABuilding();
        JDACommands jdaCommands = JDACommands.start(jda, Main.class);
   }
   
}
```

## Defining Interactions

```java title="HelloWorld.java"
@Interaction
public class HelloWorld {

    @SlashCommand("greet")
    public void onCommand(CommandEvent event) {
        event.reply("Hello World!");
    }

}
```