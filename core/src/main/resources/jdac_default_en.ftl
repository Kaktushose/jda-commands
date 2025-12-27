# Error Messages
## Type Adapting Failed
adapting-failed-title =
    ## Invalid Arguments
    ### Command
    { $command }
adapting-failed-details =
    ### Details
    **Expected Type**
    `{ $expected }`
    **Actual Type**
    `{ $actual }`
    **Raw Input**
    `{ $raw }`
adapting-failed-message =
    ### Message
    { $message }
## Insufficient Permissions
insufficient-permissions =
    ## Insufficient Permissions
    `{ $interaction }` requires specific permissions to be executed
    ### Required Permissions
    `{ $permissions }`
## Constraint Failed
constraint-failed =
    ### Parameter Error
    { $ message }
## Interaction Execution Failed
execution-failed-title =
    ## Interaction Execution Failed
    The interaction execution has unexpectedly failed. Please report the following error to the bot devs.
execution-failed-message =
    ### Error Message
    ```
    The user "{ $ user }" attempted to execute a "{ $interaction }" interaction at { $timestamp } but a "{ $ exception}" occured.
    Please refer to the logs for further information.
    ```
## Unknown Interaction
unknown-interaction =
    ### Unknown Interaction
    This interaction timed out and is no longer available!

# Constraints
member-missing-permission = Member is missing at least one permission that is required.
member-has-unallowed-permission = Member has at least one permission that isn't allowed.
# Type Adapter
member-required-got-user = A member is required, but only a user got provided.
# Command Building
no-description = no description
