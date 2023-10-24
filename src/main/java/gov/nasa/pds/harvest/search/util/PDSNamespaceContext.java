package gov.nasa.pds.harvest.search.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

import gov.nasa.pds.harvest.search.policy.Namespace;

/**
 * Class that provides support for handling namespaces in PDS4
 * data products.
 *
 * @author mcayanan
 *
 */
public class PDSNamespaceContext implements NamespaceContext {
    private Map<String, String> namespaces;

    /**
     * Constructor.
     *
     */
    public PDSNamespaceContext() {
        this.namespaces = new HashMap<String, String>();
        this.namespaces.put("xml", "http://www.w3.org/XML/1998/namespace");
    }

    /**
     * Constructor.
     *
     * @param namespaces A list of namespaces to support.
     */
    public PDSNamespaceContext(List<Namespace> namespaces) {
        this();
        for (Namespace ns : namespaces) {
            this.namespaces.put(ns.getPrefix(), ns.getUri());
        }
    }

    /**
     * Adds a namespace.
     *
     * @param namespace A namespace to support.
     */
    public void addNamespace(Namespace namespace) {
        this.namespaces.put(namespace.getPrefix(), namespace.getUri());
    }

    /**
     * Gets the namespace URI.
     *
     * @param prefix The prefix
     *
     * @return The URI to the given prefix. Returns the PDS namespace URI
     * if the given prefix is empty or null.
     */
    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null || "".equals(prefix)) {
            return "";
        } else {
            return namespaces.get(prefix);
        }
    }

    /**
     * Method not needed
     *
     */
    @Override
    public String getPrefix(String arg0) {
        // Method not necessary
        return null;
    }

    /**
     * Method not needed
     *
     */
    @Override
    public Iterator getPrefixes(String arg0) {
        // Method not necessary
        return null;
    }

}
