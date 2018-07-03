package us.coastalhacking.emf.osgi.impl;

import static org.junit.Assert.*;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Test;
import static org.mockito.Mockito.*;



public class ConcurrentResourceSetImplTest extends ConcurrentResourceSetImpl {

	@Test
	public void shouldGetAdapterFactory() {
		AdapterFactory expected = mock(AdapterFactory.class);
		ConcurrentResourceSetImpl impl = new ConcurrentResourceSetImpl();
		assertTrue(impl.delegateAdapterFactories.isEmpty());
		impl.getAdapterFactories().add(expected);
		assertEquals(expected, impl.delegateAdapterFactories.get(0));
	}

	@Test
	public void shouldRemoveAdapterFactory() {
		AdapterFactory factory = mock(AdapterFactory.class);
		ConcurrentResourceSetImpl impl = new ConcurrentResourceSetImpl();
		impl.getAdapterFactories().add(factory);
		impl.getAdapterFactories().remove(factory);
		assertTrue(impl.delegateAdapterFactories.isEmpty());
	}

	@Test
	public void shouldGetResource() {
		Resource expected = mock(Resource.class, withSettings().extraInterfaces(Resource.Internal.class));
		ConcurrentResourceSetImpl impl = new ConcurrentResourceSetImpl();
		assertTrue(impl.delegateResources.isEmpty());
		impl.getResources().add(expected);
		assertEquals(expected, impl.delegateResources.get(0));
	}
	
	@Test
	public void shouldRemoveResource() {
		Resource expected = mock(Resource.class, withSettings().extraInterfaces(Resource.Internal.class));
		ConcurrentResourceSetImpl impl = new ConcurrentResourceSetImpl();
		impl.getResources().add(expected);
		impl.getResources().remove(expected);
		assertTrue(impl.delegateResources.isEmpty());
	}
}
