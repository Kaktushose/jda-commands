import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.concurrent.TimeUnit;

@Interaction
@ReplyConfig(keepComponents = false)
public class BanCommand {

    private int delDays;
    private Member target;
    private String reason;

    @ContextCommand(value = "Ban this member", type = Command.Type.USER, enabledFor = Permission.BAN_MEMBERS, isGuildOnly = true)
    public void onContextBanMember(CommandEvent event, User target) {
        this.delDays = 0;
        // we can use safely use complete here because this event has its own virtual thread
        this.target = event.getGuild().retrieveMemberById(target.getId()).complete();
        event.replyModal("onReason");
    }

    @SlashCommand(value = "ban", desc = "Bans a Member", enabledFor = Permission.BAN_MEMBERS, isGuildOnly = true)
    public void onSlashBanMember(CommandEvent event,
                            @Param("The member to ban") Member target,
                            @Param("The history of messages that will be deleted", optional = true, fallback = "0") int delDays) {
        this.delDays = delDays;
        this.target = target;
        event.replyModal("onReason");
    }

    @Modal("Ban Reason")
    public void onReason(ModalEvent event, @TextInput("Please provide a reason") String reason) {
        this.reason = reason;
        event.with().components("onConfirm", "onCancel").reply("Are you sure that you want to ban %s for reason %s?", target.getAsMention(), reason);
    }

    @Button(value = "Yes", style = ButtonStyle.SUCCESS)
    public void onConfirm(ComponentEvent event) {
        target.ban(delDays, TimeUnit.DAYS).reason(reason).queue();
        event.reply("Banned %s", target.getAsMention());
    }

    @Button(value = "No", style = ButtonStyle.DANGER)
    public void onCancel(ComponentEvent event) {
        event.reply("Banning member %s cancelled", target.getAsMention());
    }

}

