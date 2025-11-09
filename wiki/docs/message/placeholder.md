# Placeholders
JDA-Commands supports resolving named placeholders/variables as explained [before](overview.md)

## Format
The placeholders format is similar to [project fluent](https://projectfluent.org/fluent/guide/) but with some
restrictions.

The overall placeholder format is `{ $your_placeholder }`, with following properties:

- the leading `$` is optional
- whitespace, newlines, `{` and `$` are forbidden inside the reference name
- trailing and leading whitespace or newline of the reference name is trimmed (see <String#trim()>).
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
    - `{ new \n line }` (meant is new line in the middle of the reference name)

    ```java
    event.reply("Hello { $user }!", Entry.entry("user", event.getUser().getAsMention()));
    ```

Invalid placeholders will just be treated as literal text.

### Escaping `{`
To escape the opening bracket `{` just prefix it with `\`, like `My escaped placeholder \{ this_is_text }`.

## Placeholders
JDA-Commands provides a way to set placeholder values by using <io.github.kaktushose.jdac.message.placeholder.Entry>.

Often you will find a vararg of this class at the end of a method parameters list. By adding entries
there (preferably by using <io.github.kaktushose.jdac.message.placeholder.Entry#entry(String,Object)>)
as a static import) it's possible for you to define placeholders for a given scope defined by the javadocs of
the used method.

### String Representation
To get a variables string representation, JDA-Commands will:

1. call <proteus -> Proteus#convert(S, Type, Type)>
trying to convert the value to [`Type.of(String.class)`][[proteus -> Type#of(Class)]]
2. if not successful, just call <Object#toString()>

If a variable couldn't be found, `null` will be inserted.