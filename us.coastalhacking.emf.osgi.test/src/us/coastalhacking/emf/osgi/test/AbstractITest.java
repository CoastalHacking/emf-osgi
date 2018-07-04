package us.coastalhacking.emf.osgi.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractITest {

	protected static final BundleContext CONTEXT = FrameworkUtil.getBundle(UriConverterProviderITest.class)
			.getBundleContext();

	protected static ConfigurationAdmin configAdmin;

	protected final List<ServiceTracker<?, ?>> serviceTrackers = new CopyOnWriteArrayList<>();
	protected static final List<ServiceTracker<?, ?>> staticServiceTrackers = new CopyOnWriteArrayList<>();
	protected final List<ServiceRegistration<?>> serviceRegistrations = new CopyOnWriteArrayList<>();
	protected final List<Configuration> configurations = new CopyOnWriteArrayList<>();
	protected final Map<String, CompletableFuture<Void>> configurationFutures = new ConcurrentHashMap<>();

	protected static final long timeout = 1000; // milliseconds;
	protected static final Hashtable<String, Object> customProps = new Hashtable<>();
	protected static final String customPropertyKey = "some.custom.property";
	protected static final String customPropertyValue = "some.custom.value";
	protected static final String customTarget = String.format("(%s=%s)", customPropertyKey, customPropertyValue);

	protected static void beforeClass() throws Exception {
		customProps.put(customPropertyKey, customPropertyValue);
		configAdmin = staticServiceTrackerHelper(new ServiceTracker<>(CONTEXT, ConfigurationAdmin.class, null),
				timeout);
	}

	protected static void afterClass() throws Exception {
		configAdmin = null;
		staticServiceTrackers.forEach(st -> {
			st.close();
		});
		staticServiceTrackers.clear();

	}

	public void before() throws Exception {
		assertTrue(serviceTrackers.isEmpty());
		assertTrue(serviceRegistrations.isEmpty());
		assertTrue(configurations.isEmpty());

		// Register our per-test listener
		serviceRegistrations.add(CONTEXT.registerService(org.osgi.service.cm.ConfigurationListener.class,
				new TestCaseConfigurationListener(), null));

	}

	public void after() throws Exception {
		// First delete the configurations
		configurations.forEach(c -> {
			try {
				c.delete();
			} catch (Exception e) {

				e.printStackTrace();
			}
		});

		CompletableFuture<Void> combinedFuture = CompletableFuture
				.allOf(configurationFutures.values().stream().toArray(CompletableFuture[]::new));
		// block until all the configurations are deleted
		combinedFuture.get();
		configurations.clear();

		// Then delete the registrations. Reversing these results in the configuration
		// listener being deleted prematurely
		serviceRegistrations.forEach(sr -> {
			try {
				sr.unregister();
			} catch (IllegalArgumentException e) {
				// gobble
			}
		});
		serviceRegistrations.clear();
		serviceTrackers.forEach(st -> {
			st.close();
		});
		serviceTrackers.clear();

	}

	protected <T> T serviceTrackerHelper(ServiceTracker<?, T> st, long timeout) throws Exception {
		return serviceTrackerHelper(serviceTrackers, st, timeout);
	}

	protected static <T> T staticServiceTrackerHelper(ServiceTracker<?, T> st, long timeout) throws Exception {
		return serviceTrackerHelper(staticServiceTrackers, st, timeout);
	}

	protected static <T> T serviceTrackerHelper(List<ServiceTracker<?, ?>> trackers, ServiceTracker<?, T> st,
			long timeout) throws Exception {
		// Add before opening just in case opening throws an exception
		trackers.add(st);
		st.open();
		return st.waitForService(timeout);
	}

	protected String toFilter(Class<?> clazz, String servicePid, Map<String, Object> props) {
		return String.format("(&(%s=%s)%s)", Constants.SERVICE_PID, servicePid, props.entrySet().stream().map(
				es -> String.format("(%s=%s)", es.getKey(), weaklyEscapeForLdapSearchFilter((String) es.getValue())))
				.collect(Collectors.joining()));
	}

	// Don't use me in prod! I'm weak and just for testing...
	protected String weaklyEscapeForLdapSearchFilter(String unescaped) {
		final StringBuilder result = new StringBuilder(unescaped.length());
		unescaped.chars().forEach(i -> {
			char c = (char) i;
			switch (c) {
			case '(':
				result.append("\\(");
				break;
			case ')':
				result.append("\\)");
				break;
			default:
				result.append(c);
				break;
			}
		});
		return result.toString();
	}

	protected <S> S configurationHelper(Class<S> clazz, String factoryPid, Map<String, Object> props, long timeout)
			throws Exception {
		// Can be null if the derived test case did not declare and properly annotate
		// its before, after, beforeClass, and afterClass methods
		assertNotNull(configAdmin);
		// Create the configuration
		final Configuration configuration = configAdmin.createFactoryConfiguration(factoryPid, "?");
		configurations.add(configuration);
		configurationFutures.put(configuration.getPid(), new CompletableFuture<>());
		// Updating it with the passed-in properties
		configuration.update(new Hashtable<>(props));
		// Strictly return the specific service, ensuring the desired properties are
		// also present
		final String filter = toFilter(clazz, configuration.getPid(), props);
		return serviceTrackerHelper(new ServiceTracker<>(CONTEXT, CONTEXT.createFilter(filter), null), timeout);
	}

	protected class TestCaseConfigurationListener implements ConfigurationListener {

		@Override
		public void configurationEvent(ConfigurationEvent event) {
			if (event.getType() == ConfigurationEvent.CM_DELETED) {
				configurationFutures.get(event.getPid()).complete(null);
			}
		}

	}
}
