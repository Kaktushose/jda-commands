package com.github.kaktushose.jda.commands.plugins;

import org.pf4j.ExtensionPoint;

import java.util.List;

public interface CommandPlugin extends ExtensionPoint {
  String getPluginName();
  String getVersion();
  String getAuthor();
  String getDescription();
  String getWebsite();
  List<String> getCommandPackages();
}
