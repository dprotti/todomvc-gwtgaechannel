
### Eclipse
Don't be scared, is not that bad as it appears from the length of this section :).

For running from Eclipse (requires [Google Eclipse plugin](https://developers.google.com/eclipse/)):

- _(In command line @ todomvc)_ mvn package
- _(In Eclipse)_ File -> Import... -> Existing Maven Projects (if your Eclipse does not supports Maven, choose "Existing Projects into Workspace")
- **ONLY IF NEEDED** setup GWT container. Read below section.
- Right click gwtgaechanneltodo project -> Google -> GWT Compile -> Click Compile

Compilation should succeed with a message similar to:

    Linking into /home/duilio/Devel/eclipse-workspaces/a/todomvc/gwt_gaechannel/src/main/webapp/gwtgaechanneltodo
    Link succeeded
    Compilation succeeded -- 27.010s

- **ONLY IF NEEDED** setup GAE container. Read after below section.
- Right click gwtgaechanneltodo project -> Run As -> Web Application

Eclipse Console window should display a message similar to:

    Feb 19, 2014 9:43:13 PM com.google.appengine.tools.development.AbstractServer startup
    INFO: Server default is running at http://localhost:8888/
    Feb 19, 2014 9:43:13 PM com.google.appengine.tools.development.AbstractServer startup
    INFO: The admin console is running at http://localhost:8888/_ah/admin
    Feb 19, 2014 3:43:13 PM com.google.appengine.tools.development.DevAppServerImpl start
    INFO: Dev App Server is now running

- _(In command line)_ ./create-manifest-for-eclipse.sh

Then point a browser to http://127.0.0.1:8888 to see the app running.

#### Setup GWT Container

If you have never used GWT with Eclipse or you use a version older than 2.6.0 you may need to setup a GWT container for Eclipse.

Do this on command line:

1. mkdir gwt_gaechannel/sdks
2. cd gwt_gaechannel/sdks
3. wget [https://google-web-toolkit.googlecode.com/files/gwt-2.6.0.zip](https://google-web-toolkit.googlecode.com/files/gwt-2.6.0.zip)
4. unzip gwt-2.6.0.zip

Then on Eclipse:

- Right click gwtgaechanneltodo project -> Properties -> Google -> Web Toolkit
- Click on "Configure SDKs..."
- Click "Add..." and select as Installation Directory the one created in step 2 (`gwt_gaechannel/sdks/gwt-2.6.0`)
- Click OK. GWT container 2.6.0 should display on the list of SDKs. Select it.
- Click OK again. You are done.

#### Setup GAE Container

If you have never used App Engine with Eclipse or you use a version older than 1.7.7 you may need to setup a GAE container for Eclipse.

Do this on command line:

1. mkdir gwt_gaechannel/sdks
2. cd gwt_gaechannel/sdks
3. wget [https://googleappengine.googlecode.com/files/appengine-java-sdk-1.7.7.zip](https://googleappengine.googlecode.com/files/appengine-java-sdk-1.7.7.zip)
4. unzip appengine-java-sdk-1.7.7.zip

Then on Eclipse:

- Right click gwtgaechanneltodo project -> Properties -> Google -> App Engine
- Click on "Configure SDKs..."
- Click "Add..." and select as Installation Directory `TODO-check-this/gwt_gaechannel/sdks/appengine-java-sdk-1.7.7`
- Click OK. App Engine 1.7.7 should display on the list of SDKs. Select it.
- Click OK again. You are done.
