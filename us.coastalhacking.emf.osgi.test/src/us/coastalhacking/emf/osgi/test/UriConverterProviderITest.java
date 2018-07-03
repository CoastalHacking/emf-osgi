package us.coastalhacking.emf.osgi.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.Hashtable;

import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;
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
public class UriConverterProviderITest extends AbstractITest {

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
	public void shouldGetDefaultUriConverter() throws Exception {
		URIConverter uriConverter = serviceTrackerHelper(new ServiceTracker<>(CONTEXT, URIConverter.class, null), 1000);
		assertNotNull(uriConverter);
		assertEquals(EmfOsgiApi.URIConverter.PID, uriConverter.toString());
	}

	@Test
	public void shouldConfigureAndRegisterUriConverter() throws Exception {

		// Register URIHandler
		URIHandler uriHandler = mock(URIHandler.class);
		serviceRegistrations.add(CONTEXT.registerService(URIHandler.class, uriHandler, customProps));

		// Register ContentHandler
		ContentHandler contentHandler = mock(ContentHandler.class);
		serviceRegistrations.add(CONTEXT.registerService(ContentHandler.class, contentHandler, customProps));

		// Strictly target the URIConverter
		final Hashtable<String, Object> uriConverterProps = new Hashtable<>(customProps);
		uriConverterProps.put(EmfOsgiApi.URIConverter.Reference.URI_HANDLERS_TARGET, customTarget);
		uriConverterProps.put(EmfOsgiApi.URIConverter.Reference.CONTENT_HANDLERS_TARGET, customTarget);

		// Configure and obtain the service
		URIConverter uriConverter = configurationHelper(URIConverter.class, EmfOsgiApi.URIConverter.PID, customProps,
				timeout);
		
		// Verify
		assertNotNull(uriConverter);
		assertNotEquals(EmfOsgiApi.URIConverter.PID, uriConverter.toString());
		assertEquals(uriHandler, uriConverter.getURIHandlers().get(0));
		assertEquals(contentHandler, uriConverter.getContentHandlers().get(0));
	}

}