# Terms
-project-name = JDA-Commands
-github = https://github.com/Kaktushose/jda-commands
-wiki = https://kaktushose.github.io/jda-commands/wiki

# Logging
multiple-autocomplete =
    Found multiple auto complete handler for parameter named "{ $name }" of slash command "/{ $command }":
        -> {possibleAutoCompletes}
    Every command option can only have one auto complete handler. Please exclude the unwanted ones to enable auto complete for this command option.

# Internal Errors
internal-error = Please report this error the the devs of { -project-name } at: { -github }/issues

jda-context-cast = Cannot cast context to either JDA oder ShardManager class.
invocation-not-permitted = The definition must not be invoked at this point.
no-interaction-found = No interaction found.
default-switch = Should never occur.
reply-failed = Cannot reply to { $event }.
command-input-mismatch = Command input doesn't match command options length.
proteus-error = A Proteus error occurred: { $message }.
invalid-option-type = Invalid option type: { $type }.
localization-json-error = Embed localization failed because of underlying JSON error.
wrong-labels = Failed to add child command: { $command }. { $labelCount ->
        [0] Cannot add child with empty labels.
        *[other] Cannot add a child with more than { $labelCount } labels.
    }
subcommand-with-children = Cannot transform node with children to SubcommandData.

# Invalid Declaration
blank-name = Command name must be not blank.
command-name-length = Invalid command name "{ $name }" for slash command "{ $method }". Slash commands can only have up to 3 labels.
invalid-option-data = { $type } is no valid option data type. { $guessedType ->
        [other] Perhaps you wanted to write { $guessedType }?
        *[None]
    }
no-validator-found = No Validator implementation found for annotation "{ $annotation }" used at parameter "{ $ parameter }".
wildcard-optional = Generic parameter of Optional cannot be parsed to class. Please provide a valid generic type and don't use any wildcard.
unknown-command-type = Unknown command type isn't allowed here.
invalid-context-command-type = Invalid command type for context command! Must either be USER or MESSAGE
modal-parameter-count = Invalid amount of parameters. { $ count ->
        [0] Modals need at least one TextInput.
        *[other] Modals only support up to 5 TextInputs.
    }
invalid-parameter = { $index ->
        [one] First
        [two] Second
        [few] Third
        *[other] { $index}th
    } parameter must be of type "{ $type }".
incorrect-method-signature =
    { $prefix }Invalid method signature.
        Expected: { $expected }
        Actual: { $actual }
