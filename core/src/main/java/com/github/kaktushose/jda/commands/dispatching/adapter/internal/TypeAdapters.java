package com.github.kaktushose.jda.commands.dispatching.adapter.internal;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import io.github.kaktushose.proteus.Proteus;
import io.github.kaktushose.proteus.mapping.Mapper;
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import static io.github.kaktushose.proteus.mapping.Mapper.uni;
import static io.github.kaktushose.proteus.mapping.MappingResult.failure;
import static io.github.kaktushose.proteus.mapping.MappingResult.lossless;

/// Central registry for all type adapters.
///
/// @see TypeAdapter
@ApiStatus.Internal
public class TypeAdapters {

    private static final Type<String> STRING = Type.of(String.class);
    private static final Type<User> USER = Type.of(User.class);
    private static final Type<Member> MEMBER = Type.of(Member.class);
    private static final Type<GuildChannelUnion> CHANNEL = Type.of(GuildChannelUnion.class);
    private static final Type<Role> ROLE = Type.of(Role.class);
    private static final Type<IMentionable> MENTIONABLE = Type.of(IMentionable.class);
    private static final Type<Double> NUMBER = Type.of(Double.class);
    private final Proteus proteus;

    /// Constructs a new TypeAdapters.
    @SuppressWarnings("unchecked")
    public TypeAdapters(@NotNull Map<Entry<Type<?>, Type<?>>, TypeAdapter<?, ?>> typeAdapters) {
        proteus = Proteus.global();

        proteus.from(NUMBER).into(STRING, uni((source, _) -> lossless(String.valueOf(source))));

        proteus.from(MEMBER).into(USER, uni((source, _) -> lossless(source.getUser())));
        proteus.from(USER).into(MEMBER, uni((_, _ ) -> failure("A valid member is required")));

        proteus.into(MENTIONABLE)
                .from(USER, uni((source, _) -> lossless(source)))
                .from(MEMBER, uni((source, _) -> lossless(source)))
                .from(ROLE, uni((source, _) -> lossless(source)));

        proteus.from(CHANNEL)
                .into(Type.of(GuildChannel.class), uni((source, _) -> lossless(source)))
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
}
