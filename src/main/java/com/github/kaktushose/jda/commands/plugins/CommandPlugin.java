package com.github.kaktushose.jda.commands.plugins;

import java.util.List;

public interface CommandPlugin {
  String getPluginName();
  String getVersion();
  String getAuthor();
  String getDescription();
  String getWebsite();
  List<String> getCommandPackages();
}
