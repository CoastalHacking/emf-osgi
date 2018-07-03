package us.coastalhacking.emf.osgi.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Hashtable;

import org.eclipse.emf.ecore.EPackage;
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
public class EPackageRegistryProviderITest extends AbstractITest {

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
	public void shouldGetDefaultEPackageRegistry() throws Exception {
		EPackage.Registry registry = serviceTrackerHelper(new ServiceTracker<>(CONTEXT, EPackage.Registry.class, null), 1000);
		assertNotNull(registry);
		assertEquals(EmfOsgiApi.EPackage_Registry.PID, registry.toString());
	}

	@Test
	public void shouldConfigureAndRegisterEPackageRegistry() throws Exception {

		// Strictly target the registry references
		final Hashtable<String, Object> ePackageRegistryProps = new Hashtable<>(customProps);
		ePackageRegistryProps.put(EmfOsgiApi.EPackage_Registry.Reference.EPACKAGE_DESCRIPTORS_TARGET, customTarget);
		ePackageRegistryProps.put(EmfOsgiApi.EPackage_Registry.Reference.EPACKAGES_TARGET, customTarget);
		ePackageRegistryProps.put(EmfOsgiApi.EPackage_Registry.Reference.DELEGATE_TARGET,
				String.format("(&%s%s)", EmfOsgiApi.EPackage_Registry.Reference.DELEGATE_TARGET_DEFAULT, customTarget));

		// Configure and obtain the service
		EPackage.Registry registry = configurationHelper(EPackage.Registry.class, EmfOsgiApi.EPackage_Registry.PID,
				ePackageRegistryProps, timeout);

		// Register EPackage
		EPackage ePackage = mock(EPackage.class);
		final Hashtable<String, Object> ePackageProps = new Hashtable<>(customProps);
		final String ePackageNsUri = "epackage.ns.uri";
		ePackageProps.put(Property.NS_URI, ePackageNsUri);

		assertNull(registry.getEPackage(ePackageNsUri));
		serviceRegistrations.add(CONTEXT.registerService(EPackage.class, ePackage, ePackageProps));

		// Register EPackage.Descriptor
		EPackage.Descriptor descriptor = mock(EPackage.Descriptor.class);
		when(descriptor.getEPackage()).thenReturn(ePackage);
		final Hashtable<String, Object> descriptorProps = new Hashtable<>(customProps);
		final String descriptorNsUri = "descriptor.ns.uri";
		descriptorProps.put(Property.NS_URI, descriptorNsUri);

		assertNull(registry.getEPackage(descriptorNsUri));
		serviceRegistrations.add(CONTEXT.registerService(EPackage.Descriptor.class, descriptor, descriptorProps));

		// Register delegate EPackage.Registry
		EPackage.Registry delegateRegistry = mock(EPackage.Registry.class);
		final Hashtable<String, Object> delegateRegistryProps = new Hashtable<>(customProps);
		delegateRegistryProps.put("delegate", "true");
		// mock here instead of registering as a service
		EPackage delegatedEPackage = mock(EPackage.class);
		final String delegatedEPackageNsUri = "delegated.epackage.ns.uri";
		when(delegateRegistry.getEPackage(delegatedEPackageNsUri)).thenReturn(delegatedEPackage);

		assertNull(registry.getEPackage(delegatedEPackageNsUri));
		serviceRegistrations
				.add(CONTEXT.registerService(EPackage.Registry.class, delegateRegistry, delegateRegistryProps));

		// Note: these could fail if the services above aren't registered prior to call.
		// If so maybe guard with latches
		//
		// Verify
		assertEquals(ePackage, registry.getEPackage(ePackageNsUri));
		assertEquals(ePackage, registry.getEPackage(descriptorNsUri));
		assertEquals(delegatedEPackage, registry.getEPackage(delegatedEPackageNsUri));
	}
}
