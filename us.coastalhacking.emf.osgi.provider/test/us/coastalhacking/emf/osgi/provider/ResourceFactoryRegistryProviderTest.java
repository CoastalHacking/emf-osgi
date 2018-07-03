package us.coastalhacking.emf.osgi.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.junit.Test;

import us.coastalhacking.emf.osgi.api.EmfOsgiApi.Property;

public class ResourceFactoryRegistryProviderTest extends ResourceFactoryRegistryProvider {

	@Test
	public void shouldSetUriConverter() {
		ResourceFactoryRegistryProviderTest provider = new ResourceFactoryRegistryProviderTest();
		URIConverter expected = mock(URIConverter.class);
		provider.setUriConverter(expected);
		assertEquals(expected, provider.getURIConverter());
	}

	@Test
	public void shouldUnsetUriConverter() {
		ResourceFactoryRegistryProviderTest provider = new ResourceFactoryRegistryProviderTest();
		provider.unsetUriConverter(mock(URIConverter.class));
		assertNull(provider.getURIConverter());
	}

	@Test
	public void shouldSetDescriptor() {
		ResourceFactoryRegistryProvider provider = new ResourceFactoryRegistryProvider();
		Factory.Descriptor expected = mock(Factory.Descriptor.class);
		Map<String, Object> properties = new HashMap<>();
		String scheme = "someScheme";
		properties.put(Property.SCHEME, scheme);
		String extension = "someExtension";
		properties.put(Property.EXTENSION, extension);
		String type = "someType";
		properties.put(Property.CONTENT_TYPE, type);

		assertTrue(provider.getProtocolToFactoryMap().isEmpty());
		assertTrue(provider.getExtensionToFactoryMap().isEmpty());
		assertTrue(provider.getContentTypeToFactoryMap().isEmpty());
		provider.setDescriptor(expected, properties);
		assertEquals(1, provider.getProtocolToFactoryMap().size());
		assertEquals(1, provider.getExtensionToFactoryMap().size());
		assertEquals(1, provider.getContentTypeToFactoryMap().size());
		assertEquals(expected, provider.getProtocolToFactoryMap().get(scheme));
		assertEquals(expected, provider.getExtensionToFactoryMap().get(extension));
		assertEquals(expected, provider.getContentTypeToFactoryMap().get(type));
	}

	@Test
	public void shouldUnsetDescriptor() {
		ResourceFactoryRegistryProvider provider = new ResourceFactoryRegistryProvider();
		Factory.Descriptor descriptor = mock(Factory.Descriptor.class);
		Map<String, Object> properties = new HashMap<>();
		String scheme = "someScheme";
		properties.put(Property.SCHEME, scheme);
		String extension = "someExtension";
		properties.put(Property.EXTENSION, extension);
		String type = "someType";
		properties.put(Property.CONTENT_TYPE, type);

		provider.setDescriptor(descriptor, properties);
		provider.unsetDescriptor(descriptor, properties);
		assertTrue(provider.getProtocolToFactoryMap().isEmpty());
		assertTrue(provider.getExtensionToFactoryMap().isEmpty());
		assertTrue(provider.getContentTypeToFactoryMap().isEmpty());

	}

	@Test
	public void shouldSetFactory() {
		ResourceFactoryRegistryProvider provider = new ResourceFactoryRegistryProvider();
		Factory expected = mock(Factory.class);
		Map<String, Object> properties = new HashMap<>();
		String scheme = "someScheme";
		properties.put(Property.SCHEME, scheme);
		String extension = "someExtension";
		properties.put(Property.EXTENSION, extension);
		String type = "someType";
		properties.put(Property.CONTENT_TYPE, type);

		assertTrue(provider.getProtocolToFactoryMap().isEmpty());
		assertTrue(provider.getExtensionToFactoryMap().isEmpty());
		assertTrue(provider.getContentTypeToFactoryMap().isEmpty());
		provider.setFactory(expected, properties);
		assertEquals(1, provider.getProtocolToFactoryMap().size());
		assertEquals(1, provider.getExtensionToFactoryMap().size());
		assertEquals(1, provider.getContentTypeToFactoryMap().size());
		assertEquals(expected, provider.getProtocolToFactoryMap().get(scheme));
		assertEquals(expected, provider.getExtensionToFactoryMap().get(extension));
		assertEquals(expected, provider.getContentTypeToFactoryMap().get(type));
	}

	@Test
	public void shouldUnsetFactory() {
		ResourceFactoryRegistryProvider provider = new ResourceFactoryRegistryProvider();
		Factory factory = mock(Factory.class);
		Map<String, Object> properties = new HashMap<>();
		String scheme = "someScheme";
		properties.put(Property.SCHEME, scheme);
		String extension = "someExtension";
		properties.put(Property.EXTENSION, extension);
		String type = "someType";
		properties.put(Property.CONTENT_TYPE, type);

		provider.setFactory(factory, properties);
		provider.unsetFactory(factory, properties);
		assertTrue(provider.getProtocolToFactoryMap().isEmpty());
		assertTrue(provider.getExtensionToFactoryMap().isEmpty());
		assertTrue(provider.getContentTypeToFactoryMap().isEmpty());

	}

}
