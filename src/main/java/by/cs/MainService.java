package by.cs;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @autor Dmitriy V.Yefremov
 */
public class MainService {

    private Server server;
    private static final int PORT = 8080;
    private static final Logger logger = LoggerFactory.getLogger(MainService.class);

    public MainService () {

    }

    /**
     * Start jetty
     */
    public void startServer() {

        Thread thread = new Thread(() -> {
            server = new Server(PORT);
            try {
                server.start();
            } catch (Exception e) {
                logger.error("MainService error [startServer]: " + e);
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
                logger.error("MainService error [stopServer]: " + e);
            }
        }
    }

    /**
     * Constructing system tray
     *
     * @throws IOException
     */
    public void getSystemTray() {

        if (!SystemTray.isSupported()) {
            stopServer();
            System.exit(0);
        }

        final SystemTray tray = SystemTray.getSystemTray();
        Image image = null;
        try {
            image = ImageIO.read(MainService.class.getResource("img/icon.png"));
        } catch (IOException e) {
            logger.error("MainService error [getSystemTray]: " + e);
        }

        final TrayIcon trayIcon = new TrayIcon(image);
        final PopupMenu menu = new PopupMenu();
        final MenuItem openItem = new MenuItem("Open");
        final MenuItem exitItem = new MenuItem("Exit");

        openItem.addActionListener(e -> openUri());
        trayIcon.addActionListener(ev -> openUri());
        exitItem.addActionListener(e -> {
            tray.remove(trayIcon);
            stopServer();
            System.exit(0);
        });

        menu.add(openItem);
        menu.addSeparator();
        menu.add(exitItem);
        trayIcon.setPopupMenu(menu);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            logger.error("MainService error [getSystemTray]: " + e);
        }
    }

    /**
     * Open page in browser
     */
    private void openUri()  {
        try {
            Desktop.getDesktop().browse(new URI("http://localhost:" + PORT));
        } catch (URISyntaxException e) {
            logger.error("MainService error [openUri]: " + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
