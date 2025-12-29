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
- [Reply API](../interactions/reply.md), for example the string content of a message <ReplyableEvent#reply(String,Entry...)>

If you are using localization, please take a look at [implicit localization](localization.md#implicit-localization).

!!! warning
    Message resolution of content in <MessageCreateData> is not supported implicitly.
    You have to use <MessageResolver>
    to resolve such messages.

## Adding custom string resolvers
Sometimes it's necessary to introduce custom resolution logic. JDA-Commands provides <Property#STRING_RESOLVER>
(configurable by <JDACBuilder#stringResolver(Resolver...)>) allowing the user to add own implementations of 
[`Resolver<String>`][[io.github.kaktushose.jdac.message.resolver.Resolver]].

Each <io.github.kaktushose.jdac.message.resolver.Resolver> has a [priority][[Resolver#priority()]] affecting the order in which all registered
string resolvers are applied by <MessageResolver>. A lower priority is applied first (e.g. <I18n> runs after <PlaceholderResolver>).

The default resolvers have the following priorities:

- <PlaceholderResolver> = 100
- <I18n> = 200
- <EmojiResolver> = 300

### Example
```java
public class URLResolver implements Resolver<String> {
    
    public String resolve(String msg, Locale locale, Map<String, @Nullable Object> placeholders) {
        return msg.replace("JDAC_GH", "https://github.com/Kaktushose/jda-commands");
    }
    
    public int priority() {
        return 350; // should run after all built in resolvers
    }
}
```

