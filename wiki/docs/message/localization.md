# Localization
JDA-Commands provides the <I18n> class,
which is used as the main entrypoint for localization. This class can be used by users of the framework to localize their messages.

## Localization Messages
Localization messages are identified by their corresponding key. A key can be freely chosen but might be limited by
the restrictions of the used <Localizer> implementation.

If a certain message for a key isn't found, the key is returned as the messages value.

### The dollar ($) character
The dollar (`$`) is a reserved character for [bundle name separation](#bundles).

In practically all cases this doesn't really bother you, because there are only 2 niche situations where the dollar has to be escaped:
- your message key contains `$` and no bundle is explicitly stated, e.g. `key.with$.in.it` (the default bundle should be used here)
- the string is a "raw" message containing `$`,
  that happens to have it's prior `$` part to match a bundle name and its after `$` part to match a message key, e.g.
  - you have a bundle called `my_bundle`
  - you have a message key called `my-key` in that bundle
  - and you want to print the message `my_bundle$my-key` to the user (not the message stored under "my-key" in the bundle "my_bundle")

In these cases just prefix your whole message with a `$`, e.g. `$my_bundle$my-key` or `$key.with$.in.it`.
Now the bundle will be treated as not stated explicitly and the dollar sign will be preserved.

## Implicit Localization
Due to the [implicit resolution of messages](overview.md#implicit-resolution), localization is also done in many common places
automatically for you. Furthermore, the localization of Slash commands 
is supported trough JDAs [`LocalizationFunction`](#localizationfunction-jda-slash-command-localization).

JDA-Commands will first try to find a localization message based on the provided String (as the key) and the users locale
retrieved by <GenericInteractionCreateEvent#getUserLocale()>
and if not found, will use the String directly as the content.

!!! warning
    Localization of <MessageCreateData> is not supported implicitly.
    To localize such messages you have to manually use <I18n#localize(Locale,String,Entry...)`>.

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

## Variables/Placeholders
To learn more about placeholders take a look [here](placeholder.md)

### Example (Fluava)
```java
import static com.github.kaktushose.jda.commands.message.placeholder.Entry.entry;

@Interaction
public class ComponentTest {

    @Command("say hi")
    public void onCommand(CommandEvent event) {
        event.with()
                .components(Component.button("onButton", entry("name", event.getUser().getName())))
                .reply();
    }
    
    @Button("button_name")
    public void onButton(ComponentEvent event) {
        ...
    }

}
```

```properties title="default_en.ftl"
button_name = Hello { $name }
```

## Bundles
Localization bundles are a known concept from Javas <ResourceBundle>. JDA-Commands supports different bundles of
localization files by adding them to the localization key or using the <Bundle>
annotation.

!!! warning the dollar sign ($)
    Please note that the character `$` is forbidden in bundle names.

### Via Key
To state which bundle to use the direct way is to include it in the key following the format `bundle$key`.
For example a message with key `user$not-found` will be searched for in the bundle `user` and the key `not-found`.

### Via Annotation

If no bundle is specified, it will traverse the stack (the called methods) and search for the nearest
<Bundle>
annotation with following order:

1. Method that called <I18n#localize(Locale,String,Entry...)`>
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

    1. method `A$aOne()`
    2. method `A$aTwo()`
    3. class `A`
    4. `package-info.java` of package `my.app`
    5. method `B$bOne()`
    6. method `B$bTwo()`

    The found bundle would be `package_bundle`. If `I18n#localize(Locale, String, Entry...)`
    would be called in, for example, `B$bTwo` the bundle would be `method_bundle`.

### Default Bundle
If no bundle is found with the above techniques, a bundle called `default` will be used.

## LocalizationFunction (JDA) / slash command localization
JDA uses the <LocalizationFunction> for localizing slash commands.
We implement this interface based on our `I18n` class as described above.

If you want to disable slash commands localization just call [JDACBuilder#localizeCommands(false)][[JDACBuilder#localizeCommands(boolean)]].

See the [JDA Docs](https://github.com/discord-jda/JDA/blob/master/src/examples/java/LocalizationExample.java) for details.

## Default Implementation
By default, JDA-Commands supports localization with help of the [Fluava](https://github.com/Goldmensch/fluava) library, a [Project Fluent](https://projectfluent.org/) implementation for Java.
You can provide an own instance of the [Fluava](https://goldmensch.github.io/fluava/javadocs/snapshot/dev.goldmensch.fluava/dev/goldmensch/fluava/Fluava.html) class by calling
the appropriate builder method.

```java
Fluava myFluava = Fluava.create(Locale.ENGLISH);

JDACommands.builder(jda, Main.class)
    .localizer(new FluavaLocalizer(myFluava))
    .start();
```

!!! tip
    To set a fallback bundle you have to pass it to the constructor of the `Fluava` class. In the above example the fallback locale
    is German.

!!! note
    JDA-Commands will set [FluavaBuilder.FunctionConfig#fallbackToString(boolean)](https://goldmensch.github.io/fluava/javadocs/0/dev.goldmensch.fluava/dev/goldmensch/fluava/FluavaBuilder.FunctionConfig.html#fallbackToString(boolean))
    to `true` when using `FluavaLocalizer` thus always enabling falling back to `Object#toString()` if necessary.

### Localization Keys
Since [`Project Fluent`](https://projectfluent.org/) doesn't support dots (`.`) in localization keys, the [Fluava](https://github.com/Goldmensch/fluava) 
integration will change all dots to dashes (`-`). For example, `my.key` will become `my-get`. This change also effects 
all JDA Slash Command localization keys.

### Localization files
<fluava -> Fluava> supports the loading and discovery of bundles on the classpath 
(resource directory) similar to Javas <ResourceBundle>
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