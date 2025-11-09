package io.github.kaktushose.jdac.dispatching.adapter.internal;

import io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.proteus.Proteus;
import io.github.kaktushose.proteus.mapping.Mapper;
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.getUserLocale;
import static io.github.kaktushose.jdac.dispatching.context.internal.RichInvocationContext.isSet;
import static io.github.kaktushose.proteus.ProteusBuilder.ConflictStrategy.IGNORE;
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
    private static final Type<Long> INTEGER = Type.of(Long.class);
    private static final Type<Double> NUMBER = Type.of(Double.class);
    private final Proteus proteus;

    /// Constructs a new TypeAdapters.
    @SuppressWarnings("unchecked")
    public TypeAdapters(Map<Entry<Type<?>, Type<?>>, TypeAdapter<?, ?>> typeAdapters, I18n i18n) {
        proteus = Proteus.global();

        proteus.from(INTEGER).into(STRING, uni((source, _) -> lossless(String.valueOf(source))), IGNORE);
        proteus.from(NUMBER).into(STRING, uni((source, _) -> lossless(String.valueOf(source))), IGNORE);

        proteus.from(MEMBER).into(USER, uni((source, _) -> lossless(source.getUser())), IGNORE);
        proteus.from(USER).into(MEMBER, uni((_, _ ) -> failure(i18n.localize(isSet() ? getUserLocale() : Locale.ENGLISH, "jdac$member-required-got-user"))), IGNORE);

        proteus.into(MENTIONABLE)
                .from(USER, uni((source, _) -> lossless(source)), IGNORE)
                .from(MEMBER, uni((source, _) -> lossless(source)), IGNORE)
                .from(ROLE, uni((source, _) -> lossless(source)), IGNORE);

        proteus.from(CHANNEL)
                .into(Type.of(GuildChannel.class), uni((source, _) -> lossless(source)), IGNORE)
                .into(Type.of(AudioChannel.class), channel(GuildChannelUnion::asAudioChannel), IGNORE)
                .into(Type.of(GuildMessageChannel.class), channel(GuildChannelUnion::asGuildMessageChannel), IGNORE)
                .into(Type.of(NewsChannel.class), channel(GuildChannelUnion::asNewsChannel), IGNORE)
                .into(Type.of(StageChannel.class), channel(GuildChannelUnion::asStageChannel), IGNORE)
                .into(Type.of(TextChannel.class), channel(GuildChannelUnion::asTextChannel), IGNORE)
                .into(Type.of(ThreadChannel.class), channel(GuildChannelUnion::asThreadChannel), IGNORE)
                .into(Type.of(VoiceChannel.class), channel(GuildChannelUnion::asVoiceChannel), IGNORE);

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
