# Validators
Command Options of a Slash Command can have constraints. You can add constraints by annotating the method parameter
with the respective annotation. 

## Default Validators
JDA-Commands comes with the following default constraints:

- [`@Min`](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/constraints/Min.html):
  The number must be greater or equal to the specified minimum.
- [`@Max`](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/constraints/Max.html):
  The number must be less or equal to the specified maximum.
- [`@Role`](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/constraints/Role.html):
  The member must have the specified guild role. 
- [`@NotRole`](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/constraints/NotRole.html):
  The member must **not** have the specified guild role. 
- [`@User`](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/constraints/User.html):
  Must be the specified user or member
- [`@NotUser`](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/constraints/NotUser.html):
  Must **not** be the specified user or member.
- [`@Perm`](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/constraints/Perm.html):
  The user or member that have the specified discord permission.
- [`@NotPerm`](https://kaktushose.github.io/jda-commands/javadocs/development/io.github.kaktushose.jda.commands.core/com/github/kaktushose/jda/commands/annotations/constraints/NotPerm.html):
  The user or member that **doesn't** have the specified discord permission.

!!! example
    ```java
    @SlashCommand("ban")
    public void onBan(CommandEvent event, @NotRole("adminRoleId") Member target) {...}
    ```

## Writing own Validators

### 1. Creating the Annotation
Your annotation must meet the following conditions:

- [x] `@Target` must be `ElementType.PARAMETER`
- [x] `RetentionPolicy` must be `RUNTIME`
- [x] Must be annotated with [`@Constraint`](https://kaktushose.github.io/jda-commands/javadocs/latest/jda.commands/com/github/kaktushose/jda/commands/annotations/constraints/Constraint.html)
defining the valid types for this annotation. 
- [x] Must contain a `message()` field for the error message

!!! example
    ```java
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(String.class)
    public @interface MaxString {
        
        int value();

        String message() default "The given String is too long";
        
    }
    ```

### 2. Creating the Validator

### 3. Registration