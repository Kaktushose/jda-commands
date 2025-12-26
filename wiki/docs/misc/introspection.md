# Introspection API
The introspection api allows you to access the frameworks [properties](property.md) and event system during runtime.

## Accessing the api
To access the api, you can either

- use <JDACommands#introspection()> or <Event#introspection()>
- use <Introspection#access()> that will return the current instance of this scope

### Using scoped access
When accessing trough <Introspection#access()>, you have to pay attention where you do so.
An <Introspection> instance is set in most but not in all places, to know where you can use it take a look at the
<IntrospectionAccess> annotation on user implementable methods provided by the framework.

Inside of [interaction controller methods](../interactions/overview.md#structure) 
(the ones having <io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent>, <CommandEvent> etc. 
as a parameter and are defined inside a class annotated with <io.github.kaktushose.jdac.annotations.interactions.Interaction>)
the <Introspection> instance is always set with the stage <Stage#INTERACTION>, providing access to all <Property>s.

!!! note
    Internally we use java's <ScopedValue>s for this. If you want to know how <Introspection#access()> works 
    with threads and friends, please take a look at their docs.

## Getting [Properties][[Property]]
!!! info
    Please make yourself conformable with our [properties system](property.md) before reading this section.

The [property system](property.md) allows accessing all public components and configuration options of JDA-Commands.
It can be used to retrieve framework services like <MessageResolver>, config options like <Property#GLOBAL_REPLY_CONFIG>
or context dependent information like <InvocationContext>.

Sometimes it's required to access such parts of the framework in places were this isn't directly practical with any 
"traditional" api. For that help you can access all parts via properties by using <Introspection#get(Property)>.

Please note that access is read-only, you can't set the value of a <Property> after starting the framework.

### Stage
A <Property>'s value isn't set in all places where you could access it.
The value of such properties varies depending on the location (in code) where you access the <Introspection> class.

To know what property is accessible, take a look at <Introspection#currentStage()> and compare it to <Property#stage()>.
A hint on the current stage is also provided by <IntrospectionAccess#value()>, which can be found on methods
available to be implemented by the user of the library.

When accessing the introspection api inside an [interaction controller method](../interactions/overview.md#structure)
the stage is always <Stage#INTERACTION> providing access to all properties.

## Subscribing to <FrameworkEvent>s
Sometimes it's convenient to execute some custom code at some point during runtime based on events inside the framework.

An example can be found inside the guice extension, were we use a <RuntimeCloseEvent> to remove
the interaction controller instances inside the cache at the end of a conversation.

To subscribe to a <FrameworkEvent> you use <Introspection#subscribe(Class, Subscriber)> which returns a <io.github.kaktushose.jdac.introspection.lifecycle.Subscription> allowing you
to ["unsubscribe"][[Subscription#unsubscribe()]] from this event later. 
If an <FrameworkEvent> is fired by JDA-Commands all <io.github.kaktushose.jdac.introspection.lifecycle.Subscriber>s of that event are called.

It's important to know that the events are published by multiple threads perhaps concurrently, thus <io.github.kaktushose.jdac.introspection.lifecycle.Subscriber>s
may be also called concurrently. They have to be written with threadsafety in mind!

### Accessing <Introspection> inside <io.github.kaktushose.jdac.introspection.lifecycle.Subscriber>
A <io.github.kaktushose.jdac.introspection.lifecycle.Subscriber> provides two arguments:

- the published instance of <FrameworkEvent>
- the <Introspection> instance used to publish the event

To know in which <Stage> the event is published (and thus what [Properties][[Property]] can be used), take a look
at the <IntrospectionAccess> annotation of the specific <FrameworkEvent> subclass.

For example, <InteractionStartEvent> has [`@IntrospectionAccess(Stage.INTERACTION)]`][[IntrospectionAccess]], what means
that inside of `Subscriber<InteractionStartEvent>#accept(InteractionStartEvent, Introspection)]` the stage <Stage#INTERACTION>
is set meaning all properties can be accessed.

You can also call <Introspection#access()> and <Introspection#accGet(Property)> inside of them!


### Example
```java
class MySubscriber implements Subscriber<InteractionStartEvent> {

    public void accept(InteractionStartEvent event, Introspection introspection) { // (2)
        JDA jda = introspection.get(Property.JDA); // (1)
        
        User user = event.invocationContext().event().getUser();
        log.info("Started interaction for user: {}", user.getName());
    }
}
```

1. you can access the introspection instance used to publish this event to get framework components
2. <InteractionStartEvent> has <IntrospectionAccess#value()> set to <Stage#INTERACTION>, thus allowing us to access properties available in this stage

