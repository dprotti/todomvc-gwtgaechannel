/*!! server.servlet */
package com.todomvc.server.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doPost(req, resp);
        if (req.getServletPath().equals(CONNECTED_PATH)) {
            logger.info("channel established with " + req.getRemoteHost());
        } else if (req.getServletPath().equals(DISCONNECTED_PATH)) {
            logger.info("channel with " + req.getRemoteHost() + " closed");
        }
    }
}
