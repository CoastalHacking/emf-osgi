package us.coastalhacking.emf.osgi.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.Hashtable;

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.util.tracker.ServiceTracker;

import us.coastalhacking.emf.osgi.api.EmfOsgiApi;
import us.coastalhacking.emf.osgi.api.EmfOsgiApi.Property;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceFactoryRegistryProviderITest extends AbstractITest {

	@BeforeClass
	public static void beforeClass() throws Exception {
		AbstractITest.beforeClass();
	}

	@AfterClass
	public static void afterClass() throws Exception {
		AbstractITest.afterClass();
	}

	@Before
	public void before() throws Exception {
		super.before();
	}

	@After
	public void after() throws Exception {
		super.after();
	}

	@Test
	public void shouldGetDefaultFactoryRegistry() throws Exception {
		Resource.Factory.Registry registry = serviceTrackerHelper(
				new ServiceTracker<>(CONTEXT, Resource.Factory.Registry.class, null), 1000);
		assertNotNull(registry);
		assertEquals(EmfOsgiApi.Resource_Factory_Registry.PID, registry.toString());
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

		// Configure and obtain the service
		Resource.Factory.Registry registry = configurationHelper(Resource.Factory.Registry.class,
				EmfOsgiApi.Resource_Factory_Registry.PID, factoryRegistryProps, timeout);
		
		// Verify
		assertNotEquals(EmfOsgiApi.Resource_Factory_Registry.PID, registry.toString());
		assertEquals(factory, registry.getProtocolToFactoryMap().get(scheme));
		assertEquals(factoryDescriptor, registry.getExtensionToFactoryMap().get(extension));
	}

}
