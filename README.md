
# EMF OSGi

This bundle consists of an API used by OSGi providers to enable concurrent, dynamic configuration of the following:

* `org.eclipse.emf.ecore.resource.URIConverter`
* `org.eclipse.emf.ecore.EPackage.Registry`
* `org.eclipse.emf.ecore.resource.Resource.Factory.Registry`
* `org.eclipse.emf.ecore.resource.ResourceSet`

Refer to the following bundles:

* API: [us.coastalhacking.emf.osgi.api](us.coastalhacking.emf.osgi.api/README.md).
* Implementations: [us.coastalhacking.emf.osgi.impl](us.coastalhacking.emf.osgi.impl/README.md). Can be used without OSGi.
* OSGi providers: [us.coastalhacking.emf.osgi.provider](us.coastalhacking.emf.osgi.provider/README.md). Declarative service components.
* OSGi integration tests: [us.coastalhacking.emf.osgi.test](us.coastalhacking.emf.osgi.test). Useful to see how to individually configure components.