package testing;

import io.github.kaktushose.jdac.entities.JDACommands;
import io.github.kaktushose.jdac.entities.JDACommandsBuilder;
import net.dv8tion.jda.api.JDA;

public class Main {

    public static void main(String[] args) {
        JDA jda = yourJDABuilding();
        JDACommands jdaCommands = JDACommands.start(jda);
    }

}
