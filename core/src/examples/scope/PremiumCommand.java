import io.github.kaktushose.jdac.annotations.interactions.CommandScope;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.annotations.interactions.SlashCommand;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;

@Interaction
public class PremiumCommand {

    @SlashCommand(value = "paid feature", scope = CommandScope.GUILD)
    public void onCommand(CommandEvent event) {
        event.reply("Hello World!");
    }

}