# Type Adapters
[`TypeAdapters`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/adapter/TypeAdapter.html)
are a part of the execution chain. They are used to adapt the input of a slash command to the correct type needed to invoke the method.

## Default Type Adapters
JDA-Commands provides the following type adapters by default:

- all primitive types and their respective wrapper types
- User
- Member
- Role
- Channel and subtypes (e.g. StageChannel, NewsChannel, etc.)

You can add any of these types as a parameter to your slash command methods. See [Command Options](../interactions/commands.md#command-options)
for details.

## Writing Own Type Adapters

!!! example
    === "Command"
        ```java
        @SlashCommand("example")
        public void onCommand(CommandEvent event, CustomType object) {
            ...
        }
        ```

    === "Type Adapter (`@Implementation` Registration)"
        ```java
        @Implementation(clazz = CustomType.class)
        public class UserProfileTypeAdapter implements TypeAdapter<CustomType> {
            
            public Optional<CustomType> apply(String raw, GenericInteractionCreateEvent event) {
                return Optional.of(new CustomType(raw, event));
            }

        }
        ```

    === "Type Adapter (Builder Registration)"
        ```java
        public class UserProfileTypeAdapter implements TypeAdapter<CustomType> {
            
            public Optional<CustomType> apply(String raw, GenericInteractionCreateEvent event) {
                return Optional.of(new CustomType(raw, event));
            }

        }
        ```
        ```java
        JDACommands.builder()
            .adapter(CustomType.class, new UserProfileTypeAdapter());
            .start(jda, Main.class);
        ```
