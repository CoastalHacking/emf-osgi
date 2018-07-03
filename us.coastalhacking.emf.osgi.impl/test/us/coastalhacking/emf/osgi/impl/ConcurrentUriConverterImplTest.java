package us.coastalhacking.emf.osgi.impl;

import static org.junit.Assert.*;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class ConcurrentUriConverterImplTest extends ConcurrentUriConverterImpl {

	// getURIHandler
	
	@Test
	public void shouldGetFromUriHandlers() {
		ConcurrentUriConverterImpl impl = new ConcurrentUriConverterImpl();
		URIHandler expected = mock(URIHandler.class);
		assertTrue(impl.delegateUriHandlers.isEmpty());
		impl.getURIHandlers().add(expected);
		assertEquals(expected, impl.delegateUriHandlers.get(0));
	}
	
	@Test
	public void shouldRemoveFromUriHandlers() {
		ConcurrentUriConverterImpl impl = new ConcurrentUriConverterImpl();
		URIHandler handler = mock(URIHandler.class);
		impl.getURIHandlers().add(handler);
		impl.getURIHandlers().remove(handler);
		assertTrue(impl.delegateUriHandlers.isEmpty());
	}

	@Test
	public void shouldGetFromContentHandlers() {
		ConcurrentUriConverterImpl impl = new ConcurrentUriConverterImpl();
		ContentHandler expected = mock(ContentHandler.class);
		assertTrue(impl.delegateContentHandlers.isEmpty());
		impl.getContentHandlers().add(expected);
		assertEquals(expected, impl.delegateContentHandlers.get(0));
	}
	
	@Test
	public void shouldRemoveFromContentHandlers() {
		ConcurrentUriConverterImpl impl = new ConcurrentUriConverterImpl();
		ContentHandler handler = mock(ContentHandler.class);
		impl.getContentHandlers().add(handler);
		impl.getContentHandlers().remove(handler);
		assertTrue(impl.delegateContentHandlers.isEmpty());
	}
	
	@Test
	public void shouldGetUriHandler() {
		ConcurrentUriConverterImpl impl = new ConcurrentUriConverterImpl();
		URIHandler expected = mock(URIHandler.class);
		URI uri = mock(URI.class);
		when(expected.canHandle(uri)).thenReturn(true);
		impl.getURIHandlers().add(expected);
		assertEquals(expected, impl.getURIHandler(uri));
	}
	

}
