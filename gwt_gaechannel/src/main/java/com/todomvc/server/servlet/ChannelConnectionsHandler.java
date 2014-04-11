/*!! server.servlet */
package com.todomvc.server.servlet;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.todomvc.shared.service.CommandService;

/**
 * Handles GAE channel connections with client browsers.
 *
 * See https://developers.google.com/appengine/docs/java/channel/overview#Tracking_Client_Connections_and_Disconnections
 *
 * @author Duilio Protti
 */
/*!
  Handles GAE channel connections with client browsers.

  Check this [doc](https://developers.google.com/appengine/docs/java/channel/overview#Tracking_Client_Connections_and_Disconnections).
 */
@Singleton
@SuppressWarnings("serial")
public class ChannelConnectionsHandler extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ChannelConnectionsHandler.class.getName());

    private static final String CONNECTED_PATH    = "/_ah/channel/connected/";
    private static final String DISCONNECTED_PATH = "/_ah/channel/disconnected/";

    /*!
      Client ids are created by
      the [CommandController](${basePath}/java/com/todomvc/client/command/CommandController.java.html).
     */
    private static final Pattern CLIENT_ID_PATTERN = Pattern.compile(".*:\\d*:\\d*$");

    private final CommandService commandService;

    @Inject
    public ChannelConnectionsHandler(CommandService commandService) {
        this.commandService = checkNotNull(commandService);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doPost(req, resp);
        if (CONNECTED_PATH.equals(req.getServletPath())) {
            logger.info("channel established with " + req.getRemoteHost());
        } else if (DISCONNECTED_PATH.equals(req.getServletPath())) {
            String clientId = extractClientId(req);
            if (clientId != null) {
                commandService.closeChannel(clientId);
                logger.info("channel " + clientId + " with " + req.getRemoteHost() + " closed");
            } else {
                logger.info("channel with " + req.getRemoteHost() + " closed");
            }
        }
    }

    /*!
      Try to extract from the body of the request a client id expected to come
      with a format similar to:
      
          --+++
          Content-Disposition: form-data; name="from"
      
          83844A38-070A-4912-89B2-66312D358924:273200543:1397195924827
          --+++
     */
    @Nullable
    private String extractClientId(HttpServletRequest request) {
        String clientId = null;
        try {
            BufferedReader reader = request.getReader();
            String line = reader.readLine();
            while (line != null) {
                // this will create some Matcher objects that will have to be garbage
                // collected. they won't be many so we assume the cost.
                if (CLIENT_ID_PATTERN.matcher(line).matches()) {
                    clientId = line;
                    break;
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            logger.warning("error extracting cliend id from request: " + e.getLocalizedMessage());
        }
        return clientId;
    }

}
