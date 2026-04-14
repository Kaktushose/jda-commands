# Migrate to V5
With V4 we have made a clean cut during development to finally have a stable release. However, V4 is based on JDA 5 and
doesn't have support for Components V2 or Modals. With JDA-Commands V5, we finally support these features. But as always, 
some things have changed in the meantime. This guide will help you to migrate from V4 to V5.

## New Features
- You can now reply with [Components V2](./interactions/reply.md#components-v2)
- The [Modal API](./interactions/modals.md#modal-components) has changed to also support CV2
- The new [Introspection API](./misc/introspection.md) allows you to access JDA-Commands' properties and event system
- The [Reply Config](./interactions/reply.md#replyconfig) now supports JDA's `setSuppresedNotifications` and `setAllowedMentions`
- [Command Choices](./interactions/commands.md#choices) can now be dynamically loaded 
- <IMentionable> objects now will be automatically localized
- You can now update the guild commands for a [specific guild][[JDACommands#updateGuildCommands(Collection)]]
- You can now add custom [String Resolvers](./message/overview.md#adding-custom-string-resolvers) to the localization system
 
!!! note
    You can find the full changelog [here](https://github.com/Kaktushose/jda-commands/releases/tag/v5.0.0)

## Changes
- After calling <MessageReply#embeds(String...)>, <MessageReply#components(String...)> or <MessageReply#builder(Consumer)> you can no longer access the <ConfigurableReply>
- <InteractionDefinition.ReplyConfig#editReply()>, <InteractionDefinition.ReplyConfig#keepComponents()> and <InteractionDefinition.ReplyConfig#keepSelections()> can now only be called on a <jdac -> ComponentEvent>
- The <ErrorMessageFactory> now uses Components V2. It has to return a <MessageTopLevelComponent> instead of <MessageCreateData>
- The <DefaultErrorMessageFactory> now uses Components V2
- Removed `EmbedConfig#errorSource()`
- Removed `Event#i18n()`, use <Event#messageResolver()> instead

## Renaming
- `ErrorMessageFactory#getCommandExecutionFailedMessage()` -> <ErrorMessageFactory#getInteractionExecutionFailedMessage()>
- `@EntitySelectMenu` -> <@EntityMenu>
- `@StringSelectMenu` -> <@StringMenu>
- `Event#localize` -> <Event#resolve>
- `Priority#PERMISSIONS` -> <Priority#HIGHEST>
