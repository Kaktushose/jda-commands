package com.github.kaktushose.jda.commands.plugins;

import java.util.List;

/**
 * Represents a plugin that can be loaded by the {@link com.github.kaktushose.jda.commands.reflect.CommandRegistry CommandRegistry} at Runtime.
 */
public interface CommandPlugin {

  /**
   * Returns the name of the plugin.
   * @return the name of the plugin
   */
  String getPluginName();

  /**
   * Returns the version of the plugin. Should be formatted like this: {@code major.minor.patch}. e.g. {@code 1.2.0}
   * @return the version of the plugin
   */
  String getVersion();

  /**
   * Returns the author of the plugin.
   * @return the author of the plugin
   */
  String getAuthor();

  /**
   * Returns a description of what the plugin is and does.
   * @return the plugin description
   */
  String getDescription();

  /**
   * Returns the plugin's website.
   * @return a website URL as a String
   */
  String getWebsite();

  /**
   * Returns a list of all packages that should be scanned for commands. This is used to load commands from the plugin.
   * Example return value: {@code List.of("com.github.kaktushose.plugins.example")}
   * @return a list of fully qualified package names
   */
  List<String> getCommandPackages();
}
