# Terms
-project-name = JDA-Commands
-github = https://github.com/Kaktushose/jda-commands
-wiki = https://kaktushose.github.io/jda-commands/wiki

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