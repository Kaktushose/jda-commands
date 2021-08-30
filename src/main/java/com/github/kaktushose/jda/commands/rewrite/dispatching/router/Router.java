package com.github.kaktushose.jda.commands.rewrite.dispatching.router;

import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;

public interface Router {

    void apply(CommandContext context);

}
