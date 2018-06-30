package us.coastalhacking.emf.osgi.api;

public interface EmfOsgiApi {

	@interface Property {
		String NS_URI = "emf.nsuri";
		String SCHEME = "emf.resource.factory.scheme";
		String EXTENSION = "emf.resource.factory.extension";
		String CONTENT_TYPE = "emf.resource.factory.contenttype";
	}
	
	//
	// PROVIDER COMPONENT PROPERTY TYPES
	//
	/**
	 * Component property type for
	 * {@link org.eclipse.emf.ecore.resource.ResourceSet} providers
	 */
	@interface ResourceSet {
		String PID = "org.eclipse.emf.ecore.resource.ResourceSet.pid";

		/**
		 * Recommended reference names to be used by ResourceSet providers
		 */
		@interface Reference {
			String URI_CONVERTER = "emf.resourceset.uriconverter";
			String URI_CONVERTER_TARGET = "emf.resourceset.uriconverter.target";
			String EPACKAGE_REGISTRY = "emf.resourceset.epackageregistry";
			String EPACKAGE_REGISTRY_TARGET = "emf.resourceset.epackageregistry.target";
			String RESOURCE_FACTORY_REGISTRY = "emf.resourceset.resourcefactoryregistry";
			String RESOURCE_FACTORY_REGISTRY_TARGET = "emf.resourceset.resourcefactoryregistry.target";
		}
	}

	/**
	 * Component property type for
	 * {@link org.eclipse.emf.ecore.resource.URIConverter} providers
	 */
	@interface URIConverter {
		String PID = "org.eclipse.emf.ecore.resource.URIConverter.pid";
	}

	/**
	 * Component property type for {@link org.eclipse.emf.ecore.EPackage.Registry}
	 * providers
	 */
	@interface EPackage_Registry {
		String PID = "org.eclipse.emf.ecore.EPackage.Registry.pid";

		/**
		 * Recommended reference names to be used by EPackage.Registry providers
		 */
		@interface Reference {
			String EPACKAGES = "emf.epackage.registry.epackages";
			String EPACKAGES_TARGET = "emf.epackage.registry.epackages.target";
			String EPACKAGE_DESCRIPTORS = "emf.epackage.registry.epackagedescriptors";
			String EPACKAGE_DESCRIPTORS_TARGET = "emf.epackage.registry.epackagedescriptors.target";
		}
	}

	/**
	 * Component property type for
	 * {@link org.eclipse.emf.ecore.resource.Resource.Factory.Registry} providers
	 */
	@interface Resource_Factory_Registry {
		String PID = "org.eclipse.emf.ecore.resource.Resource.Factory.Registry.pid";

		/**
		 * Recommended reference names to be used by Resource.Factory.Registry providers
		 */
		@interface Reference {
			String RESOURCE_FACTORIES = "emf.resource.factory.registry.resourcefactories";
			String RESOURCE_FACTORIES_TARGET = "emf.resource.factory.registry.resourcefactories.target";
			String RESOURCE_FACTORY_DESCRIPTORS = "emf.resource.factory.registry.resourcefactorydescriptors";
			String RESOURCE_FACTORY_DESCRIPTORS_TARGET = "emf.resource.factory.registry.resourcefactorydescriptors.target";
			String URI_CONVERTER = "emf.resource.factory.registry.uriconverter";
			String URI_CONVERTER_TARGET = "emf.resource.factory.registry.uriconverter.target";
		}
	}

}
