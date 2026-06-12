package io.github.kaktushose.jdac.dispatching;

import io.github.kaktushose.jdac.definitions.interactions.internal.Base64Utils;
import net.dv8tion.jda.api.JDA;
import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import de.mkammerer.snowflakeid.options.Options;
import de.mkammerer.snowflakeid.structure.Structure;
import de.mkammerer.snowflakeid.time.MonotonicTimeSource;
import de.mkammerer.snowflakeid.time.TimeSource;

import java.time.Instant;

class RuntimeIdGenerator {
    private static final TimeSource EPOCH = new MonotonicTimeSource(Instant.parse("2026-04-15T18:23:05.000Z"));

    private final SnowflakeIdGenerator generator;

    public RuntimeIdGenerator(JDA.ShardInfo shardInfo) {
        this.generator = SnowflakeIdGenerator.createCustom(shardInfo.getShardId(), EPOCH, Structure.createDefault(), Options.createDefault());
    }

    public String next() {
        return Base64Utils.encodeLong(generator.next());
    }


}
