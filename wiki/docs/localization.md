# Localization
JDA-Commands provides the [`I18n`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/i18n/I18n.html) class,
which is used as the main entrypoint for localization. This class can be used by users of the framework to localize their messages.

## Localization Messages
Localization messages are identified by their corresponding key. A key can be freely chosen but might be limited by
the restrictions of the used [`Localizer`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/i18n/Localizer.html) implementation.

If a certain message for a key isn't found, the key is returned as the messages value.

## Implicit Localization
Instead of using the localization API manually through the `I18n` class, JDA-Commands allows for implicit usage of
localization keys in many common places. These include:

- Component API including the corresponding annotations like [`@Button`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/interactions/Button.html),
 [`@Modal`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/interactions/Modal.html) etc.
- Reply API, for example the string content of a message [`Reply#reply(String)`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/dispatching/reply/Reply.html#reply(java.lang.String))
- (Slash commands are supported trough JDAs [`LocalizationFunction`](#localizationfunction-jda))

JDA-Commands will first try to find a localization message based on the provided String (as the key) and the users locale
retrieved by [`GenericInteractionCreateData#getUserLocale()`](https://docs.jda.wiki/net/dv8tion/jda/api/events/interaction/GenericInteractionCreateEvent.html#getUserLocale())
and if not found, will use the String directly as the content.

!!! warning
    Localization of [`MessageCreateData`](https://docs.jda.wiki/net/dv8tion/jda/api/utils/messages/MessageCreateData.html) is not supported implicitly.
    To localize such messages you have to manually use [`I18n#localize(...)`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/i18n/I18n.html#localize(java.util.Locale,java.lang.String,com.github.kaktushose.jda.commands.i18n.I18n.Entry...)).

### Example
```java
@Bundle("component")
@Interaction
public class ComponentTest {

    @Command("say hi")
    public void onCommand(CommandEvent event) {
        event.reply("command-reply");
    }

}
``` 

In this example, the bundle `component` will be searched for the key `command-reply`.

## Directly Inserting Localization Messages
In some cases, especially while testing it's common to hardcode a messages content.
But often times you still want to use dynamic placeholders.

For your luck JDA-Commands treats hardcoded values (where a localization key would normally be used) as the content of 
a localization message, thus supporting all functionality of the used [`Localizer`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/i18n/Localizer.html) implementation.

A String value (whether in an annotation, embed, modal or component) will be resolved by following order:

1. searched for as a localization key
2. treated as localization message content
3. used as raw value

An example of this can be found [here](#example-fluava).


## Variables/Placeholders
Most localization systems support variables or placeholders to insert dynamic values into a message.

JDA-Commands provides this functionality in many places by using [`I18n.Entry`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/i18n/I18n.Entry.html).
Often you will find a vararg of this class at the end of a method parameters list. By adding entries
there (preferably by using [`I18n#entry`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/i18n/I18n.html#entry(java.lang.String,java.lang.Object))
as a static import) it's possible for you to define placeholders for a given scope defined by the javadocs of
the used method.

### Example (Fluava)
```java
import static com.github.kaktushose.jda.commands.i18n.I18n.entry;

@Interaction
public class ComponentTest {

    @Command("say hi")
    public void onCommand(CommandEvent event) {
        event.with()
                .components(Component.button("onButton", entry("name", event.getUser().getName())))
                .reply();
    }
    
    @Button("Hello { $name }")
    public void onButton(ComponentEvent event) {
        ...
    }

}
```

## Bundles
Localization bundles are a known concept from Javas [ResourceBundles](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/util/ResourceBundle.html). JDA-Commands supports different bundles of
localization files by adding them to the localization key or using the [`@Bundle("bundle_name")`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/i18n/Bundle.html)
annotation.

### Via Key
To state which bundle to use the direct way is to include it in the key following the format `bundle#key`.
For example a message with key `user#not-found` will be searched for in the bundle `user` and the key `not-found`.

### Via Annotation

If no bundle is specified, it will traverse the stack (the called methods) and search for the nearest
[`@Bundle`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/i18n/Bundle.html)
annotation with following order:

1. Method that called [`I18n#localize(...)`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/i18n/I18n.html#localize(java.util.Locale,java.lang.String,com.github.kaktushose.jda.commands.i18n.I18n.Entry...))
2. Other called methods in the same class
3. This methods class
4. The class' packages `package-info.java` file

If no annotation is found, the previous method (in another class) is searched with the same pattern up to the
class at the very beginning.

??? example "Detailed Example"
    ```java title="A.java"
    package my.app;

    class A {
        void aOne() {
            i18n.localize(Locale.GERMAN, "fail", Map.of());
        }
    
        void aTwo() {
            aOne();
        }
    }
    ```
    ```java title="B.java"
    package my.app.other;
    
    @Bundle("class_bundle")
    class B {
        A another = new A();

        void bOne() {
            a.aOne();
        }

        @Bundle("method_bundle")
        void bTwo() {
            bOne();
        }
    }
    ```
    ```java title="package-info.java"
    @Bundle("package_bundle")
    package my.app;
    ```

    The order in which the bundle name is searched for is following:

    1. method `A#aOne()`
    2. method `A#aTwo()`
    3. class `A`
    4. `package-info.java` of package `my.app`
    5. method `B#bOne()`
    6. method `B#bTwo()`

    The found bundle would be `package_bundle`. If `I18n#localize(Locale, String, I18n.Entry...)`
    would be called in, for example, `B#bTwo` the bundle would be `method_bundle`.

### Default Bundle
If no bundle is found with the above techniques, a bundle called `default` will be used.

## LocalizationFunction (JDA) / slash command localization
JDA uses the [`LocalizationFunction`](https://docs.jda.wiki/net/dv8tion/jda/api/interactions/commands/localization/ResourceBundleLocalizationFunction.html) for localizing slash commands.
We implement this interface based on our `I18n` class as described above.

If you want to disable slash commands localization just call [`JDACBuilder#localizeCommands(false)`](https://kaktushose.github.io/jda-commands/javadocs/4/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/JDACBuilder#localizeCommands(boolean)).

See the [JDA Docs](https://github.com/discord-jda/JDA/blob/master/src/examples/java/LocalizationExample.java) for details.

## Default Implementation
By default, JDA-Commands supports localization with help of the [Fluava](https://github.com/Goldmensch/fluava) library, a [Project Fluent](https://projectfluent.org/) implementation for Java.
You can provide an own instance of the [`Fluava`](https://goldmensch.github.io/fluava/javadocs/0/dev.goldmensch.fluava/dev/goldmensch/fluava/Fluava.html) class by calling
the appropriate builder method.

```java
    Fluava myFluava = new Fluava(Locale.GERMAN);

    JDACommands.builder(jda, Main.class)
       .localizer(new FluavaLocalizer(myFluava))
       .start();
```

!!! tip
    To set a fallback bundle you have to pass it to the constructor of the `Fluava` class. In the above example the fallback locale
    is German.

### Localization Keys
Since [`Project Fluent`](https://projectfluent.org/) doesn't support dots (`.`) in localization keys, the [Fluava](https://github.com/Goldmensch/fluava) 
integration will change all dots to dashes (`-`). For example, `my.key` will become `my-get`. This change also effects 
all JDA Slash Command localization keys.

### Localization files
[Fluava](https://github.com/Goldmensch/fluava) supports the loading and discovery of bundles on the classpath 
(resource directory) similar to Javas [`ResourceBundle`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/util/ResourceBundle.html)
but with a slightly more flexible structure.

The classpath will be lazily searched for a fluent file given a specific locale with the following order:

1. `BASE_LANGUAGE_COUNTRY_VARIANT.ftl`
2. `BASE/LANGUAGE_COUNTRY_VARIANT.ftl`
3. `BASE_LANGUAGE_COUNTRY.ftl`
4. `BASE/LANGUAGE_COUNTRY.ftl`
5. `BASE_LANGUAGE.ftl`
6. `BASE/LANGUAGE.ftl`

If a key isn't found in any of the above files, the same procedure will be done for the given "fallback" locale.

A resource folder structure could for example look like this:

```
src/
├─ main/
│  ├─ resources/
│  │  ├─ component/
│  │  │  ├─ de.ftl
│  │  │  ├─ en.ftl
│  │  ├─ default_de.ftl
│  │  ├─ default_en.ftl
```

Such a structure has the two bundles `component` and `default` and a locale specific file for German and English for each bundle.