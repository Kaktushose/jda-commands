package reply;

import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;

@Interaction
@ReplyConfig(ephemeral = true, editReply = false, keepComponents = false)
public class CustomController {

    @Button
    public void defaultValues(ComponentEvent event) {

    }

    @Button
    @ReplyConfig(ephemeral = true, editReply = false, keepComponents = false)
    public void sameValues(ComponentEvent event) {

    }

    @Button
    @ReplyConfig
    public void customValues(ComponentEvent event) {

    }

}