package us.coastalhacking.emf.osgi.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;

import us.coastalhacking.emf.osgi.api.EmfOsgiApi;
import us.coastalhacking.emf.osgi.api.EmfOsgiApi.Property;

@RunWith(MockitoJUnitRunner.class)
public class ProviderIntegrationTest {

	private static final BundleContext CONTEXT = FrameworkUtil.getBundle(ProviderIntegrationTest.class)
			.getBundleContext();

	private static ConfigurationAdmin configAdmin;

	private final List<ServiceTracker<?, ?>> serviceTrackers = new CopyOnWriteArrayList<>();
	private static final List<ServiceTracker<?, ?>> staticServiceTrackers = new CopyOnWriteArrayList<>();
	private final List<ServiceRegistration<?>> serviceRegistrations = new CopyOnWriteArrayList<>();
	private final List<Configuration> configurations = new CopyOnWriteArrayList<>();

	private static final long timeout = 1000; // milliseconds;
	private static final Hashtable<String, Object> customProps = new Hashtable<>();
	private static final String customPropertyKey = "some.custom.property";
	private static final String customPropertyValue = "some.custom.value";
	private static final String customTarget = String.format("(%s=%s)", customPropertyKey, customPropertyValue);

	@BeforeClass
	public static void beforeClass() throws Exception {
		customProps.put(customPropertyKey, customPropertyValue);
		configAdmin = staticServiceTrackerHelper(new ServiceTracker<>(CONTEXT, ConfigurationAdmin.class, null),
				timeout);
	}

	@AfterClass
	public static void afterClass() {
		configAdmin = null;
		staticServiceTrackers.forEach(st -> {
			st.close();
		});
		staticServiceTrackers.clear();

	}

	@Before
	public void before() {
		assertTrue(serviceTrackers.isEmpty());
		assertTrue(serviceRegistrations.isEmpty());
		assertTrue(configurations.isEmpty());
	}

	@After
	public void after() {
		serviceRegistrations.forEach(sr -> {
			try {
				sr.unregister();
			} catch (IllegalArgumentException e) {
				// gobble
			}
		});
		serviceRegistrations.clear();
		serviceTrackers.forEach(st -> {
			st.close();
		});
		serviceTrackers.clear();

		configurations.forEach(c -> {
			try {
				c.delete();
			} catch (Exception e) {
				// gobble
			}
		});
		configurations.clear();
	}

	private <T> T serviceTrackerHelper(ServiceTracker<?, T> st, long timeout) throws Exception {
		return serviceTrackerHelper(serviceTrackers, st, timeout);
	}

	private static <T> T staticServiceTrackerHelper(ServiceTracker<?, T> st, long timeout) throws Exception {
		return serviceTrackerHelper(staticServiceTrackers, st, timeout);
	}

	private static <T> T serviceTrackerHelper(List<ServiceTracker<?, ?>> trackers, ServiceTracker<?, T> st,
			long timeout) throws Exception {
		// Add before opening just in case opening throws an exception
		trackers.add(st);
		st.open();
		return st.waitForService(timeout);
	}

	private String toFilter(Class<?> clazz, String servicePid, Map<String, Object> props) {
		return String.format("(&(%s=%s)%s)", Constants.SERVICE_PID, servicePid, props.entrySet().stream().map(
				es -> String.format("(%s=%s)", es.getKey(), weaklyEscapeForLdapSearchFilter((String) es.getValue())))
				.collect(Collectors.joining()));
	}

	// Don't use me in prod! I'm weak and just for testing...
	private String weaklyEscapeForLdapSearchFilter(String unescaped) {
		final StringBuilder result = new StringBuilder(unescaped.length());
		unescaped.chars().forEach(i -> {
			char c = (char) i;
			switch (c) {
			case '(':
				result.append("\\(");
				break;
			case ')':
				result.append("\\)");
				break;
			default:
				result.append(c);
				break;
			}
		});
		return result.toString();
	}

	private <S> S configurationHelper(Class<S> clazz, String factoryPid, Map<String, Object> props, long timeout)
			throws Exception {
		// Create the configuration
		final Configuration configuration = configAdmin.createFactoryConfiguration(factoryPid, "?");
		configurations.add(configuration);
		// Updating it with the passed-in properties
		configuration.update(new Hashtable<>(props));
		// Strictly return the specific service, ensuring the desired properties are
		// also present
		final String filter = toFilter(clazz, configuration.getPid(), props);
		return serviceTrackerHelper(new ServiceTracker<>(CONTEXT, CONTEXT.createFilter(filter), null), timeout);
	}

	/**
	 * Basic OSGi provider test that simulates default (albeit not recommended)
	 * usage.
	 */
	@Test
	public void shouldServeResourceSetDefault() throws Exception {
		ResourceSet rs = serviceTrackerHelper(new ServiceTracker<>(CONTEXT, ResourceSet.class, null), 1000);
		assertNotNull(rs);
		assertNotNull(rs.getURIConverter());
		assertNotNull(rs.getResourceFactoryRegistry());
		assertNotNull(rs.getPackageRegistry());
	}

