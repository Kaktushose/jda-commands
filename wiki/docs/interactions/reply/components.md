!!! note
    This section assumes that you are already familiar with Components V2. You can find the Discord documentation [here](https://discord.com/developers/docs/change-log/2025-04-22-components-v2).
    Also, please have a look at JDAs component [classes](https://docs.jda.wiki/net/dv8tion/jda/api/components/package-summary.html).

    It is also advisable to read the section about [Legacy Components](./legacy.md) first.

## Replying with Components V2

You can reply with Components V2 by passing one or more <MessageTopLevelComponent>s to the
[`reply()`][[ReplyableEvent#reply(Collection, Entry...)]] method.
This will automatically enable the V2 flag. Note that this method is only available either directly at the event class or at the <ConfigurableReply>
stage (accessed by calling [`with()`][[ReplyableEvent#with()]]).
!!! example
    ```java
    event.reply(TextDisplay.of("Hello World"));

    event.with().ephemeral(true).reply(TextDisplay.of("Hello World"));
    ```
Note, that you cannot reply with Components V2 after:

- `components(...)`
- `embeds(...)`
- `builder(...)`

has been called, because adding content, embeds, files, etc. disqualifies the message from being Components V2.

### Action Components
Components V2 also can have action components. They can either be added to an <ActionRow> or a <SectionAccessoryComponent>
as part of a <net.dv8tion.jda.api.components.section.Section>. You add them by using the
<io.github.kaktushose.jdac.dispatching.reply.Component> class:

!!! example
    ```java
    event.reply(Section.of(Component.button("onButton"), TextDisplay.of("Useless Button")));
    
    event.reply(ActionRow.of(Component.stringSelect("onMenu"))));
    ```

[Enabling and disabling](./legacy.md#enabling-disabling) as well as [modifying](./legacy.md#dynamic-components) works the same
as explained before.

!!! tip
    You can use <io.github.kaktushose.jdac.dispatching.reply.Component#row(String...)> as a shortcut for creating <ActionRow>s.
    ```java
    event.reply(Component.row("onMenu"));
    ```

### Subsequent Replies
Once you've sent a Components V2 message it has to remain Components V2. The [Reply Configuration](./overview.md#reply-configuration) rules
also apply to Components V2.

Often, you want to keep the original components, but edit some of them. You can do so by calling
[`reply(ComponentReplacer... replacer)`][[ComponentEvent#reply(ComponentReplacer, Entry...)]].
This method will enforce `keepComponents` and throw an <UnsupportedOperationException> if the message isn't Components V2.

!!! example
    ```java
    public void onComponent(ComponentEvent event) {
        event.reply(ComponentReplacer.byUniqueId(1, TextDisplay.of("Updated Component")));
    }
    ```

To make working with the <ComponentReplacer> easier, you can assign unique ids to components. For action components, which
are defined by JDA-Commands, you can either use the component annotation (e.g. `@Button`) or the [dynamic components API](./legacy.md#dynamic-components).
