package test;

import com.github.kaktushose.jda.commands.entities.JDACommands;
import com.github.kaktushose.jda.commands.entities.JDACommandsBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        JDA jda;
        try {
            jda = JDABuilder.createDefault("NTM2MjM4NjMwMjE4MzY2OTg2.XvnoNA.LMxPBNa21fWdApQpIKbMajR2V5M", Arrays.asList(GatewayIntent.values()))
                    .setStatus(OnlineStatus.ONLINE)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableCache(Arrays.asList(CacheFlag.values()))
                    .build()
                    .awaitReady();
        } catch (InterruptedException | LoginException e) {
            e.printStackTrace();
            return;
        }

/*
        CommandDispatcher commandDispatcher = new CommandDispatcher(
                jda,
                false,
                new CommandSettings("!", true, false),
                new EventParser(),
                new CommandMapper(),
                new ArgumentParser(),
                Collections.emptyList()
        );
        commandDispatcher.start();

        JDACommands jdaCommands = new JDACommandsBuilder(jda).addProvider(new DependencyProvider()).build();
*/

        JDACommands jdaCommands = JDACommandsBuilder.startDefault(jda);
        jdaCommands.getDefaultSettings().getMutedUsers().add(393843637437464588L);
        // jdaCommands.getSettings().getPermissionHolders("user.report").add(393843637437464588L);

    }

    public String getString() {
        return "Ehre alda";
    }

}
