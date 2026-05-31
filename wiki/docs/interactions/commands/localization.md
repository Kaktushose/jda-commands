JDA uses the <LocalizationFunction> for localizing slash commands.
We implement this interface based on <MessageResolver> (see the [localization wiki](../../message/overview.md#resolution) for details). 
If you want to disable command localization just call [`JDACBuilder#localizeCommands(false)`][[JDACBuilder#localizeCommands(boolean)]].

The [bundle](../../message/localization.md#bundles) used is generally `default`.
If the [interaction controller class](../../interactions/overview.md#structure) or the `package-info.java` in the same package as the class
is annotated by <Bundle>, the specified bundle will be used instead. If the localization key isn't found in this bundle, the `default`
bundle will be searched as a fallback option.

!!! example
    ```java title="main/Greet.java"
    @Bundle("greet")
    @Interaction
    public class Greet {
        
        @Command("greet user")
        public void onCommand(CommandEvent event, Member member) { 
            event.reply("Hello World %s!".formatted(member.getAsMention()));
        }    
    }
    ```
    
    ```properties title="resources/greet_en.ftl"
    greet-user-description=Greets a user
    greet-user-options-member-name=user
    greet-user-options-member-description=The user to greet
    ```
    
    ```properties title="resources/greet_de.ftl"
    greet-name=grüße
    greet-user-name=nutzer
    greet-user-description=Begrüßt einen Nutzer
    greet-user-options-member-name=nutzer
    greet-user-options-member-description=Der Nutzer der gegrüßt werden soll
    ```

See the [JDA Docs](https://github.com/discord-jda/JDA/blob/master/src/examples/java/LocalizationExample.java) for more details about the localization keys and locales.
(We use <DiscordLocale#toLocale()> to convert the locales).
