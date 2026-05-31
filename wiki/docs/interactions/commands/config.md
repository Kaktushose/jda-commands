Use the <io.github.kaktushose.jdac.annotations.interactions.CommandConfig> annotation to configure the
following settings. You can either annotate a command method directly or annotate the interaction controller class. It
is also possible to set a [global command config][[JDACBuilder#globalCommandConfig(CommandDefinition.CommandConfig)]]
at the builder:

!!! example "Global CommandConfig"
    ```java
    JDACommands.builder(jda, Main.class)
        .globalCommandConfig(CommandConfig.of(config -> config.nsfw(true))
        .start();
    ```

JDA-Commands will apply clashing CommandConfigs in the following hierarchy:

1. `CommandConfig` method annotation
2. `CommandConfig` class annotation
3. global `CommandConfig`

## Enabled For (Permissions)
Sets the [`Discord Permissions`][[net.dv8tion.jda.api.Permission]] a command will be enabled for. By default, a command
will be enabled for every permission.
!!! danger
    Guild admins can modify these permissions at any time! If you want to enforce permissions or secure a critical command
    further you should use the permissions system of JDA-Commands. You can read more about it [here](../../middlewares/permissions.md).

```java
@CommandConfig(enabledFor = Permission.BAN_MEMBERS)
@Command(value = "example")
public void onCommand(CommandEvent event) {...}
```

## Interaction Context Type
Sets the [`InteractionContextTypes`][[InteractionContextType]]
of a command. The default value is <InteractionContextType#GUILD>.

```java
@CommandConfig(context = {InteractionContextType.GUILD, InteractionContextType.BOT_DM})
@Command(value = "example")
public void onCommand(CommandEvent event) {...}
```

## Integration Type
Sets the [`IntegrationTypes`][[IntegrationType]]
of a command. The default value is <IntegrationType#GUILD_INSTALL>.

```java
@CommandConfig(integration = {IntegrationType.GUILD_INSTALL, IntegrationType.USER_INSTALL})
@Command(value = "example")
public void onCommand(CommandEvent event) {...}
```

## NSFW
Sets whether a command can only be executed in NSFW channels. The default value is `false`.

```java
@CommandConfig(isNSFW = true)
@Command(value = "example")
public void onCommand(CommandEvent event) {...}
```

## Scope (Guild & Global Commands)
Sets whether a command should be registered as a `global` or as a `guild` command. The default value is `global`.

```java
@CommandConfig(scope = CommandScope.GUILD)
@Command(value = "example")
public void onCommand(CommandEvent event) {...}
```

When having guild scoped commands you have to use the <GuildScopeProvider> to
tell JDA-Commands what guilds a command should be registered for.

Let's say we have a paid feature in our bot:
!!! example
    ```java
    @CommandConfig(scope = CommandScope.GUILD)
    @Command(value = "paid feature")
    public void onCommand(CommandEvent event) {
        event.reply("Hello World!");
    }
    ```

We then need to implement a <GuildScopeProvider> to only register this command
for guilds that have paid for that feature:
!!! example
    ```java
    public class PremiumGuildsProvider implements GuildScopeProvider {

        @Override
        public Set<Long> apply(CommandData commandData) {
            if (commandData.getName().equals("paid feature")) {
                // this is the place where you could also perform a database lookup
                return Set.of(1234567890L);
            }
            return Set.of();
        }
    }
    ```

Finally, we have to register our `PremiumGuildsProvider` either at the builder or by annotating it with <io.github.kaktushose.jdac.guice.Implementation>.

!!! example
    === "`@Implementation`"
        ```java
        @Implementation
        public class PremiumGuildsProvider implements GuildScopeProvider {
            ...
        }
        ```

    === "Builder Registration"
        ```java
        JDACommands.builder(jda, Main.class)
            .guildScopeProvider(new PremiumGuildsProvider())
            .start();
        ```

!!! note
    Using the <io.github.kaktushose.jdac.guice.Implementation> annotation requires the guice integration
    (shipped by default). You can read more about it [here](../../di.md).   