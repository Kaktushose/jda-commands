package io.github.kaktushose.jdac.annotations;

import java.lang.annotation.*;

/// Marks a public API element as unstable.
///
/// Elements annotated with [UnstableApi] are part of the public API because they are required for advanced use cases
/// or framework integration. However, they are **not** covered by JDA-Commands' semantic versioning compatibility
/// guarantees.
///
/// An unstable API may change, be redesigned, or be removed in a minor release, without a major version bump or further
/// notice. This allows the API to evolve alongside changes in Discord and JDA.
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface UnstableApi { }
