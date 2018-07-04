
# us.coastalhacking.emf.osgi.api

## EmfOsgiApi

Consists of an API implemented via annotation types focusing on OSGi:

* Service configuration properties
* Component service references
* And Configuration Admin PIDs

### Providers Implementing the API

The API conveys the following for Declarative Service providers:

* Service configuration property names. Values are strings.
* Reference names via nested annotation types matching `@interface Reference`. They should be used by providers via `@Reference(name = ...)` declarative service annotations. When utilized, bundles creating a service factory configuration via Configuration Admin can also use these names as targets. These are then included in the properties provided to Configuration Admin.
* Service factory PID used by the provider via its `@Component(configurationPid=...)` annotation. When no configurations are present, these are also the configuration PIDs.

 