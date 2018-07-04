
# us.coastalhacking.emf.osgi.provider

OSGi declarative service components

They can be deployed as is, resulting in by default singleton-scoped components.

These singleton components can be configured via their default service factory PID (see Configuration Admin.) They can also be uniquely configured by creating a new configuration based on their factory PID. 

## Todos

* Consider registering global registries for those which have default EMF global registries.