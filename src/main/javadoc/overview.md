# JDA-Commands

### A declarative, annotation driven interaction framework for JDA.

---

## Resources

This is the official documentation for jda-commands. If you are new to jda-commands
(or [JDA](https://jda.wiki/introduction/jda/) in general) you might also find the following resources helpful:

- [JDA-Commands Wiki](https://github.com/Kaktushose/jda-commands/wiki)
- [Release Notes](https://github.com/Kaktushose/jda-commands/releases)
- [JDA Wiki](https://jda.wiki/)

Having trouble or found a bug?

- Check out the [Examples](https://github.com/Kaktushose/jda-commands/tree/main/src/examples)
- Join the [Discord Server](https://discord.gg/JYWezvQ)
- Or open an [Issue](https://github.com/Kaktushose/jda-commands/issues)

## Runtime Concept

One of the core concepts in jda-commands is the so-called `Runtime`. It is mentioned frequently in the docs. A `Runtime` delegates the jda events to their corresponding `EventHandlers` and manages the used virtual threads.

A new `Runtime` is created each time a [`SlashCommandInteractionEvent`](https://javadoc.io/doc/net.dv8tion/JDA/latest/net/dv8tion/jda/api/events/interaction/command/SlashCommandInteractionEvent.html),
[`GenericContextInteractionEvent`](https://javadoc.io/doc/net.dv8tion/JDA/latest/net/dv8tion/jda/api/events/interaction/command/GenericContextInteractionEvent.html)
or [`CommandAutoCompleteInteractionEvent`](https://javadoc.io/doc/net.dv8tion/JDA/latest/net/dv8tion/jda/api/events/interaction/command/CommandAutoCompleteInteractionEvent.html) is provided by jda
or if an interaction is marked as [*independent*](jda.commands/com/github/kaktushose/jda/commands/dispatching/reply/Component.html#independent(java.lang.String...)).

Runtimes are executed in parallel, but events are processed sequentially by each runtime.
Every `EventHandler` called by this `Runtime` is executed in its own virtual thread, isolated from the runtime one.

See [`ExpirationStrategy`](jda.commands/com/github/kaktushose/jda/commands/dispatching/ExpirationStrategy.html) for
details when a `Runtime` will close.

<img src="doc-files/flowchart.png" alt="event/runtime flowchart" width="100%"/>
