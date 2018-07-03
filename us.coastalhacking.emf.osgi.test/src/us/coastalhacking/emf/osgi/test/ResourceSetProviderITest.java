package us.coastalhacking.emf.osgi.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.util.Hashtable;

import org.eclipse.emf.common.notify.AdapterFactory;
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
import org.osgi.util.tracker.ServiceTracker;

import us.coastalhacking.emf.osgi.api.EmfOsgiApi;

@RunWith(MockitoJUnitRunner.class)
public class ResourceSetProviderITest extends AbstractITest {

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
	public void shouldGetDefaultResourceSet() throws Exception {
		ResourceSet rs = serviceTrackerHelper(new ServiceTracker<>(CONTEXT, ResourceSet.class, null), 1000);
		assertNotNull(rs);
		assertEquals(EmfOsgiApi.ResourceSet.PID, rs.toString());
		assertNotNull(rs.getURIConverter());
		assertNotNull(rs.getResourceFactoryRegistry());
		assertNotNull(rs.getPackageRegistry());
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

		// Configure and obtain the service
		ResourceSet rs = configurationHelper(ResourceSet.class, EmfOsgiApi.ResourceSet.PID, resourceSetProps,
				timeout);
		assertNotEquals(EmfOsgiApi.ResourceSet.PID, rs.toString());

		// Register AdapterFactory
		AdapterFactory adapterFactory = mock(AdapterFactory.class);
		assertTrue(rs.getAdapterFactories().isEmpty());
		serviceRegistrations.add(CONTEXT.registerService(AdapterFactory.class, adapterFactory, customProps));

		// Register EPackage.Descriptor
		Resource resource = mock(Resource.class, withSettings().extraInterfaces(Resource.Internal.class));
		assertTrue(rs.getResources().isEmpty());
		serviceRegistrations.add(CONTEXT.registerService(Resource.class, resource, customProps));

		// Verify
		assertEquals(factoryRegistry, rs.getResourceFactoryRegistry());
		assertEquals(packageRegistry, rs.getPackageRegistry());
		assertEquals(uriConverter, rs.getURIConverter());
		assertEquals(adapterFactory, rs.getAdapterFactories().get(0));
		assertEquals(resource, rs.getResources().get(0));
	}
}
