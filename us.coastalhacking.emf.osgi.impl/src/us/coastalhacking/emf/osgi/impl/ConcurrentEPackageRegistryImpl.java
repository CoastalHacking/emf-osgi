package us.coastalhacking.emf.osgi.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;

public class ConcurrentEPackageRegistryImpl extends ConcurrentHashMap<String, Object> implements EPackage.Registry {

	private static final long serialVersionUID = -5942228130618962255L;

	protected volatile EPackage.Registry delegateRegistry;

	public ConcurrentEPackageRegistryImpl() {
		super();
	}

	protected void setDelegateRegistry(EPackage.Registry delegateRegistry) {
		this.delegateRegistry = delegateRegistry;
	}

	protected EPackage.Registry getDelegateRegistry() {
		return delegateRegistry;
	}

	@Override
	public Object put(String key, Object value) {
		if (value instanceof EPackage || value instanceof EPackage.Descriptor) {
			return super.put(key, value);
		}
		throw new IllegalArgumentException(
				String.format("Object '%s' is not of type EPackage or EPackage.Descriptor via NS URI %s", value, key));
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		m.entrySet().stream().forEach(es -> put(es.getKey(), es.getValue()));
	}

	@Override
	public EPackage getEPackage(String nsURI) {
		final Object thing = get(nsURI);
		if (thing instanceof EPackage) {
			return (EPackage) thing;
		} else if (thing instanceof EPackage.Descriptor) {
			return ((EPackage.Descriptor) thing).getEPackage();
		}
		final EPackage.Registry delegateRegistry = getDelegateRegistry();
		return delegateRegistry == null ? null : delegateRegistry.getEPackage(nsURI);

	}

	@Override
	public EFactory getEFactory(String nsURI) {
		final Object thing = get(nsURI);
		if (thing instanceof EPackage) {
			return ((EPackage) thing).getEFactoryInstance();
		} else if (thing instanceof EPackage.Descriptor) {
			return ((EPackage.Descriptor) thing).getEFactory();
		}
		final EPackage.Registry delegateRegistry = getDelegateRegistry();
		return delegateRegistry == null ? null : delegateRegistry.getEFactory(nsURI);
	}

}
