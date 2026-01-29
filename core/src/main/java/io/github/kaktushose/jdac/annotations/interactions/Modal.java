package io.github.kaktushose.jdac.annotations.interactions;

import io.github.kaktushose.jdac.dispatching.events.ModalReplyableEvent;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Methods annotated with [Modal] will be registered as a modal at startup.
///
/// Therefore, the method must be declared inside a class that is annotated with [Interaction].
///
/// You only define the title via this annotation. The rest of the modal is built when calling
/// [ModalReplyableEvent#replyModal(String, ModalTopLevelComponent, Entry...)].
///
/// ## Example:
/// ```
/// @Modal("My Modal")
/// public void onModal(ModalEvent event) { ... }
///
/// @Command("/example")
/// public void onCommand(CommandEvent event) {
///     event.replyModal("onModal", TextDisplay.of("Hello World"));
/// }
/// ```
///
/// @see Interaction
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Modal {

    /// Gets the title of this modal.
    ///
    /// @return the title of the modal
    String value();

}
