package com.github.kaktushose.jda.commands.annotations.interactions;

import com.github.kaktushose.jda.commands.dispatching.events.ModalReplyableEvent;
import com.github.kaktushose.jda.commands.i18n.I18n;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Methods annotated with [Modal] will be registered as a modal at startup.
///
/// Therefore, the method must be declared inside a class that is annotated with
/// [Interaction]. Text inputs are defined via method parameters that must be annotated with [TextInput].
///
/// You can reply with a modal by calling [ModalReplyableEvent#replyModal(String, I18n.Entry...)].
///
/// ## Example:
/// ```
/// @Modal("My Modal")
/// public void onModal(ModalEvent event, @TextInput("Type here") String input) { ... }
/// ```
/// @see Interaction
/// @see TextInput
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Modal {

    /// Gets the title of this modal.
    ///
    /// @return the title of the modal
    String value();

}
