package by.cs.cpu;

/**
 * @author Dmitriy V.Yefremov
 */
public class CpuS7Connection implements CpuConnection {

    @Override
    public void connect(Cpu cpu) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Cpu getCurrentCpu() {
        return null;
    }

    @Override
    public void writeData(Data data) {

    }

    @Override
    public Data getData() {
        return null;
    }
}
