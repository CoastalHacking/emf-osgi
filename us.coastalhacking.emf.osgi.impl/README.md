
# us.coastalhacking.emf.osgi.impl

These are the underlying EMF thread-safe implementations which can be used separately from OSGi if desired.

Of note:

* Implementations do not refer to the standard EMF global registries. 
* Implementations should be thread-safe.
* Some implementations behave differently than EMF's default implementations.

## Global Registries 

EMF uses global registries, populated by default when it runs within an Equinox OSGi environment, via Equinox extensions.

The implementations here do not defer to global registries. Some do support deferring to a delegate, such as `ConcurrentEPackageRegistryImpl`, however the delegate is not set automatically.

## Thread Safety

EMF natively is not thread-safe. However, some data structures can be made thread-safe more easily than others.


### EList

Uses of `org.eclipse.emf.common.util.EList<E>` as members are implemented with `org.eclipse.emf.common.util.DelegatingEList<E>`, where `DelegatingEList.delegateList()` returns `java.util.concurrent.CopyOnWriteArrayList`. This should allow for thread-safe operations on `EList`s.

## Different Implementation Behavior

* `ConcurrentResourceSetImpl`: can return a null `URIConverter` via `getURIConverter()`
* `ConcurrentUriConverterImpl`: its `getURIMap()` is implemented via a concurrent map instead of `URIMappingRegistryImpl`. This means prefix support isn't provided.

## Todos


### Thread-Safe URIMappingRegistryImpl
 
Consider creating a thread-safe `URIMappingRegistryImpl`-like implementation. Its base class is a `BasicEMap`. Quickly looking, it looks like the following is needed:

* Override / mask `uriMap` member
* Specialize `delegateEList` to a copy on write array list
* Override all methods which call prefixMaps and re-implement
  * `did(Add|Modify|Remove|Clear)`
  * getURI
* Probably override `View` and many others?


