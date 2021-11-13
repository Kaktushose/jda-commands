package adapting;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;

import java.util.Optional;

public class CustomTypeAdapter implements TypeAdapter<CustomType> {

    @Override
    public Optional<CustomType> parse(String raw, CommandContext context) {
        return Optional.of(new CustomType());
    }
}
