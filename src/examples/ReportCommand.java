package testing;

import com.github.kaktushose.jda.commands.annotations.*;
import com.github.kaktushose.jda.commands.entities.CommandEvent;
import net.dv8tion.jda.api.entities.Member;

@CommandController("report")
@Permission("user.report")
public class ReportCommand {

    @Command(name = "Create Report", usage = "{prefix}report @Member (reason)", desc = "Create a new report", category = "Moderation")
    public void onReportCreate(CommandEvent event, Member member, @Optional("no reason") @Concat String reason) {
        event.reply("%s got reported for reason %s", member.getAsMention(), reason);
    }

    @Command(value = "list",
            name = "Get Report", usage = "{prefix}report list @Member", desc = "Get the reports for a user", category = "Moderation")
    public void onReportGet(CommandEvent event, Member member) {
        event.reply("This member has no reports yet");
    }

    @Command(value = {"remove", "rm"},
            name = "Remove Report", usage = "{prefix}report remove @Member", desc = "Remove the reports of a user", category = "Moderation")
    public void onReportRemove(CommandEvent event, Member member) {
        event.reply("Cleared reports for member %s", member.getAsMention());
    }

}
