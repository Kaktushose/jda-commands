# Placeholders

## Format
The overall placeholder format is `{ $your_placeholder }`, with following properties:

- the leading `$` is optional
- whitespace, newlines, `{` and `$` are forbidden inside the reference name
- trailing and leading whitespace or newline of the reference name is trimmed (see <String#trim()>).
- to escape the `{` character just prefix it with `\\` (backslashes can be used _unescaped_ in the rest of the string)

!!! example "Valid Placeholders"
    - `{ $your_placeholder }`
    - `{ your_placeholder }`
    - `{ \n your_placeholder \n}`


!!! example "Invalid Placeholders"
    
    - `{ white space }`
    - `{ dollar$sign }`
    - `{ bra{cket}` (will result in `{ bra}` as plain text and `{cket}` as placeholder)
    - `{ new \n line }` (meant is new line in the middle of the reference name)

Invalid placeholders will just be treated as literal text.

??? tip "Escaping the opening bracket"
    To escape the opening bracket `{` just prefix it with `\`, like `My escaped placeholder \{ this_is_text }`.

## Providing Values
JDA-Commands provides a way to set placeholder values by using the <io.github.kaktushose.jdac.message.placeholder.Entry>
class.

Often you will find a vararg of this class at the end of a method parameters list. By adding entries
there (preferably by using <io.github.kaktushose.jdac.message.placeholder.Entry#entry(String,Object)>
as a static import) it's possible for you to define placeholders for a given scope.

!!! example "Example Usage"
    ```java
    event.reply("Hello { $user }!", Entry.entry("user", event.getUser().getAsMention()));
    ```

!!! tip
    When using [Fluava](localization.md#default-implementation) (default) for localization, we can omit the call to
    <IMentionable#getAsMention()> because Fluava will automatically format any <IMentionable> for you.

## String Representation
To get a variables string representation, JDA-Commands will 

1. call <proteus -> Proteus#convert(S, Type, Type)>
trying to convert the value to [`Type.of(String.class)`][[proteus -> Type#of(Class)]]
2. if not successful, just call <Object#toString()>

If a variable couldn't be found, `null` will be inserted.