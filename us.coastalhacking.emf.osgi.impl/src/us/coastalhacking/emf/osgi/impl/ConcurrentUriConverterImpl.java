package us.coastalhacking.emf.osgi.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.emf.common.util.DelegatingEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;

/*
 * Members set outside of ctor since they need to be available to super during its ctor
 */
public class ConcurrentUriConverterImpl extends ExtensibleURIConverterImpl {

	// The URI and content handler members below cannot be initialized here nor in
	// this ctor
	protected List<URIHandler> delegateUriHandlers;
	protected List<ContentHandler> delegateContentHandlers;
	protected EList<URIHandler> uriHandlers;
	protected EList<ContentHandler> contentHandlers;
	protected Map<URI, URI> uriMap = new ConcurrentHashMap<>();

	public ConcurrentUriConverterImpl() {
		// Bypass global
		super(Collections.emptyList(), Collections.emptyList());
	}

	protected class URIHandlerList extends DelegatingEList<URIHandler> {

		public URIHandlerList() {
			delegateUriHandlers = new CopyOnWriteArrayList<>();
		}

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean canContainNull() {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * Similar situation as with class. Set here since called during base ctor.
		 * 
		 * @see org.eclipse.emf.common.util.DelegatingEList#delegateList()
		 */
		@Override
		protected List<URIHandler> delegateList() {
			return delegateUriHandlers;
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * Setting needs to occur in getter since super calls getter during its ctor.
	 * Since it's called during the base ctor should not need to be atomic.
	 * 
	 * @see
	 * org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl#getURIHandlers
	 * ()
	 */
	@Override
	public EList<URIHandler> getURIHandlers() {
		if (uriHandlers == null) {
			uriHandlers = new URIHandlerList();
		}
		return uriHandlers;
	}

	@Override
	public URIHandler getURIHandler(URI uri) {
		return getURIHandlers().stream().filter(u -> u.canHandle(uri)).findFirst().get();
	}

	protected class ContentHandlerList extends DelegatingEList<ContentHandler> {

		private static final long serialVersionUID = 1L;

		public ContentHandlerList() {
			delegateContentHandlers = new CopyOnWriteArrayList<>();
		}

		@Override
		protected boolean canContainNull() {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * Similar situation as with class. Set here since called during base ctor.
		 * 
		 * @see org.eclipse.emf.common.util.DelegatingEList#delegateList()
		 */
		@Override
		protected List<ContentHandler> delegateList() {
			return delegateContentHandlers;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Setting needs to occur in getter since super calls getter during its ctor.
	 * Since it's called during the base ctor should not need to be atomic.
	 *
	 * @see org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl#
	 * getContentHandlers()
	 */
	@Override
	public EList<ContentHandler> getContentHandlers() {
		if (contentHandlers == null) {
			contentHandlers = new ContentHandlerList();
		}
		return contentHandlers;
	}

	/*
	 * Not a drop-in replacement: no prefix support nor global registry delegation.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl#getURIMap()
	 */
	@Override
	public Map<URI, URI> getURIMap() {
		return uriMap;
	}

}
