# Modals

## Overview
Modals are defined by annotating a method with <io.github.kaktushose.jdac.annotations.interactions.Modal>.
The first parameter must always be a <ModalEvent>.

```java
@Modal("My Modal")
public void onModal(ModalEvent event) { ... }
```

## Replying with Modals

You can reply to [`CommandEvents`][[CommandEvent]]
and [`ComponentEvents`][[io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent]]
with a Modal by calling <ModalReplyableEvent#replyModal(String, ModalTopLevelComponent, io.github.kaktushose.jdac.message.placeholder.Entry...)>
on the event.
!!! example
    ```java
    event.replyModal("onModal", TextDisplay.of("Hello World"));
    ```

### Foreign Modals
You can also reply with modals that were defined in a different class by calling 
<ModalReplyableEvent#replyModal(Class, String, ModalTopLevelComponent, io.github.kaktushose.jdac.message.placeholder.Entry...)>.
!!! example
    ```java
    event.replyModal(ModalHelpers.class, "onModal", TextDisplay.of("Hello World"));
    ```

## Modal Components
Modals consist of a title, which is set via the <io.github.kaktushose.jdac.annotations.interactions.Modal> annotation, 
and of up to 5 components. 

These components aren't defined via annotations, but are just passed over to the reply method.
!!! example
    ```java
    @Command("ban")
    public void onCommand(CommandEvent event, User target) {
        event.replyModal("onModal", TextDisplay.of("Do you really want to ban the user?")); //(1)!
    }

    @Modal("Confirm ban")
    public void onModal(ModalEvent event) { ... }
    ```

    1. We reference the Modal we want to send via the method name.

When using input components, such as a text input or select menu, you can access the <ModalMapping>s directly via the
<ModalEvent>.
!!! example
    ```java
    @Modal("User Select")
    public void onModal(ModalEvent event) {
        event.replyModal("onModal", Label.of(
            "Select a user",
            EntitySelectMenu.create("user-select", SelectTarget.USER).build()
        ));
    }

    @Modal("Example")
    public void onModal(ModalEvent event) { 
        var value = event.value("user-select");
    }
    ```

## Localization and Placeholders
To avoid hardcoded values, all string values can be replaced by a localization key as supported by the
current used [Localization System](../message/localization.md).

Furthermore, it's possible to directly use placeholders.
For more information on how to use placeholders please visit [this page](../message/placeholder.md).

Also take a look at the general [message resolution documentation](../message/overview.md).

!!! example "Key Example (with Fluava)"
    ```java    
    @Command("ban")
    public void onCommand(CommandEvent event, User target) {
        event.replyModal("onModal", TextDisplay.of("modal-text"));
    }

    @Modal("modal-title")
    public void onModal(ModalEvent event) { ... }
    ```

When using placeholders, you have to also pass the [Entries][[io.github.kaktushose.jdac.message.placeholder.Entry]] to
the reply method.
!!! example "Placeholder Example"
    ```java    
    @Command("ban")
    public void onCommand(CommandEvent event, User target) {
        event.replyModal("onModal", List.of(TextDisplay.of("modal-text")), entry("modal_name", target.getName()));
    }

    @Modal("Ban { $user_name }?")
    public void onModal(ModalEvent event) { ... }
    ```

## Unicode and application emojis
JDA-Commands has built in support for Unicode and application emoji aliases.
If you want to use them, just take a look [here](../message/emojis.md).

!!! example
    ```java
    @Modal("Example :thumbs_up:")
    public void onModal(ModalEvent event) { ... }
    ```