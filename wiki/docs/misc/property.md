# Properties
The property system is a core system of JDA-Commands. Its purpose is...

- to allow the dynamic and advance configuration of JDA-Commands through our builder and extension system
- to serve as a central collection point for all values that the user can get during runtime

You can think of it like a really primitive Dependency Injection framework that comes without any annotations.
At its heart, there is basically a big <Map> mapping [properties](#properties) to different [PropertyProviders](#propertyprovider) which then
provide the value of the property.

## Properties
As said already, basically the whole system is about [properties][[Property]] - but what are they really?
Well, properties, as the name says, are properties of JDA-Commands as a whole. 
They can be either:

- a config option to adjust the behaviour of JDA-Commands
- an implementation of exposed services
- or a service provided by the framework that is exposed to the user

Because they're all properties of JDA-Commands, they come predefined as `public static final` in the <Property> class
and can be accessed by the user. The user can't create own properties!

### Categories of properties
To ensure an intuitive configuration experience, properties are primarily categorized in 3 groups:

- [_user settable_][[Category#USER_SETTABLE]] -> only configurable by using the designated <JDACBuilder> method
- [_user settable + loadable from extension_][[Category#LOADABLE]] -> above applies plus values can be provided by <Extension>s
- [_provided_][[Category#PROVIDED]] -> service that are provided by JDA-Commands; the user can use but not create/replace them

### Types
Due to the diverse nature of properties, JDA-Commands defines 3 different types of properties:

#### Singleton
A <Property.Singleton> can only have one final instance. For example <Localizer> is such a property.

To decide which value will be used, the priorities of all <PropertyProvider>s are compared and
the highest will be chosen as the provider for this value. To learn more about this, take a look [here](#priority)

#### Enumeration
A <Property.Enumeration> is basically a <Collection>, that will consist of the accumulated values of
all <PropertyProvider> for this property.

Each <PropertyProvider> for property `T` of type [`enumeration`][[Property.Enumeration]] returns an `Collection<T>`.
The returned collections of all <PropertyProviders> are then [`combined`][[Collection#addAll(Collection)]] into one
and used as the final value for this property.

#### Map
A <Property.Map> is similar to <Property.Enumeration> except it uses a <Map> instead a <Collection>.
The values of all <PropertyProvider> are accumulated, while the one with higher priority
takes precedence.

Each <PropertyProvider> for a property `K, V` of type [`map`][[Property.Map]] returns an `Map<K, V>`.
The returned maps of all <PropertyProviders> are then [`combined`][[Map#putAll(Map)]] into one
and used as the final value for this property. 
If multiple <PropertyProvider>s are setting a value for the same key, then
the value of the provider with the highest [priority][[PropertyProvider#priority()]] is chosen.

!!! note "fallback/default values"
    For some properties (like <Property#CLASS_FINDER>) the default value will be completely
    overridden instead of accumulated although the property's type is enumeration or map.

    Whether the default values will be overridden or accumulated together with other values is
    defined by <PropertyProvider#fallbackBehaviour()>

## PropertyProvider
A <PropertyProvider> provides a value according to the [type](#types) of the <Property>. 

That can be either just a [simple instance](#singleton) `T`, an [enumeration](#enumeration) `Collection<T>` of
a [map](#map) `Map<K, V>`.

### Priority
Furthermore, a <PropertyProvider> has a [`priority`][[PropertyProvider#priority()]] that controls the order
in which the values are accumulated. (only important for type [singleton](#singleton) and [map](#map)).

!!! warning
    The priorities ranging from 0 to 100 and the priority <Integer#MAX_VALUE> are reserved by JDA-Commands.
    Internally, we use them as following:

    - 0                     -> fallback/default values provided by JDA-Commands
    - <Integer#MAX_VALUE>   -> all values manually set by the user in <JDACBuilder>

### value creation
An important fact of <PropertyProvider>s is, that the value is computed lazily later in the resolution process
not when the <PropertyProvider> is constructed. If another <PropertyProvider> is chosen instead of yours (due to higher priority),
that means that sometimes your [`value supplier`][[PropertyProvider#supplier()]] won't be called at all. 

However, this allows you to get the values of other properties as dependencies for your own one by calling
<PropertyProvider.Context#get(Property)> inside your [`supplier`][[PropertyProvider#supplier()]].

!!! note
    If a cycling dependency is detected during the resolution of dependencies, an excepting will be thrown
    providing information on how the recursion occurred.

#### Example of <PropertyProvider> for <Property#CLASS_FINDER>.

```java
class Foo {
    ...

    public PropertyProvider<?> provider() {
        return new PropertyProvider(
                Property.CLASS_FINDER,
                200, // just some random non reserved priority
                Foo.class, //(1)
                ctx -> List.of(new CustomClassFinder(ctx.get(Priority.PACKAGES))) //(2)
        );
    }
}
```

1. The <PropertyProvider#referenceClass()> value is just used for debugging purpose.
   The name of the class will for example be displayed in the cycling dependencies exception messages. 
2. The values for <Priority#PACKAGES> will be returned. You can get the value of any property through this method.









