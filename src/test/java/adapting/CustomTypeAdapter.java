package adapting;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CustomTypeAdapter implements TypeAdapter<CustomType> {


    @Override
    public Optional<CustomType> apply(@NotNull String raw, @NotNull GenericInteractionCreateEvent event) {
        return Optional.of(new CustomType());
    }
}
