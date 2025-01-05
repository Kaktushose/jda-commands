import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Set;

public class PremiumGuildsProvider implements GuildScopeProvider {

    @Override
    public Set<Long> apply(CommandData commandData) {
        if (commandData.getName().equals("paid feature")) {
            // this is the place where you could also perform a database lookup
            return Set.of(1234567890L);
        }
        return Set.of();
    }
}