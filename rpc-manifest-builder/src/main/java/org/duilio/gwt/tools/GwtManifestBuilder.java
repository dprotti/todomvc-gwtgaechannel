package org.duilio.gwt.tools;

import static java.io.File.separator;
import static java.lang.String.format;
import static java.lang.System.err;
import static java.lang.System.exit;
import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GwtManifestBuilder {

    private static final Pattern SERVICE_PATTERN = Pattern
            .compile("serviceClass: (([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*)");
    private static final Pattern GWT_DOT_RPC_FILE_PATTERN = Pattern
            .compile("path: (.*\\.gwt\\.rpc)");

    private final String rpcPolicyManifestPath;
    // serviceClass name -> gwt.rpc policy file
    private final Map<String, String> policies;
    private boolean policiesLoaded;

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 1) {
            err.println("Error: please provide path to 'rpcPolicyManifest' folder");
            exit(1);
        }
        String rpcPolicyManifestPath = args[0];
        new GwtManifestBuilder(rpcPolicyManifestPath).loadPoliciesFromManifestsFolder()
                .consolidatePoliciesIntoAuxiliaryManifestTxt();
    }

    public GwtManifestBuilder(String rpcPolicyManifestPath) {
        this.rpcPolicyManifestPath = rpcPolicyManifestPath;
        policies = new HashMap<String, String>();
        policiesLoaded = false;
    }

    public GwtManifestBuilder loadPoliciesFromManifestsFolder() {
        String manifestsPath = rpcPolicyManifestPath + separator + "manifests";
        out.println("Opening folder " + manifestsPath);
        File rpcPolicyManifestFolder = new File(manifestsPath);
        if (rpcPolicyManifestFolder == null || rpcPolicyManifestFolder.listFiles() == null) {
            System.err.println("Error: cannot open folder " + manifestsPath);
            exit(1);
        }
        for (File rpcFile : rpcPolicyManifestFolder.listFiles()) {
            out.println("Found rpc file " + rpcFile.getName());
            extractServiceClassAndSerializationPolicyFilename(rpcFile);
        }
        policiesLoaded = true;
        return this;
    }

    /*
     * Expects file content similar to:
     * 
     * # Comment (optional) serviceClass: org.example.my.app.MyServiceClass
     * path: 04AF7B2735E5EF5EE28C8D70BBC698C0.gwt.rpc
     */
    private void extractServiceClassAndSerializationPolicyFilename(File fromFile) {
        String line;
        String className = null;
        String policyFilename = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fromFile));
            while ((line = reader.readLine()) != null
                    && (className == null || policyFilename == null)) {
                if (line.startsWith("#")) {
                    continue;
                }
                Matcher serviceMatcher = SERVICE_PATTERN.matcher(line);
                if (serviceMatcher.matches()) {
                    className = serviceMatcher.group(1);
                }
                Matcher policyFileMatcher = GWT_DOT_RPC_FILE_PATTERN.matcher(line);
                if (policyFileMatcher.matches()) {
                    policyFilename = policyFileMatcher.group(1);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            fatalFileError("opening file", fromFile, e);
        } catch (IOException e) {
            fatalFileError("reading from file", fromFile, e);
        }

        if (className == null) {
            throw new RuntimeException("Cannot find 'serviceClass:' value in file " + fromFile);
        }
        if (policyFilename == null) {
            throw new RuntimeException("Cannot find 'path:' value in file " + fromFile);
        }
        policies.put(className, policyFilename);
    }

    public GwtManifestBuilder consolidatePoliciesIntoAuxiliaryManifestTxt() {
        if (!policiesLoaded) {
            throw new IllegalStateException(
                    "Policies not loaded. Did you call loadPoliciesFromManifestsFolder()?");
        }
        File rpcPolicyManifestFolder = new File(rpcPolicyManifestPath);
        String auxiliaryFolderPath = rpcPolicyManifestFolder.getParent() + separator
                + "rpcPolicyManifest-aux";
        File auxiliaryFolder = new File(auxiliaryFolderPath);
        if (!auxiliaryFolder.isDirectory()) {
            if (!auxiliaryFolder.mkdir()) {
                throw new RuntimeException(format("Cannot create auxiliary folder %s",
                        rpcPolicyManifestPath));
            }
        }
        File manifestTxt = new File(auxiliaryFolderPath, "manifest.txt");
        try {
            if (!manifestTxt.exists()) {
                manifestTxt.createNewFile();
            }
        } catch (IOException e) {
            fatalFileError("creating file", manifestTxt, e);
        }
        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(manifestTxt.getAbsoluteFile()));
            for (Entry<String, String> entry : policies.entrySet()) {
                String className = entry.getKey();
                String policyFilename = entry.getValue();
                out.println(format("Adding '%s, %s' to manifest.txt", className, policyFilename));
                writer.write(format("%s, %s\n", className, policyFilename));
            }
            out.println("Closing file " + manifestTxt.getAbsolutePath());
            writer.close();
        } catch (IOException e) {
            fatalFileError("opening output file", manifestTxt, e);
        }
        return this;
    }

    private void fatalFileError(String action, File file, Exception e) {
        String errorMessage = format("Error %s %s", action, file.getAbsolutePath());
        throw new RuntimeException(errorMessage, e);
    }
}
