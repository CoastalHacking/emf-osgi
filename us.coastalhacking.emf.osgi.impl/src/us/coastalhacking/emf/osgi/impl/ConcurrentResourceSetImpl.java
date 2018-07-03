/**
 * Copyright (c) 2018 Coastal Hacking
 * Copyright (c) 2002-2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Most of DelegatingResourcesEList
 */
package us.coastalhacking.emf.osgi.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.DelegatingEList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.DelegatingNotifyingInternalEListImpl;
import org.eclipse.emf.ecore.util.InternalEList;

public class ConcurrentResourceSetImpl extends ResourceSetImpl {

	protected class DelegatingResourcesEList extends DelegatingNotifyingInternalEListImpl<Resource>
			implements InternalEList<Resource> {

		@Override
		protected List<Resource> delegateList() {
			return delegateResources;
		}

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean isNotificationRequired() {
			return ConcurrentResourceSetImpl.this.eNotificationRequired();
		}

		@Override
		public Object getNotifier() {
			return ConcurrentResourceSetImpl.this;
		}

		@Override
		public int getFeatureID() {
			return RESOURCE_SET__RESOURCES;
		}

		@Override
		protected boolean useEquals() {
			return false;
		}

		@Override
		protected boolean hasInverse() {
			return true;
		}

		@Override
		protected boolean isUnique() {
			return true;
		}

		@Override
		protected NotificationChain inverseAdd(Resource resource, NotificationChain notifications) {
			Resource.Internal internalResource = (Resource.Internal) resource;
			return internalResource.basicSetResourceSet(ConcurrentResourceSetImpl.this, notifications);
		}

		@Override
		protected NotificationChain inverseRemove(Resource resource, NotificationChain notifications) {
			Resource.Internal internalResource = (Resource.Internal) resource;
			Map<URI, Resource> map = getURIResourceMap();
			if (map != null) {
				for (Iterator<Resource> i = map.values().iterator(); i.hasNext();) {
					if (internalResource == i.next()) {
						i.remove();
					}
				}
			}
			return internalResource.basicSetResourceSet(null, notifications);
		}

		@Override
		public boolean contains(Object object) {
			return size() <= 4 ? super.contains(object)
					: object instanceof Resource
							&& ((Resource) object).getResourceSet() == ConcurrentResourceSetImpl.this;
		}
	}

	// Mask & declare as volatile
	protected volatile URIConverter converter;
	protected volatile EPackage.Registry ePackageRegistry;
	protected volatile Factory.Registry factoryRegistry;

	// TODO
//	org.eclipse.emf.ecore.resource.ResourceSet.getLoadOptions()

	protected List<AdapterFactory> delegateAdapterFactories;
	protected List<Resource> delegateResources;

	public ConcurrentResourceSetImpl() {
		delegateAdapterFactories = new CopyOnWriteArrayList<>();
		delegateResources = new CopyOnWriteArrayList<>();
		loadOptions = new ConcurrentHashMap<>();
		uriResourceMap = new ConcurrentHashMap<>();

		adapterFactories = new DelegatingEList<AdapterFactory>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected List<AdapterFactory> delegateList() {
				return delegateAdapterFactories;
			}

			@Override
			protected boolean useEquals() {
				return false;
			};

			@Override
			protected boolean isUnique() {
				return true;
			};
		};

		resources = new DelegatingResourcesEList();
	}

	@Override
	public void setURIConverter(URIConverter uriConverter) {
		this.converter = uriConverter;
	}

	@Override
	public URIConverter getURIConverter() {
		return this.converter;
	}

	@Override
	public void setPackageRegistry(EPackage.Registry packageRegistry) {
		this.ePackageRegistry = packageRegistry;
	}

	@Override
	public Registry getPackageRegistry() {
		return ePackageRegistry;
	}

	@Override
	public void setResourceFactoryRegistry(Factory.Registry factoryRegistry) {
		this.factoryRegistry = factoryRegistry;
	}

	@Override
	public Factory.Registry getResourceFactoryRegistry() {
		return factoryRegistry;
	}

}