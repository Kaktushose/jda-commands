package com.github.kaktushose.jda.commands.dispatching.middleware;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;

public interface Middleware {

    void execute(Context context);

}
