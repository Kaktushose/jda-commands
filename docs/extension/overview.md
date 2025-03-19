# Overview
Extensions are a way to customize the behaviour of JDA-Commands in a modular style, giving an alternative option to
utilizing the JDA-Commands builder.

## Motivation
The reason for adding this rather complicated feature to JDA-Commands is to allow the user to integrate another
[Dependency Injection Framework](../di.md) than Guice. The default Guice integration is in fact also an extension, which could just be excluded.

Additionally, extensions can also be used to share common behavior between bots or to integrate other libraries.

!!! tip
    For a working example of an extension you can take a look at the
    [default guice extension](https://github.com/Kaktushose/jda-commands/tree/main/guice-extension).