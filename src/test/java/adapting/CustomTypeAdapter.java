package adapting;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CustomTypeAdapter implements TypeAdapter<CustomType> {

    @Override
    public Optional<CustomType> parse(@NotNull String raw, @NotNull CommandContext context) {
        return Optional.of(new CustomType());
    }
}
