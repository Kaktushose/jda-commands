JDA-Commands also provides a pagination implementation that is based on Components V2 and is controlled by action components.

## Layout
A <Pagination> is made up of <PaginationLayout>s. Each <PaginationLayout> can be placed freely and as many times as
wanted. The only limit is <Message#MAX_CONTENT_LENGTH_COMPONENT_V2>. 

The following <PaginationLayout>s are available:

- <Static>: Holds static content, always remains exactly the same
- <Dynamic>: Its content is dependent on the pagination state, e.g. the current page number
- <ControlRow>: Can hold <io.github.kaktushose.jdac.components.pagination.layout.Control>s. You can think of it like <ActionRow>

!!! example
    ```java
    Pagination.of(
        Static.text("Pagination Example"),
        Static.divider(Spacing.SMALL),
        Dynamic.of(page -> List.of(TextDisplay.of("Page: %d".formatted(page.currentPage())))),
        ControlRow.of(Control.forward("onForward"), Control.backward("onBackward"))
    );
    ```

By default, the <Pagination> will be wrapped inside a <net.dv8tion.jda.api.components.container.Container>. This can be 
disabled by calling <Pagination#container(boolean)>. Call <Pagination#spoiler(boolean)> and 
<Pagination#color(Color)> to customize the <net.dv8tion.jda.api.components.container.Container>.

!!! example
    ```java
    var pagination = Pagination.of(...).container(false);

    pagination = pagination.container(true).color(Color.RED);
    ```

## Maximum Page
By default, the pagination has no last page. It is possible to scroll forward indefinitely. Use <Pagination#maxPages(int)> to
set a limit. Use <Page#cancel()> to set the maximum number of pages dynamically when paginating.

!!! example
    ```java
    Pagination.of(
        Dynamic.of(page -> {
            if (resultSet.isLast()) {
                page.cancel();//(1)!
                return List.of(TextDisplay.of("No more data available"));
            }
            return renderPage(resultSet);
        })
    );    
    ```
    
    1. Dynamically sets the maximum number of pages to the current page

## Threshold
<Dynamic> content, <ControlRow>s as well as their <io.github.kaktushose.jdac.components.pagination.layout.Control>s can
have a [`#threshold()`][[Threshold#threshold()]] that must be reached before they show up. In other words, the current 
page number must be equal or greater than then the threshold for the <PaginationLayout> to show up.

!!! example
    ```java
    Pagination.of(
        Dynamic.of(...).threshold(2)//(1)! 
    );
    ```

    1. Only show this dynamic content starting from the second page

## Controls
There are two types of controls: <PageButton> and <PageSelect>

### <PageButton>
A <PageButton> is backed by a <net.dv8tion.jda.api.components.buttons.Button> that performs an arbitrary task with the <Pagination>. Most commonly, this
would be scrolling back and forth, but could also be something like a refresh button.

<PageButton>s can have a <Direction>. When the direction is set to <Direction#BACKWARD>, the <PageButton> will
automatically be disabled on the first page. <Direction#FORWARD> will disable the <PageButton> on the last page.
<Direction#NEUTRAL> will keep the <PageButton> always enabled.

When used within JDA-Commands you can use the <io.github.kaktushose.jdac.dispatching.reply.Component> class to reference a
[`@Button`][[io.github.kaktushose.jdac.annotations.interactions.Button]] handler:

!!! example
    ```java
    @Command("pagination")
    public void onCommand(CommandEvent event) {
        event.reply(Pagination.of(
            Dynamic.of(...),
            Control.forward(Component.button("onForth"));
        ));
    }    

    @Button("Forth")
    public void onForth(ComponentEvent event) {
        event.reply(pagination.forward());
    }
    ```
### <PageSelect>
A <PageSelect> is backed by <StringSelectMenu> and can be used by the user to directly jump to a specific page. The 
<SelectOption>s are generated automatically depending on the state of the <Pagination>. 

If set, <Pagination#maxPages()> will determine the number of <SelectOption>s generated, else <Pagination#currentPage()>. 
This can be overwritten by <PageSelect#selectOptions(Integer)>. However, if in any case the number of <SelectOption>s 
exceeds <SelectMenu#OPTIONS_MAX_AMOUNT>, this value will be used instead.

The <SelectOption>'s label will be generated using Java's <String#format(String, Object...)> method with a format 
String and the current page number. The default format String is `Page %d`. Use <PageSelect#format(String)> to change 
the format String. The <SelectOption>'s value will always just be the page number.

When used within JDA-Commands you can use the <io.github.kaktushose.jdac.dispatching.reply.Component> class to reference 
a <StringMenu> handler. 

!!! example
    ```java
    @Command("pagination")
    public void onCommand(CommandEvent event) {
        event.reply(Pagination.of(
            Dynamic.of(...),
            Control.select(Component.stringSelect("onPageSelect"));
        ).maxPages(10));
    }    

    @StringMenu("Forth")
    public void onPageSelect(ComponentEvent event, List<String> options) {
        event.reply(pagination.page(options.getFirst().getValue()));
    }
    ```

!!! note 
    The used <StringMenu> for a <PageSelect> cannot have any own <MenuOption>s.

### <ControlRow>

Both <PageButton> and <PageSelect> must be wrapped in a <ControlRow> and just like with <ActionRow>, one <ControlRow> can support up to five
<PageButton>s but only one <PageSelect> per row.

The <Pagination> is then controlled by the instance itself. Use methods like <Pagination#forward()> or <Pagination#backward()>
to paginate back and forth.

!!! example
    ```java
    @Button("Back")
    public void onBack(ComponentEvent event) {
        event.reply(pagination.backward());
    }
    ```

## Replying
When used inside JDA-Commands, just pass the <Pagination> to <ReplyableEvent#reply(Pagination, Entry...)>.
[Localization](../../message/localization.md) will work just as with any other message.

When used outside JDA-Commands, call <Pagination#build()> to retrieve a collection of <MessageTopLevelComponent>s to send.

!!! warning
    When using <Pagination#build()>, the pagination will not be localized!
