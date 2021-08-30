package com.github.kaktushose.jda.commands.rewrite.dispatching.filter;

import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;

public interface Filter {

    void apply(CommandContext context);

}
