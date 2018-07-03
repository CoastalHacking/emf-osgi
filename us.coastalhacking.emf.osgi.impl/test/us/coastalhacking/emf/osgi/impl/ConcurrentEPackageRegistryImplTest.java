package us.coastalhacking.emf.osgi.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.junit.Test;

@SuppressWarnings("serial")
public class ConcurrentEPackageRegistryImplTest extends ConcurrentEPackageRegistryImpl {

	@Test
	public void shouldGetEPackageEFactory() {
		ConcurrentEPackageRegistryImpl impl = new ConcurrentEPackageRegistryImpl();
		EPackage ePackage = mock(EPackage.class);
		EFactory expected = mock(EFactory.class);
		when(ePackage.getEFactoryInstance()).thenReturn(expected);
		String key = "a.b.c";
		impl.put(key, ePackage);
		EFactory actual = impl.getEFactory(key);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldGetEPackageDescriptorEFactory() {
		ConcurrentEPackageRegistryImpl impl = new ConcurrentEPackageRegistryImpl();
		EPackage.Descriptor descriptor = mock(EPackage.Descriptor.class);
		EFactory expected = mock(EFactory.class);
		when(descriptor.getEFactory()).thenReturn(expected);
		String key = "a.b.c";
		impl.put(key, descriptor);
		EFactory actual = impl.getEFactory(key);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldNotGetEFactory() {
		ConcurrentEPackageRegistryImpl impl = new ConcurrentEPackageRegistryImpl();
		assertNull(impl.getEFactory("a.b.c"));
	}

	@Test
	public void shouldSetAndGetDelegateEFactory() {
		EFactory expected = mock(EFactory.class);
		String key = "a.b.c";
		EPackage.Registry delegate = mock(EPackage.Registry.class);
		when(delegate.getEFactory(key)).thenReturn(expected);
		ConcurrentEPackageRegistryImpl impl = new ConcurrentEPackageRegistryImpl();
		impl.setDelegateRegistry(delegate);
		EFactory actual = impl.getEFactory(key);
		assertEquals(expected, actual);
	}


	@Test
	public void shouldGetEPackage() {
		ConcurrentEPackageRegistryImpl impl = new ConcurrentEPackageRegistryImpl();
		EPackage expected = mock(EPackage.class);
		String key = "a.b.c";
		impl.put(key, expected);
		EPackage actual = impl.getEPackage(key);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldGetEPackageDescriptor() {
		ConcurrentEPackageRegistryImpl impl = new ConcurrentEPackageRegistryImpl();
		EPackage.Descriptor descriptor = mock(EPackage.Descriptor.class);
		EPackage expected = mock(EPackage.class);
		when(descriptor.getEPackage()).thenReturn(expected);
		String key = "a.b.c";
		impl.put(key, expected);
		EPackage actual = impl.getEPackage(key);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldSetAndGetDelegateEPackage() {
		EPackage expected = mock(EPackage.class);
		String key = "a.b.c";
		EPackage.Registry delegate = mock(EPackage.Registry.class);
		when(delegate.getEPackage(key)).thenReturn(expected);
		ConcurrentEPackageRegistryImpl impl = new ConcurrentEPackageRegistryImpl();
		impl.setDelegateRegistry(delegate);
		EPackage actual = impl.getEPackage(key);
		assertEquals(expected, actual);
	}
	
	@Test
	public void shouldNotGetEPackageOrEPackageDescriptor() {
		ConcurrentEPackageRegistryImpl impl = new ConcurrentEPackageRegistryImpl();
		assertNull(impl.getEPackage("a.b.c"));
	}

	@Test
	public void shouldPutEPackageDescriptor() {
		ConcurrentEPackageRegistryImpl impl = new ConcurrentEPackageRegistryImpl();
		impl.put("a.b.c", mock(EPackage.Descriptor.class));
	}

	@Test
	public void shouldPutEPackage() {
		ConcurrentEPackageRegistryImpl impl = new ConcurrentEPackageRegistryImpl();
		impl.put("a.b.c", mock(EPackage.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPut() {
		ConcurrentEPackageRegistryImpl impl = new ConcurrentEPackageRegistryImpl();
		impl.put("a.b.c", new Object());
	}

	@Test
	public void shouldPutAll() {
		Map<String, Object> map = new HashMap<>();
		map.put("a.b.c", mock(EPackage.class));
		map.put("c.b.a", mock(EPackage.Descriptor.class));
		ConcurrentEPackageRegistryImpl impl = new ConcurrentEPackageRegistryImpl();
		impl.putAll(map);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPutAll() {
		Map<String, Object> map = new HashMap<>();
		map.put("a.b.c", new Object());
		ConcurrentEPackageRegistryImpl impl = new ConcurrentEPackageRegistryImpl();
		impl.putAll(map);
	}

}