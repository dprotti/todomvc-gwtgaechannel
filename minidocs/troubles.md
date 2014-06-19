
## Troubleshooting

> I edit in one browser, then other browsers do not automatically reflect the changes.

Look at server console output for error messages of the kind:

    java.io.FileNotFoundException: WEB-INF\deploy\gwtgaechanneltodo\rpcPolicyManifest-aux\manifest.txt (The system cannot find the path specified)
    
If you find them, then you need to re-create manifest.txt. Do this by running:

    ./create-manifest-for-maven.sh

or else

    ./create-manifest-for-eclipse.sh
    
whatever applies to your case.
