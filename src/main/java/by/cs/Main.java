package by.cs;

/**
 * @autor Dmitriy V.Yefremov
 */
public class Main {

    public static void main(String[] args) {

        MainService service = new MainService();
        service.getSystemTray();
        service.startServer();
    }
}
