package testing;

import com.github.kaktushose.jda.commands.entities.JDACommands;
import com.github.kaktushose.jda.commands.entities.JDACommandsBuilder;
import net.dv8tion.jda.api.JDA;

public class Main {

    public static void main(String[] args) {
        JDA jda = yourJDABuilding();
        JDACommands jdaCommands = JDACommands.start(jda);
    }

}
