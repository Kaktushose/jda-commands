package com.github.kaktushose.jda.commands.permissions;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public interface PermissionsProvider {

    boolean hasPermission(User user, CommandContext context);

    boolean hasPermission(Member member, CommandContext context);

}
