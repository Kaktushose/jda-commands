import io.github.kaktushose.jdac.annotations.interactions.Button;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.annotations.interactions.SlashCommand;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.dispatching.reply.Component;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

@Interaction
public class CookieClicker {

    private int count;

    @SlashCommand(value = "cookie clicker", desc = "Play cookie clicker")
    public void onClicker(CommandEvent event) {
        event.with().components("onCookie", "onReset").reply("You've got %s cookie(s)!", count);
    }

    @Button(value = "Collect", emoji = "🍪", style = ButtonStyle.SUCCESS)
    public void onCookie(ComponentEvent event) {
        count++;
        event.reply("You've got %s cookie(s)!", count);
    }

    @Button(value = "Reset", emoji = "🔄", style = ButtonStyle.DANGER)
    public void onReset(ComponentEvent event) {
        count = 0;
        event.reply("You've got %s cookie(s)!", count);
    }

}

