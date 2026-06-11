package io.github.kaktushose.jdac.definitions.interactions;

import io.github.kaktushose.jdac.definitions.Definition;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;

import java.nio.ByteBuffer;
import java.util.Base64;

/// # Custom ID
/// Representation of a custom id used in modals, buttons or select menus.
///
/// ## Format
///
/// Discord allows custom ids to have a maximum character length of 100. We utilize
/// this space by using a predefined format with explicit versioning.
///
/// ### The current version is `2`:
///
/// | Position  (inclusive, 0 indexed) 	| Description           	| `Value`/Type              	|
/// |----------------------------------	|-----------------------	|---------------------------	|
/// | 0-3                              	| Prefix                	| `jdac`                    	|
/// | 4-5                              	| Specification Version 	| 2 character decimal number    |
/// | 6-16                             	| Runtime ID            	| long as base64            	|
/// | 17-22                            	| Definition ID         	| int (hashcode) as base64  	|
/// | 23 - 37                           | Reserved for later use    | `000000000000000`             |
/// | 38 - 99                           | Payload                   | user specific                 |
///
/// If the component is runtime independent, then its runtime id is `0`.
///
/// ### Older formats
///
/// #### Version 1
///
/// | Position  (inclusive, 0 indexed) 	| Description   	| `Value`/Type             	|
/// |----------------------------------	|---------------	|--------------------------	|
/// | 0-3                              	| Prefix        	| `jdac`                   	|
/// | 4                                	| Separator     	| `.`                      	|
/// | 5-40                             	| Runtime ID    	| UUID                     	|
/// | 41                               	| Separator     	| `.`                      	|
/// | 42-52                            	| Definition ID 	| int (hashcode) as base10 	|
///
/// Version 1 is the only version including separators. It can be identified as version 1, if the `jdac` prefix is followed
/// by a separator (`.`).
///
/// Only custom ids with runtime id set to `independent` are supported for version 1.
///
///
/// @param runtimeId    the id of the [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) this custom id is bound to
///                     or the literal `independent`.
/// @param definitionId the [Definition#definitionId()]
/// @implNote the custom id has the following format: `jdac.runtimeId.definitionId`
///
public record CustomId(long runtimeId, String definitionId, String payload) {
    private static final String PREFIX = "jdac";
    public static final long INDEPENDENT_ID = 0;
    private static final int CURRENT_VERSION = 2;



    /// Constructs a new [CustomId] from the given String.
    ///
    /// @param id the custom id String
    /// @return the [CustomId]
    public static CustomId fromMerged(String id) {
        if (!(id.startsWith(PREFIX)) || (id.length() > 100 || id.length() < 38)) {
            throw new IllegalArgumentException(JDACException.errorMessage("invalid-custom-id"));
        }

        if (id.charAt(4) == '.') {
            return parseV1(id);
        }

        int version = Integer.parseUnsignedInt(id.substring(4, 6));
        return switch (version) {
            case 2 -> parseV2(id);
            default -> throw new RuntimeException("TODO: illegal version");
        };
    }

    private static CustomId parseV1(String id) {
        String[] split = id.split("\\.");

        if (!split[1].equals("independent")) {
            throw new RuntimeException("TODO: exception");
        }

        return new CustomId(INDEPENDENT_ID, split[2], "");
    }

    private static CustomId parseV2(String id) {
        long runtimeId = decodeLong(substring(id, 6, 16));
        int definitionId = decodeInt(substring(id, 17, 22));

        return new CustomId(runtimeId, String.valueOf(definitionId), "");
    }

    private static long decodeLong(String raw) {
        byte[] bytes = Base64.getUrlDecoder().decode(raw);
        return ByteBuffer.wrap(bytes).getLong();
    }

    private static String encodeLong(long raw) {
        byte[] data = ByteBuffer.allocate(8)
                .putLong(raw)
                .array();

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(data);
    }

    private static int decodeInt(String raw) {
        byte[] bytes = Base64.getUrlDecoder().decode(raw);
        return ByteBuffer.wrap(bytes).getInt();
    }

    private static String encodeInt(int raw) {
        byte[] data = ByteBuffer.allocate(4)
                .putInt(raw)
                .array();

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(data);
    }

    private static String substring(String id, int begin, int end) {
        return id.substring(begin, end + 1);
    }

    /// Constructs a new runtime-independent [CustomId] from the given definition id.
    ///
    /// @param definitionId the definition id to construct the [CustomId] from
    /// @return a new runtime-independent [CustomId]
    public static CustomId independent(String definitionId) {
        return new CustomId(INDEPENDENT_ID, definitionId, "");
    }

    public static boolean isValid(String id) {
        return id.startsWith(PREFIX);
    }

    /// The String representation of this custom id.
    public String merged() {
        return PREFIX + "%02d".formatted(CURRENT_VERSION) + encodeLong(runtimeId) + encodeInt(Integer.parseInt(definitionId)) + "0".repeat(15) + payload;
    }

    /// Gets the runtime id of this custom id.
    ///
    /// @return the runtime id
    /// @throws IllegalStateException if this custom id is runtime-independent
    public long runtimeId() {
        if (isIndependent()) {
            throw new IllegalStateException(JDACException.errorMessage("independent-runtime-id"));
        }
        return runtimeId;
    }

    /// Checks if the passed custom id is runtime-independent.
    ///
    /// @return `true` if the custom id is runtime-independent
    public boolean isIndependent() {
        return runtimeId == INDEPENDENT_ID;
    }

    /// Checks if the passed custom id is runtime-bound.
    ///
    /// @return `true` if the custom id is runtime-bound
    public boolean isBound() {
        return !isIndependent();
    }
}