	@Test
	public void shouldConfigureAndRegisterFactoryRegistry() throws Exception {
		// Register Factory
		Resource.Factory factory = mock(Resource.Factory.class);
		final Hashtable<String, Object> factoryProps = new Hashtable<>(customProps);
		final String scheme = "emf";
		factoryProps.put(Property.SCHEME, scheme);
		serviceRegistrations.add(CONTEXT.registerService(Resource.Factory.class, factory, factoryProps));

		// Register Factory.Descriptor
		final String extension = "fme";
		final Hashtable<String, Object> factoryDescriptorProps = new Hashtable<>(customProps);
		factoryDescriptorProps.put(Property.EXTENSION, extension);
		Resource.Factory.Descriptor factoryDescriptor = mock(Resource.Factory.Descriptor.class);
		serviceRegistrations.add(
				CONTEXT.registerService(Resource.Factory.Descriptor.class, factoryDescriptor, factoryDescriptorProps));

		// Strictly target the registry references
		final Hashtable<String, Object> factoryRegistryProps = new Hashtable<>(customProps);
		factoryRegistryProps.put(EmfOsgiApi.Resource_Factory_Registry.Reference.RESOURCE_FACTORIES_TARGET,
				customTarget);
		factoryRegistryProps.put(EmfOsgiApi.Resource_Factory_Registry.Reference.RESOURCE_FACTORY_DESCRIPTORS_TARGET,
				customTarget);

		// Verify
		Resource.Factory.Registry factoryRegistry = configurationHelper(Resource.Factory.Registry.class,
				EmfOsgiApi.Resource_Factory_Registry.PID, factoryRegistryProps, timeout);
		assertEquals(factory, factoryRegistry.getProtocolToFactoryMap().get(scheme));
		assertEquals(factoryDescriptor, factoryRegistry.getExtensionToFactoryMap().get(extension));
	}

	@Test
	public void shouldConfigureAndRegisterUriConverter() throws Exception {
		// Nothing to target, use default custom properties
		// No need to check return value. throws an exception if timed out
		configurationHelper(URIConverter.class, EmfOsgiApi.URIConverter.PID, customProps, timeout);
	}

	@Test
	public void shouldConfigureAndRegisterEPackageRegistry() throws Exception {
		// Register EPackage
		EPackage ePackage = mock(EPackage.class);
		final Hashtable<String, Object> ePackageProps = new Hashtable<>(customProps);
		final String ePackageNsUri = "epackage.ns.uri";
		ePackageProps.put(Property.NS_URI, ePackageNsUri);
		serviceRegistrations.add(CONTEXT.registerService(EPackage.class, ePackage, ePackageProps));

		// Register EPackage.Descriptor
		EPackage.Descriptor descriptor = mock(EPackage.Descriptor.class);
		when(descriptor.getEPackage()).thenReturn(ePackage);
		final Hashtable<String, Object> descriptorProps = new Hashtable<>(customProps);
		final String descriptorNsUri = "descriptor.ns.uri";
		descriptorProps.put(Property.NS_URI, descriptorNsUri);
		serviceRegistrations.add(CONTEXT.registerService(EPackage.Descriptor.class, descriptor, descriptorProps));

		// Strictly target the registry references
		final Hashtable<String, Object> ePackageRegistryProps = new Hashtable<>(customProps);
		ePackageRegistryProps.put(EmfOsgiApi.EPackage_Registry.Reference.EPACKAGE_DESCRIPTORS_TARGET, customTarget);
		ePackageRegistryProps.put(EmfOsgiApi.EPackage_Registry.Reference.EPACKAGES_TARGET, customTarget);

		// Verify
		EPackage.Registry registry = configurationHelper(EPackage.Registry.class, EmfOsgiApi.EPackage_Registry.PID,
				ePackageRegistryProps, timeout);
		assertEquals(ePackage, registry.getEPackage(ePackageNsUri));
		assertEquals(ePackage, registry.getEPackage(descriptorNsUri));
	}

	@Test
	public void shouldConfigureAndRegisterResourceSet() throws Exception {
		// Configure references
		// Below is order-sensitive, since we're keeping local copies of the service
		// Resource.Factory.Registry consumes the same URIConverter
		URIConverter uriConverter = configurationHelper(URIConverter.class, EmfOsgiApi.URIConverter.PID, customProps,
				timeout);
		Resource.Factory.Registry factoryRegistry = configurationHelper(Resource.Factory.Registry.class,
				EmfOsgiApi.Resource_Factory_Registry.PID, customProps, timeout);
		EPackage.Registry packageRegistry = configurationHelper(EPackage.Registry.class,
				EmfOsgiApi.EPackage_Registry.PID, customProps, timeout);

		// Strictly target the references
		final Hashtable<String, Object> resourceSetProps = new Hashtable<>(customProps);
		resourceSetProps.put(EmfOsgiApi.ResourceSet.Reference.EPACKAGE_REGISTRY_TARGET, customTarget);
		resourceSetProps.put(EmfOsgiApi.ResourceSet.Reference.RESOURCE_FACTORY_REGISTRY_TARGET, customTarget);
		resourceSetProps.put(EmfOsgiApi.ResourceSet.Reference.URI_CONVERTER_TARGET, customTarget);

		// Verify
		ResourceSet resourceSet = configurationHelper(ResourceSet.class, EmfOsgiApi.ResourceSet.PID, resourceSetProps,
				timeout);
		assertEquals(factoryRegistry, resourceSet.getResourceFactoryRegistry());
		assertEquals(packageRegistry, resourceSet.getPackageRegistry());
		assertEquals(uriConverter, resourceSet.getURIConverter());
	}
}