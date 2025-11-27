# Overview - property and configuration system
The property and configuration system is a core system of JDA-Commands. Its purpose is...

- to allow the dynamic and advance configuration of JDA-Commands through our builder and extension system
- to serve as a central collection point for all values that the user can get during runtime

You can think of it like a really primitive Dependency Injection framework that comes without any annotations.
At the heart there is basically a big <Map> mapping [properties](#properties) to different PropertyProviders which then
provide the value of this property.

## Properties
As said already, basically the whole system is about [properties][[Property]] - but what are they really?
Well, properties, as the name says, are properties of JDA-Commands as a whole. 
They can be either:

- a config option to adjust the behaviour of JDA-Commands
- an implementation of exposed services
- or a service provided by the framework that are exposed to the user

Because they're all properties of JDA-Commands, they are predefined as `public static final` in the <Property> class
and can be accessed by the user. The user can't create own properties!

### Categories of properties
To ensure an intuitive configuration experience, properties are primarily categorized by 3 groups:

- [_user settable_][[Category#USER_SETTABLE]] -> only configurable by using the designated <JDACBuilder> method
- [_user settable + loadable from extension_][[Category#LOADABLE]] -> above applies plus values can be provided by <Extension>s
- [_provided_][[Category#PROVIDED]] -> service that are provided by JDA-Commands, the user can use but not create/replace them

### Types
Due to the diverse nature of properties present - reaching from simple _one_ value ones to lists and mappings -
JDA-Commands defines 3 different types of properties:

#### Singleton
A <Property.Singleton> can only have one final value - no list, no map, just one value.

To decide which value will be used, the priorities of all <PropertyProvider>s are compared and
the highest will be chosen as the provider for this value. To learn more about this, take a look [here](TODO)

#### Enumeration
A <Property.Enumeration> is basically a <Collection>, that will consist of the accumulated values of
all <PropertyProvider> for this property.


