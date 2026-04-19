JDA-Commands also provides a collection of utility classes for components V2. You can think of these like an ui 
component library.

## <SequencedContainer>
A <Container> implementation that allows adding <ContainerChildComponent>s sequentially.

This class is fully compatible with JDA and implements both <Container> and <MessageTopLevelComponentUnion>. Use
<#of(ContainerChildComponent)> to create a new <SequencedContainer>. Compared to JDA's <Container>, the component
list of this container isn't immutable and can be extended.
```java
SequencedContainer<ContainerChildComponent> container = SequencedContainer.of(TextDisplay.of("Hello World!"));

container.add(Separator.createDivider(Spacing.SMALL));

container.add(TextDisplay.of("Goodbye World"));
```

## Localization
This <Container> implementation also supports localization. When <#getComponents()> or <#toData()> is called, the
component list of this container is localized via <ComponentResolver>. Use <#entries(Entry...)> to provide
additional [Entries][[Entry]] outside the <SequencedContainer#add(ContainerChildComponent, Entry...)> methods.

## Usage outside JDA-Commands
<Container#of(Collection)> uses the <JDACIntrospection> API to access a <Resolver> and the user locale. This means, the
static `of` factory method is only usable in <JDACScope#PREPARATION>. If you want to use this class outside JDA-Commands
call the constructor and pass the <Resolver> as well as the <Locale> manually.
```java
SequencedContainer<ContainerChildComponent> container = new SequencedContainer(resolver, locale, TextDisplay.of("Hello World!));
```