# Placeholders
JDA-Commands supports resolving named placeholders/variables in all places where [localization](../localization.md#implicit-localization) is done implicitly.

This is done prior to [localization](../localization.md).

## Format
The placeholders format is similar to [project fluent](https://projectfluent.org/fluent/guide/) but with some
restrictions.

The overall placeholder format is `{ $your_placeholder }`, with following properties:

- the leading `$` is optional
- whitespace, newlines, `{` and `$` are forbidden inside the reference name
- trailing and leading whitespace or newline of the reference name is trimmed (see [String#trim()](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/lang/String.html#trim())).
- to escape the `{` character just prefix it with `\\` (backslashes can be used _unescaped_ in the rest of the string)

!!! examples
    valid placeholders:

    - `{ $your_placeholder }`
    - `{ your_placeholder }`
    - `{ \n your_placeholder \n}`

    Invalid placeholders:
    
    - `{ white space }`
    - `{ dollar$sign }`
    - `{ bra{cket}` (will result in `{ bra}` as plain text and `{cket}` as placeholder)
    - `{ new \n line }`

Invalid placeholders will just be treated as literal text.

### Escaping `{`
To escape the opening bracket `{` just prefix it with `\`, like `My escaped placeholder \{ this_is_text }`.

## Placeholders
TBD

### string representation
To get a variables string representation, JDA-Commands will:

1. call [Proteus#convert(Object, Type, Type)](https://kaktushose.github.io/proteus/javadocs/0/io.github.kaktushose.proteus/io/github/kaktushose/proteus/Proteus.html#convert(S,io.github.kaktushose.proteus.type.Type,io.github.kaktushose.proteus.type.Type))
trying to convert the values to [`Type.of(String.class)`](https://kaktushose.github.io/proteus/javadocs/0/io.github.kaktushose.proteus/io/github/kaktushose/proteus/type/Type.html#of(java.lang.Class))
2. if not successful, just calls [Object#toString()](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/lang/Object.html#toString())

If a variable couldn't be found, `null` will be inserted.