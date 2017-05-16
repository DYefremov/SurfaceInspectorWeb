package by.cs.cpu;

/**
 *@author Dmitriy V.Yefremov
 */
public interface CpuConnection<T> {

    void connect();

    void connect(T  cpu);

    void disconnect();

    boolean isConnected();

    T getCurrentCpu();

    Object getInfo();

    void writeData(Data data);

    Data getData();
}
