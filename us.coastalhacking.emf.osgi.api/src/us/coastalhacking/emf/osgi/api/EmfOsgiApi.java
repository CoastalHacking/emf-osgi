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
		 * Reference names used by compliant ResourceSet providers
		 */
		@interface Reference {
			String URI_CONVERTER = "emf.resourceset.uriconverter";
			String URI_CONVERTER_TARGET = "emf.resourceset.uriconverter.target";
			String EPACKAGE_REGISTRY = "emf.resourceset.epackageregistry";
			String EPACKAGE_REGISTRY_TARGET = "emf.resourceset.epackageregistry.target";
			String RESOURCE_FACTORY_REGISTRY = "emf.resourceset.resourcefactoryregistry";
			String RESOURCE_FACTORY_REGISTRY_TARGET = "emf.resourceset.resourcefactoryregistry.target";
			String ADAPTER_FACTORY = "emf.resourceset.adapterfactory";
			String ADAPTER_FACTORY_TARGET = "emf.resourceset.adapterfactory.target";
			String RESOURCE = "emf.resourceset.resource";
			String RESOURCE_TARGET = "emf.resourceset.resource.target";
		}
	}

	/**
	 * Component property type for
	 * {@link org.eclipse.emf.ecore.resource.URIConverter} providers
	 */
	@interface URIConverter {
		String PID = "org.eclipse.emf.ecore.resource.URIConverter.pid";
		
		/**
		 * Reference names used by compliant URIConverter providers
		 */
		@interface Reference {
			String URI_HANDLERS = "emf.uriconverter.urihandlers";
			String URI_HANDLERS_TARGET = "emf.uriconverter.urihandlers.target";
			String CONTENT_HANDLERS = "emf.uriconverter.contenthandlers";
			String CONTENT_HANDLERS_TARGET = "emf.uriconverter.contenthandlers.target";
		}
	}

	/**
	 * Component property type for {@link org.eclipse.emf.ecore.EPackage.Registry}
	 * providers
	 */
	@interface EPackage_Registry {
		String PID = "org.eclipse.emf.ecore.EPackage.Registry.pid";

		/**
		 * Reference names used by compliant EPackage.Registry providers
		 */
		@interface Reference {
			String EPACKAGES = "emf.epackageregistry.epackages";
			String EPACKAGES_TARGET = "emf.epackageregistry.epackages.target";
			String EPACKAGE_DESCRIPTORS = "emf.epackageregistry.epackagedescriptors";
			String EPACKAGE_DESCRIPTORS_TARGET = "emf.epackageregistry.epackagedescriptors.target";
			String DELEGATE = "emf.epackageregistry.delegate";
			String DELEGATE_TARGET = "emf.epackageregistry.delegate.target";
			String DELEGATE_TARGET_DEFAULT = "(delegate=true)";
		}
	}

	/**
	 * Component property type for
	 * {@link org.eclipse.emf.ecore.resource.Resource.Factory.Registry} providers
	 */
	@interface Resource_Factory_Registry {
		String PID = "org.eclipse.emf.ecore.resource.Resource.Factory.Registry.pid";

		/**
		 * Reference names used by compliant Resource.Factory.Registry providers
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
