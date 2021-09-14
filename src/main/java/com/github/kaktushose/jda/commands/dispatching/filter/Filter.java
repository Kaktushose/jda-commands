package com.github.kaktushose.jda.commands.dispatching.filter;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;

public interface Filter {

    void apply(CommandContext context);

}
