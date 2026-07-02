JDA-Commands also provides a collection of utility classes for components V2. You can think of these like an ui 
component library.

## <SequencedContainer>
A <SequencedContainer> is an implementation of JDA's <net.dv8tion.jda.api.components.container.Container> that allows 
adding <ContainerChildComponent>s sequentially. Compared to JDA's <net.dv8tion.jda.api.components.container.Container>, 
the component list of this container isn't immutable and can be extended. This allows for a dynamic creation of
containers.

Similar to Java's <SequencedCollection>, special methods like 
[`SequencedContainer#addFirst(...)`][[AbstractSequencedContainer#addFirst(E, Entry...)]] and 
[`SequencedContainer#addLast(...)`][[AbstractSequencedContainer#addLast(E, Entry...)]] exist to further improve the developer
experience.

This class is fully compatible with JDA and implements both <net.dv8tion.jda.api.components.container.Container> and 
<MessageTopLevelComponentUnion>. That means it can be used anywhere JDA or JDA-Commands accepts a 
<net.dv8tion.jda.api.components.container.Container>.
!!! example
    ```java
    SequencedContainer<ContainerChildComponent> container = SequencedContainer.of(TextDisplay.of("Hello World!"));
    
    container.add(Separator.createDivider(Spacing.SMALL));
    
    container.add(TextDisplay.of("Goodbye World"));
    ```

### <SeparatedContainer>
A <SeparatedContainer> is a special type of <SequencedContainer> that always adds a 
<net.dv8tion.jda.api.components.separator.Separator> between its elements. All other rules described above are the same. 

!!! example
    ```java
    SeparatedContainer<ContainerChildComponent> container = SeparatedContainer.of(
            TextDisplay.of("Hello World!"), 
            Separator.createDivider(Spacing.SMALL)
    );//(1)!

    container.add(TextDisplay.of("Goodbye World"));

    container.addFirst(TextDisplay.of("Above Worlds", Separator.createInvisible(Spacing.LARGE));//(2)!
    ```
    
    1. The factory method also declares the default <net.dv8tion.jda.api.components.separator.Separator> to use
    2. Overrides the default <net.dv8tion.jda.api.components.separator.Separator>, also accepts `null` for no <net.dv8tion.jda.api.components.separator.Separator>

### <TextDisplayContainer>
A <TextDisplayContainer> is another type of special <SequencedContainer> implementation that only allows <TextDisplay>s.

!!! example
    ```java
    var container = SequencedTextDisplay.of("Line 1");

    container.add("Line 2");

    container.addFirst("Line 0");
    ```

## <SequencedTextDisplay>
A <SequencedTextDisplay> is a special <TextDisplay> implementation that is internally backed by a 
<SequencedCollection> and allows its content to be appended in sequence.

The <SequencedTextDisplay#add(String, Entry...)> methods of this class will first append to this internally stored
<SequencedCollection>. When <SequencedTextDisplay#getContent()> or <SequencedTextDisplay#toData()> gets called, this 
collection gets unfolded and all <TextDisplay>s are joined into one. <SequencedTextDisplay#textDisplays()> will return 
the <TextDisplay>s directly as stored in the <SequencedCollection>.

### Usage inside [SequencedContainers](#sequencedcontainer)
If used inside a <SequencedContainer>, the <TextDisplay>s don't get joined and each <TextDisplay> will be added
individually in sequence.

## Localization
All implementations presented above also support localization. When `#getComponents()` or `#toData()` is called, the
component list of these containers is localized via <ComponentResolver>. Use <LocalizedComponent#entries(Entry...)> to 
provide additional [Entries][[io.github.kaktushose.jdac.message.placeholder.Entry]] outside the 
[`#add(T, Entry)`][[SequencedComponent#add(T, Entry...)]] methods.

## Usage outside JDA-Commands
The static `#of(...)` factory methods use the <JDACIntrospection> API to access a <MessageResolver> and the user locale. 
This means, this static `#of(...)` method is only usable inside <JDACScope#PREPARATION>. 

If you want to use these classes 
outside JDA-Commands call the constructor and pass a <io.github.kaktushose.jdac.message.resolver.Resolver> as well as 
the <Locale> manually.
!!! example
    ```java
    SequencedContainer<ContainerChildComponent> container = new SequencedContainer(resolver, locale, TextDisplay.of("Hello World!"));
    ```
