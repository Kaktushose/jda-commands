# Messages
Messages are a central part of JDA-Commands, they include content send to the user via the reply API,
strings used in framework annotations and modals and much more.

To provide the best user experience possible, JDA-Commands comes included with [localization](localization.md),
support for [placeholders](placeholder.md) and [Unicode and app emojis](emojis.md). In many places in the framework,
these feature are applied [implicitly](localization.md#implicit-localization).

## Resolution
The features listed above are all pipelined together with help of the <MessageResolver>.

They are applied to the string provided by the user in following order:

1. [resolution of placeholders](placeholder.md)
2. [localization](localization.md)
3. [resolution of emoji aliases](emojis.md)

The resulting string is then send to Discord or returned to the user.

## Usage
Instead of manually using the <MessageResolver>
class, JDA-Commands allows for implicit resolution
of messages in many common please. These include:

- [Component API](../interactions/components.md) including the corresponding annotations like <io.github.kaktushose.jdac.annotations.interactions.Button>,
  <io.github.kaktushose.jdac.annotations.interactions.Modal> etc.
- [Embed API](embeds.md) 
- [Reply API](../interactions/reply.md), for example the string content of a message <Reply#reply(String,Entry...)>

If you are using localization, please take a look at [implicit localization](localization.md#implicit-localization).

!!! warning
    Message resolution of content in <MessageCreateData> is not supported implicitly.
    You have to use <MessageResolver>
    to resolve such messages.

