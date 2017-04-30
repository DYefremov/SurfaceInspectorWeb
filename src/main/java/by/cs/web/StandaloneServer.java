package by.cs.web;

import by.cs.Constants;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * For the server role uses jetty
 *
 * @autor Dmitriy V.Yefremov
 */
public class StandaloneServer {

    private Server server;
    private static final Logger logger = LoggerFactory.getLogger(StandaloneServer.class);

    private static StandaloneServer INSTANCE;

    private StandaloneServer () {

    }

    public static synchronized StandaloneServer getInstance() {
        if (INSTANCE == null) {
            return new StandaloneServer();
        }
        return INSTANCE;
    }

    /**
     * Start jetty
     */
    public void startServer() {

        Thread thread = new Thread(() -> {
            try {
                initServer();
            } catch (Exception e) {
                logger.error("StandaloneServer error [startServer]: " + e);
            }
        });

        thread.setDaemon(true);
        thread.start(); 
    }

    /**
     * Stop jetty
     */
    public void stopServer() {
        if (server != null && server.isRunning()) {
            try {
                server.stop();
            } catch (Exception e) {
                logger.error("StandaloneServer error [stopServer]: " + e);
            }
        }
    }

    /**
     * Initialize server
     *
     * @throws Exception
     */
    private void initServer() throws Exception {

        server = new Server();
        server.addBean(new ScheduledExecutorScheduler());
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);

        ServerConnector httpConnector = new ServerConnector(server);
        httpConnector.setHost("localhost");
        httpConnector.setPort(Constants.DEFAULT_PORT);
        httpConnector.setIdleTimeout(5000);
        server.addConnector(httpConnector);

        final String webDir = this.getClass().getClassLoader().getResource("webapp").toExternalForm();
        WebAppContext appContext = new WebAppContext(webDir, "");

        server.setHandler(appContext);
        server.start();
        server.join();
    }

}
