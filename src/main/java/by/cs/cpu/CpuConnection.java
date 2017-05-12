package by.cs.cpu;

/**
 *@author Dmitriy V.Yefremov
 */
public interface CpuConnection {

    void connect(Cpu cpu);

    void disconnect();

    boolean isConnected();

    Cpu getCurrentCpu();

    void writeData(Data data);

    Data getData();
}
