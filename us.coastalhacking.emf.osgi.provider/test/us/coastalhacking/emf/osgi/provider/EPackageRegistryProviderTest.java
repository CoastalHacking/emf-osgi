package us.coastalhacking.emf.osgi.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.junit.Test;

import us.coastalhacking.emf.osgi.api.EmfOsgiApi.Property;

@SuppressWarnings("serial")
public class EPackageRegistryProviderTest extends EPackageRegistryProvider {

	@Test
	public void shouldGetUri() {
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		Map<String, Object> props = new HashMap<>();
		String expected = "a.b.c";
		props.put(Property.NS_URI, expected);
		String actual = provider.getUri(props).get();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldNotGetUri() {
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		Map<String, Object> props = new HashMap<>();
		assertFalse(provider.getUri(props).isPresent());
	}

	@Test
	public void shouldSetEPackage() {
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		EPackage expected = mock(EPackage.class);
		String nsUri = "a.b.c";
		Map<String, Object> props = new HashMap<>();
		props.put(Property.NS_URI, nsUri);
		assertEquals(0, provider.size());
		provider.setEPackage(expected, props);
		EPackage actual = (EPackage) provider.get(nsUri);
		assertTrue(provider.containsKey(nsUri));
		assertEquals(1, provider.size());
		assertEquals(expected, actual);
	}

	@Test
	public void shouldUnsetEPackage() {
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		EPackage ePackage = mock(EPackage.class);
		String nsUri = "a.b.c";
		Map<String, Object> props = new HashMap<>();
		props.put(Property.NS_URI, nsUri);
		provider.setEPackage(ePackage, props);
		provider.unsetEPackage(ePackage, props);
		assertFalse(provider.containsKey(nsUri));
		assertEquals(0, provider.size());
	}

	@Test
	public void shouldSetEPackageDescriptor() {
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		EPackage.Descriptor expected = mock(EPackage.Descriptor.class);
		String nsUri = "a.b.c";
		Map<String, Object> props = new HashMap<>();
		props.put(Property.NS_URI, nsUri);
		assertEquals(0, provider.size());
		provider.setEPackageDescriptor(expected, props);
		EPackage.Descriptor actual = (EPackage.Descriptor) provider.get(nsUri);
		assertTrue(provider.containsKey(nsUri));
		assertEquals(1, provider.size());
		assertEquals(expected, actual);

	}

	@Test
	public void shouldUnsetEPackageDescriptor() {
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		EPackage.Descriptor descriptor = mock(EPackage.Descriptor.class);
		String nsUri = "a.b.c";
		Map<String, Object> props = new HashMap<>();
		props.put(Property.NS_URI, nsUri);
		provider.setEPackageDescriptor(descriptor, props);
		provider.unsetEPackageDescriptor(descriptor, props);
		assertFalse(provider.containsKey(nsUri));
		assertEquals(0, provider.size());
	}

}