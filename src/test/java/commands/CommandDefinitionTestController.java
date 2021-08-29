package commands;

import com.github.kaktushose.jda.commands.entities.CommandEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class CommandDefinitionTestController {

    public void noArgs() {
    }

    public void noCommandEvent(Object argument) {
    }

    public void arrayArgument(CommandEvent event, String[] args) {

    }

    public void argsAfterArray(CommandEvent event, String[] args, Object argument) {

    }

    public void supportedPrimitiveTypes(CommandEvent event, boolean bool, byte b, char c, double d, float f, int i, long l, short s) {

    }

    public void supportedWrapperTypes(CommandEvent event, Boolean bool, Byte b, Character c, Double d, Float f, Integer i, Long l, Short s, String string) {

    }

    public void supportedJDATypes(CommandEvent event, Member member, User user, Role role, TextChannel textChannel) {

    }

    public void unsupportedType(CommandEvent event, UnsupportedType argument) {

    }

}
