# Terms
-project-name = JDA-Commands
-github = https://github.com/Kaktushose/jda-commands
-wiki = https://kaktushose.github.io/jda-commands/wiki

# Logging
multiple-autocomplete =
    Found multiple auto complete handler for parameter named "{ $name }" of slash command "/{ $command }":
        -> { $possibleAutoCompletes }
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
localization-json-error =
    Embed localization failed because of underlying JSON parsing error. This might be due to invalid user input. Raw JSON:
    { $rawJson }
wrong-labels = Failed to add child command: { $command }. { $labelCount ->
        [0] Cannot add child with empty labels.
        *[other] Cannot add a child with more than { $labelCount } labels.
    }
subcommand-with-children = Cannot transform node with children to SubcommandData.
null-member-context-command = Member for context command is null (not executed in a guild). This should not be possible.
emoji-not-loadable-from-resource = An error occoured while loading emoji called { $name } from resources at { $path }.
default-msg-not-in-bundle = "No messages could be found for key { $key }, searched in bundle 'jdac' and 'jdac_default'"

# Invalid Declaration
command-name-length = Invalid command name "{ $name }" for slash command "{ $method }". Slash commands can only have up to 3 labels.
invalid-option-data = { $type } is no valid option data type. { $guessedType ->
        [other] Perhaps you wanted to write { $guessedType }?
        *[None]
    }
no-validator-found = No Validator implementation found for annotation "{ $annotation }" used at parameter "{ $parameter }".
validator-type-not-supported =
    The "{ $annotation }" constraint doesn't support the type of parameter "{ $parameter }". Supported types:
        -> { $supportedTypes}
wildcard-optional = Generic parameter of Optional cannot be parsed to class. Please provide a valid generic type and don't use any wildcard.
unknown-command-type = Unknown command type isn't allowed here.
invalid-context-command-type = Invalid command type for context command! Must either be USER or MESSAGE
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
incorrect-method-signature-hint = You forgot to add { $parameter } as the first parameter of the method.
member-context-guild = User context commands which use a Member object are only allowed to use InteractionContextType.GUILD.
    Change the InteractionContextType or use an User object instead.
jda-exception =
    { $cause }
    Interaction: "{ $type }" defined at "{ $class }#{ $method }".
should-never-be-thrown = This exception should never be thrown!

# Configuration Errors
resource-not-found = Failed to find resource "{ $resource }".
io-exception = "Failed to open file.
no-type-adapting-path =
    Cannot create OptionData.
    There is no type adapting path to convert from OptionType "{ $optionType }" (underlying type: "{ $source }") to "{ $target }".
    Please add a respective TypeAdapter ("{ $source }" => "{ $target }) or change the OptionType.
cycling-dependencies =
    Cycling dependencies while getting implementations of "{ $type }".
        { $data }
duplicate-commands =
    Found multiple slash commands named "{ $display }". Please remove or change one to make them unique again.
        -> { $command }
        -> { $duplicate }
missing-implementation = No implementation for "{ $type }" found. Please provide.
multiple-implementations =
    Found multiple implementations of"{ $type }", please exclude the unwanted extension:
        { $found }

## properties
reserved-priority =
    The priority { $priority } can't be used.
    Priorities ranging from 0 to 100 and 2147483647 (Integer.MAX_VALUE) are reserved.

negative-priority = The priority { $priority } is invalid. Priories must be positive!

provided-property =
    The property { $property } can only be provided by JDA-Commands.
    You can't add custom PropertyProviders for it!

user-property =
    The property { $property } can only be set by the user via JDACBuilder.
    You can't add custom PropertyProviders for it!

property-not-set =
    Missing value for the required property { $property }.
    Please provide one via JDACBuilder or an Extension!

cycling-calls-itself = PropertyProvider cannot depenend on property { $property } while providing it! (provider in { $class })

cycling-tree =
    Cannot resolve property { $property }! Found cycling dependency:

    { $tree }

# CustomId
invalid-runtime-id = Invalid runtime id! Must either be a UUID or "independent".
invalid-custom-id = Provided custom id is invalid.
independent-runtime-id = Provided custom id is runtime-independent.

# ConfigurableReply
modal-as-component = Modals cannot be attached as components! "{ $method }" is a modal method! You have to reply with "ModalReplyableEvent#replyModal".

# EditableConfigurableReply
component-replacer-v1 = Cannot use ComponentReplacers on a message that isn't Components V2. Upgrade this message to Components V2 first or
    use the V1 API for editing action components.

# ComponentEvent
remove-components-v2 = Cannot remove components on a message with Components V2. This would result in an empty message.

# ModalBuilder
no-text-input-found =
    No text input named { $input } found! Please check that the referenced text input parameter exists.
    Available text inputs for this modal are:
        "{ $available} "

# Helpers
detached-entity = { $class } doesn't support detached entities and cannot be used for user installable apps.