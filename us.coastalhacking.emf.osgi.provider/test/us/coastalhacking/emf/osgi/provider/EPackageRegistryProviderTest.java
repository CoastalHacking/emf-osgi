package us.coastalhacking.emf.osgi.provider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.junit.Test;
import org.osgi.service.log.LogService;

import us.coastalhacking.emf.osgi.api.EmfOsgiApi.Property;

@SuppressWarnings("serial")
public class EPackageRegistryProviderTest extends EPackageRegistryProvider {

	@Test
	public void shouldGetEPackageEFactory() {
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		EPackage descriptor = mock(EPackage.class);
		EFactory expected = mock(EFactory.class);
		when(descriptor.getEFactoryInstance()).thenReturn(expected);
		String key = "a.b.c";
		provider.put(key, descriptor);
		EFactory actual = provider.getEFactory(key);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldGetEPackageDescriptorEFactory() {
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		EPackage.Descriptor descriptor = mock(EPackage.Descriptor.class);
		EFactory expected = mock(EFactory.class);
		when(descriptor.getEFactory()).thenReturn(expected);
		String key = "a.b.c";
		provider.put(key, descriptor);
		EFactory actual = provider.getEFactory(key);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldNotGetEFactory() {
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		assertNull(provider.getEFactory("a.b.c"));
	}

	@Test
	public void shouldGetEPackage() {
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		EPackage expected = mock(EPackage.class);
		String key = "a.b.c";
		provider.put(key, expected);
		EPackage actual = provider.getEPackage(key);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldGetEPackageDescriptor() {
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		EPackage.Descriptor descriptor = mock(EPackage.Descriptor.class);
		EPackage expected = mock(EPackage.class);
		when(descriptor.getEPackage()).thenReturn(expected);
		String key = "a.b.c";
		provider.put(key, expected);
		EPackage actual = provider.getEPackage(key);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldNotGetEPackage() {
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		assertNull(provider.getEPackage("a.b.c"));
	}

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

	@Test
	public void shouldPutEPackageDescriptor() {
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		provider.put("a.b.c", mock(EPackage.Descriptor.class));
	}

	@Test
	public void shouldPutEPackage() {
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		provider.put("a.b.c", mock(EPackage.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPut() {
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		provider.logger = mock(LogService.class);
		provider.put("a.b.c", new Object());
	}

	@Test
	public void shouldPutAll() {
		Map<String, Object> map = new HashMap<>();
		map.put("a.b.c", mock(EPackage.class));
		map.put("c.b.a", mock(EPackage.Descriptor.class));
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		provider.putAll(map);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPutAll() {
		Map<String, Object> map = new HashMap<>();
		map.put("a.b.c", new Object());
		EPackageRegistryProvider provider = new EPackageRegistryProvider();
		provider.logger = mock(LogService.class);
		provider.putAll(map);
	}

}