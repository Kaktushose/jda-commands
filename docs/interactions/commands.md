# Commands
!!! info
    If you're new to JDA and Discord Bots in general, please make yourself familiar with the
    [JDA wiki](https://jda.wiki/using-jda/interactions/) first. We assume that the basic structure of interactions is known.

## Slash Commands
SlashCommands are defined by annotating a method with [`@SlashCommand`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/annotations/interactions/SlashCommand.html).
The first parameter must always be a [`CommandEvent`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/dispatching/events/interactions/CommandEvent.html).
The name of the command is passed to the annotation.
```java
@SlashCommand("example")
public void onCommand(CommandEvent event) {...}
```
### Sub Commands & Sub Command Groups
In contrast to JDA, JDA-Commands doesn't differentiate between slash commands, sub command groups and sub commands.
JDA-Commands determines the type automatically based on the command names. 

Let's say we have the following commands in our moderation bot:
```java
@SlashCommand("delete")
public void onDeleteMessages(CommandEvent event) {...}

@SlashCommand("moderation warn")
public void onWarnMember(CommandEvent event) {...}

@SlashCommand("moderation kick")
public void onKickMember(CommandEvent event) {...}

@SlashCommand("moderation ban")
public void onBanMember(CommandEvent event) {...}
```
JDA-Commands will create a tree structure of these commands:


### Command Options
### Choices
### AutoComplete
## Context Commands
### Message Context
### User Context
## Additional Settings
### isGuildOnly
### isNSFW
### enabledFor
### scope