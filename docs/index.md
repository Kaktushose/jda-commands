# JDA-Commands

A declarative, annotation driven interaction framework for JDA. Our goal is to remove any boilerplate code, so 
you can focus solely on the business logic of your bot - writing bots has never been easier:

## Example
```java
@Interaction
public class CookieClicker {

    private int count;

    @SlashCommand(value = "cookie clicker", desc = "Play cookie clicker")
    public void onClicker(CommandEvent event) {
        event.with().components("onCookie").reply("You've got %s cookie(s)!", count);
    }

    @Button(value = "Collect", emoji = "🍪", style = ButtonStyle.SUCCESS)
    public void onCookie(ComponentEvent event) {
        event.reply("You've got %s cookie(s)!", ++count);
    }
}
```
## Dependency
=== "Maven"
      ```xml
      <dependency>
         <groupId>io.github.kaktushose</groupId>
         <artifactId>jda-commands</artifactId>
         <version>v4.0.0-beta.4</version>
      </dependency>
      ```
=== "Gradle (Kotlin DSL)"
      ```kotlin
      repositories {
         mavenCentral()
      }
      dependencies {
         implementation("io.github.kaktushose:jda-commands:v4.0.0-beta.4")
      }
      ```
=== "Gradle (Groovy DSL)"
      ```groovy
      repositories {
         mavenCentral()
      }
      dependencies {
         implementation 'io.github.kaktushose:jda-commands:v4.0.0-beta.4"'
      }
      ```

## Resources

You might also find the following resources helpful:

- [Javadocs](https://kaktushose.github.io/jda-commands/javadocs/latest/)
- [Release Notes](https://github.com/Kaktushose/jda-commands/releases)
- [JDA Wiki](https://jda.wiki/)

Having trouble or found a bug?

- Check out the [Examples](https://github.com/Kaktushose/jda-commands/tree/main/src/examples)
- Join the [Discord Server](https://discord.gg/JYWezvQ)
- Or open an [Issue](https://github.com/Kaktushose/jda-commands/issues)
