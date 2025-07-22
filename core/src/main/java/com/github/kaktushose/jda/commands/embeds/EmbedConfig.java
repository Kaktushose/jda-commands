package com.github.kaktushose.jda.commands.embeds;

import com.github.kaktushose.jda.commands.embeds.error.DefaultErrorMessageFactory;
import com.github.kaktushose.jda.commands.i18n.I18n;
import com.github.kaktushose.jda.commands.i18n.Localizer;
import dev.goldmensch.fluava.Fluava;

import java.nio.file.Path;
import java.util.Map;

/// Builder for configuring the Embed API of JDA-Commands.
///
/// # Embed Sources
/// Use [#sources(EmbedDataSource)] to add an [EmbedDataSource] that [Embed]s can be loaded from. You can have
/// multiple [EmbedDataSource]s.
///
/// Use [EmbedDataSource#file(Path)] to load embeds from a JSON file. The file must contain a single JSON object
/// that contains all embeds that can be loaded as child objects. Every embed must have a unique name, the embed
/// object must follow the [Discord API format](https://discord.com/developers/docs/resources/message#embed-object).
/// ## Example
/// ```json
/// {
///    "example": {
///       "title": "Greetings",
///       "description": "Hello World!"
///    }
/// }
///```
/// # Localization
/// The Embed API supports localization via the [I18n] class. The embed fields can either contain a localization key
/// a direct localization message in a format supported by the [Localizer] implementation.
///
/// For the default [Localizer] implementation, which uses [Fluava], this could look like this:
/// ```json
/// {
///    "example": {
///       "title": "example-title", // localization key
///       "description": "Hello {$user}!" // localization message
///    }
///}
///```
///
/// # Global Placeholders
/// Use [#placeholders(I18n.Entry...)] to define placeholders that will be globally available for any [Embed].
public interface EmbedConfig {

    /// Adds one or more new global placeholders. Global placeholders will be available for any [Embed] loaded by this API.
    ///
    /// @param placeholders the [`entries`][I18n.Entry] to add
    /// @return this instance for fluent interface
    EmbedConfig placeholders(I18n.Entry... placeholders);

    /// Adds global placeholders with the values of the given map, where the key of the map represents
    /// the name of the placeholder and the value of the map the value to replace the placeholder with.
    ///
    /// Global placeholders will be available for any [Embed] loaded by this API.
    ///
    /// @param placeholders the [Map] to get the values from
    /// @return this instance for fluent interface
    EmbedConfig placeholders(Map<String, Object> placeholders);

    /// Adds a new [EmbedDataSource] that [Embed]s can be loaded from.
    ///
    /// @param source the [EmbedDataSource] to add
    /// @return this instance for fluent interface
    EmbedConfig sources(EmbedDataSource... source);

    /// Adds a new [EmbedDataSource] that will be used by the [DefaultErrorMessageFactory] to load the error messages
    /// from.
    ///
    /// @param source the [EmbedDataSource] to load the error embeds from
    /// @return this instance for fluent interface
    EmbedConfig errorSource(EmbedDataSource source);
}
