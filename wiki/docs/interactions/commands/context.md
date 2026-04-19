# Context Commands
Both types of context commands are defined by the same <io.github.kaktushose.jdac.annotations.interactions.Command>
annotation. The first parameter must always be a <CommandEvent>.
The name and other metadata of the command is passed to the annotation.

## Message Context
For message context commands the second method parameter must be a <Message>
and the `type` must be <net.dv8tion.jda.api.interactions.commands.Command.Type#MESSAGE>.
```java
@Command(value = "Delete this message", type = Command.Type.MESSAGE)
public void onDeleteMessage(CommandEvent event, Message target) { ... }
```

## User Context
For user context commands the second method parameter must be a <User>
and the `type` must be <Command.Type#USER>.
```java
@Command(value = "Ban this user", type = Command.Type.USER)
public void onBanMember(CommandEvent event, User user) { ... }
```

Alternatively, you can also use <net.dv8tion.jda.api.entities.Member> in the method signature. However, this only works for
<InteractionContextType#GUILD>.

```java
@Command(value = "Ban this user", type = Command.Type.USER)
@CommandConfig(context = InteractionContextType.GUILD) // default value
public void onBanMember(CommandEvent event, Member member) { ... }
```