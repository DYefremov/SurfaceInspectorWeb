package by.cs;

import by.cs.web.StandaloneServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Main class for initialize and starting application
 *
 * @author Dmitriy V.Yefremov
 */
public class MainService {

    private StandaloneServer server;

    private static final Logger logger = LoggerFactory.getLogger(MainService.class);

    public MainService () {

    }

    public static void main(String[] args) {

        MainService service = new MainService();
        service.init();
    }

    public void init() {

        getSystemTray();
        server = StandaloneServer.getInstance();
        server.startServer();
    }

    /**
     * Constructing system tray
     *
     * @throws IOException
     */
    private void getSystemTray() {

        if (!SystemTray.isSupported()) {
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
            server.stopServer();
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
            Desktop.getDesktop().browse(new URI("http://localhost:" + Constants.DEFAULT_PORT));
        } catch (URISyntaxException e) {
            logger.error("MainService error [openUri]: " + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
