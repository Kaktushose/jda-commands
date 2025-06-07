package com.github.kaktushose.jda.commands.dispatching.adapter.internal;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import io.github.kaktushose.proteus.Proteus;
import io.github.kaktushose.proteus.mapping.Mapper;
import io.github.kaktushose.proteus.type.Format;
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import static io.github.kaktushose.proteus.mapping.Mapper.bi;
import static io.github.kaktushose.proteus.mapping.Mapper.uni;
import static io.github.kaktushose.proteus.mapping.MappingResult.*;

/// Central registry for all type adapters.
///
/// @see TypeAdapter
@ApiStatus.Internal
public class TypeAdapters {

    private static final Type<String> STRING = Type.of(new TypeFormat(OptionType.STRING), String.class);
    private static final Type<Long> INTEGER = Type.of(new TypeFormat(OptionType.INTEGER), Long.class);
    private static final Type<Boolean> BOOLEAN = Type.of(new TypeFormat(OptionType.BOOLEAN), Boolean.class);
    private static final Type<User> USER = Type.of(new TypeFormat(OptionType.USER), User.class);
    private static final Type<Member> MEMBER = Type.of(new TypeFormat(OptionType.USER), Member.class);
    private static final Type<GuildChannelUnion> CHANNEL = Type.of(new TypeFormat(OptionType.CHANNEL), GuildChannelUnion.class);
    private static final Type<Role> ROLE = Type.of(new TypeFormat(OptionType.ROLE), Role.class);
    private static final Type<IMentionable> MENTIONABLE = Type.of(new TypeFormat(OptionType.MENTIONABLE), IMentionable.class);
    private static final Type<Double> NUMBER = Type.of(new TypeFormat(OptionType.NUMBER), Double.class);
    private static final Type<Message.Attachment> ATTACHMENT = Type.of(new TypeFormat(OptionType.ATTACHMENT), Message.Attachment.class);
    private final Proteus proteus;

    /// Constructs a new TypeAdapters.
    @SuppressWarnings("unchecked")
    public TypeAdapters(@NotNull Map<Entry<Type<?>, Type<?>>, TypeAdapter<?, ?>> typeAdapters) {
        proteus = Proteus.global();
        proteus.from(NUMBER).into(INTEGER, bi(
                (source, _) -> {
                    if (source > Long.MAX_VALUE || source < Long.MIN_VALUE) {
                        return failure("Double value out of bounds");
                    }
                    return lossy((source.longValue()));
                },
                (source, _) -> lossless(source.doubleValue())
        ));
        proteus.from(NUMBER).into(STRING, uni((source, _) -> lossless(String.valueOf(source))));

        proteus.from(MEMBER).into(USER, uni((source, _) -> lossless(source.getUser())));

        proteus.into(MENTIONABLE)
                .from(USER, uni((source, _) -> lossless(source)))
                .from(MEMBER, uni((source, _) -> lossless(source)))
                .from(ROLE, uni((source, _) -> lossless(source)));

        proteus.from(CHANNEL)
                .into(Type.of(AudioChannel.class), channel(GuildChannelUnion::asAudioChannel))
                .into(Type.of(GuildMessageChannel.class), channel(GuildChannelUnion::asGuildMessageChannel))
                .into(Type.of(NewsChannel.class), channel(GuildChannelUnion::asNewsChannel))
                .into(Type.of(StageChannel.class), channel(GuildChannelUnion::asStageChannel))
                .into(Type.of(TextChannel.class), channel(GuildChannelUnion::asTextChannel))
                .into(Type.of(ThreadChannel.class), channel(GuildChannelUnion::asThreadChannel))
                .into(Type.of(VoiceChannel.class), channel(GuildChannelUnion::asVoiceChannel));

        typeAdapters.forEach(((entry, adapter) ->
                proteus.register((Type<Object>) entry.getKey(), (Type<Object>) entry.getValue(), (Mapper.UniMapper<Object, Object>) adapter))
        );
    }

    private <T extends GuildChannel> Mapper.UniMapper<GuildChannelUnion, T> channel(Function<GuildChannelUnion, T> function) {
        return uni((source, _) -> {
            try {
                return lossless(function.apply(source));
            } catch (IllegalArgumentException e) {
                return failure(e.getMessage());
            }
        });
    }

    /// Checks if a path for the given types exists.
    ///
    /// @param source the source type to check
    /// @param target the target type to check
    /// @return `true` if a type adapter exists
    public boolean exists(@NotNull Type<?> source, @NotNull Type<?> target) {
        return proteus.existsPath(source, target);
    }

    /// Retrieves a type adapter.
    ///
    /// @param type the type to get the adapter for
    /// @return the type adapter or an empty Optional if none found
    public Optional<TypeAdapter<?, ?>> get(@Nullable Class<?> type) {
        return Optional.empty();
    }

    public record TypeFormat(OptionType type) implements Format {

        @Override
        public boolean equals(Format other) {
            if (other instanceof TypeFormat(OptionType optionType)) {
                return type.equals(optionType);
            }
            return false;
        }
    }

}
