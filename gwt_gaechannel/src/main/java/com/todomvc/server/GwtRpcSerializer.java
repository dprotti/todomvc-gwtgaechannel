/*!! server */
package com.todomvc.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;

/**
 * Serialize Java objects using GWT RPC serialization mechanisms.
 */
/*!
  Serialize Java objects using GWT RPC serialization mechanisms. Based on
  [ideas](http://www.youtube.com/watch?v=wWhd9ZwvCyw&t=29m44s) from Justin Fagnani.
 */
public class GwtRpcSerializer {
    private static final Logger logger = Logger
            .getLogger(GwtRpcSerializer.class.getName());

    /*!
      Use the manifest.txt file created by rpc-manifest-builder tool.
      manifest.txt contains for each `RemoteService` the name of its corresponding policy file.
     */
    private static final String RPC_MANIFEST_BASE_PATH   = "WEB-INF/deploy/gwtgaechanneltodo/rpcPolicyManifest-aux";
    private static final String RPC_MANIFEST_FILENAME    = "manifest.txt";
    private static final String RPC_MANIFESTS_BASE_PATH  = "gwtgaechanneltodo";

    private final Supplier<Map<String, SerializationPolicy>> serviceToSerializationPolicySupplier;

    public GwtRpcSerializer() throws Exception {
        /*!
          Lazily instantiate the map of serialization policies to keep the
          construction of this object cheap and prevent performance issues when
          starting up a server on AppEngine.
         */
        serviceToSerializationPolicySupplier = Suppliers
                .memoize(new Supplier<Map<String, SerializationPolicy>>() {
                    @Override
                    public Map<String, SerializationPolicy> get() {
                        return loadSerializationPolicies();
                    }
                });
    }

    /**
     * Serializes object in the GWT RPC format.
     * 
     * @param o
     *            The object to serialize
     * @param method
     *            The service method that returns objects of the same type than object {@code o}.
     * @return A string with the serialized representation of the object.
     */
    /*!
      The public GWT API for serializing objects is the static method `RPC.encodeResponseForSuccess()`.
      It requires a reference to a `java.lang.reflect Method` whose return value has the same type than
      the type of object `o`.
      RPC.encodeResponseForSuccess() does not invoke the method, it require it simply to determine
      the declared return type.
     */
    public String serialize(Object o, Method method) {
        String methodClassName = method.getDeclaringClass().getName();
        Map<String, SerializationPolicy> serviceToSerializationPolicy = serviceToSerializationPolicySupplier
                .get();
        /*!
           Get the serialization policy for the method whose return type equals the type
           of the object to be serialized.
         */
        SerializationPolicy serializationPolicy = serviceToSerializationPolicy
                .get(methodClassName);
        if (serializationPolicy == null) {
            throw new RuntimeException(
                    "Could not find serialization policy for " + methodClassName);
        }
        try {
            /*! Ask GWT to serialize the object and return such a string representation. */
            return RPC.encodeResponseForSuccess(method, o, serializationPolicy);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the serialization policies for all services by reading the RPC
     * manifest file to find correct serialization policy file for each service.
     * 
     * The manifest file format is expected to be:
     * 
     * # comment
     * classname, rpc_strong_file_name
     */
    /*!
      Loads the serialization policies, one for each RemoteService, from the auxiliary manifest.txt
      built by rpc-manifest-builder tool.
     */
    private Map<String, SerializationPolicy> loadSerializationPolicies() {
        BufferedReader reader = null;
        try {
            InputStream in = new FileInputStream(new File(RPC_MANIFEST_BASE_PATH + "/" + RPC_MANIFEST_FILENAME));
            reader = new BufferedReader(new InputStreamReader(in));
            Map<String, SerializationPolicy> serializationPolicies = Maps.newHashMap();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split(",");
                String service = parts[0].trim();
                String strongName = parts[1].trim();
                serializationPolicies.put(
                        service,
                        loadSerializationPolicy(RPC_MANIFESTS_BASE_PATH, strongName));
            }
            return serializationPolicies;
        } catch (IOException ioe) {
            logger.severe("Error loading manifest.txt - collaborative editing will not work. Reason: "
                    + ioe.getLocalizedMessage());
            throw new RuntimeException(ioe);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }

    private SerializationPolicy loadSerializationPolicy(String path,
            String strongName) throws IOException {
        logger.fine("Loading serialization policy " + path + "/" + strongName + "...");
        InputStream in = new FileInputStream(new File(path, strongName));

        List<ClassNotFoundException> exceptions = Lists.newArrayList();
        try {
            SerializationPolicy policy = SerializationPolicyLoader
                    .loadFromStream(in, exceptions);
            if (!exceptions.isEmpty()) {
                logger.warning("Error finding serializable classes on server classpath: "
                        + exceptions);
            }
            return policy;
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing RPC file : " + strongName, e);
        }
    }
}
