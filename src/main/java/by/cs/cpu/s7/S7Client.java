package by.cs.cpu.s7;

import by.cs.cpu.CpuS7;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


/**
 * Implementation of s7 client based on S7Client class
 * from project Moka7 by David Nardella
 * @see "http://snap7.sourceforge.net/"
 *
 * @author Dmitriy V.Yefremov
 */
public class S7Client {

    private boolean connected;
    private boolean hasError;
    private CpuS7 cpu;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private final byte[] PDU = new byte[2048];
    //Default port
    private static final int PORT = 102;
    private static final int MIN_PDU_SIZE = 16;
    private static final int DEFAULT_PDU_SIZE_REQUESTED = 480;
    private static final int ISO_H_SIZE = 7; // TPKT+COTP Header Size
    private static final int MAX_PDU_SIZE = DEFAULT_PDU_SIZE_REQUESTED + ISO_H_SIZE;
    private static final int TIMEOUT = 2000;
    //Used connection type OP
    private static final short CONNECTION_TYPE = 2;

    private byte LOCAL_TSAP_HI;
    private byte LOCAL_TSAP_LO;
    private byte REMOTE_TSAP_HI;
    private byte REMOTE_TSAP_LO;
    private byte lastPduType;

    private static final Logger logger = LoggerFactory.getLogger(S7Client.class);

    public S7Client() {
        //Sets default cpu
        cpu = new CpuS7(0, 2, "127.0.0.1");
    }

    public S7Client(CpuS7 cpu) {
        this.cpu = cpu;
    }

    public CpuS7 getCpu() {
        return cpu;
    }

    public void setCpu(CpuS7 cpu) {
        this.cpu = cpu;
    }

    public boolean isConnected() {
        return connected;
    }

    /**
     * Connect to CPU
     */
    public void connect() {
        //Set connection parameters
        int remoteTSAP=(CONNECTION_TYPE<<8) + (cpu.getRack() * 0x20) + cpu.getSlot();
        int locTSAP = 0x0100 & 0x0000FFFF;
        int remTSAP = remoteTSAP & 0x0000FFFF;
        LOCAL_TSAP_HI = (byte) (locTSAP>>8);
        LOCAL_TSAP_LO = (byte) (locTSAP & 0x00FF);
        REMOTE_TSAP_HI= (byte) (remTSAP>>8);
        REMOTE_TSAP_LO= (byte) (remTSAP & 0x00FF);

        try {
            SocketAddress address = new InetSocketAddress(cpu.getIp(), PORT);
            socket = new Socket();
            socket.connect(address, 5000);
            socket.setTcpNoDelay(true);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            if (socket.isConnected()) {
                int size = 0;
                S7Requests.ISO_CR[16] = LOCAL_TSAP_HI;
                S7Requests.ISO_CR[17] = LOCAL_TSAP_LO;
                S7Requests.ISO_CR[20] = REMOTE_TSAP_HI;
                S7Requests.ISO_CR[21] = REMOTE_TSAP_LO;
                // Sends the connection request telegram
                sendPacket(S7Requests.ISO_CR, S7Requests.ISO_CR.length);

                if (!hasError) {
                    size = receivePacket();
                }
                connected = size == 22 && lastPduType !=(byte)0xD0;
            }
        } catch (IOException e) {
            connected = false;
            disconnect();
            logger.error("S7Client error [connect]: " + e);
        }
    }

    /**
     *Disconnect from the CPU
     */
    public void disconnect() {

        try {
            if (inputStream != null) {
                inputStream.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }

            if (socket != null ) {
                socket.close();
                connected = socket.isClosed();
            }
        } catch (IOException e) {
            logger.error("S7Client error [disconnect]: " + e);
        }
    }

    /**
     * @param buffer
     */
    private void sendPacket(byte[] buffer) {
        sendPacket(buffer, buffer.length);
    }

    /**
     * @param buffer
     * @param length
     */
    private void sendPacket(byte[] buffer, int length) {

        if (outputStream == null) {
            logger.error("S7Client error [sendPacket]: output is null.");
            return;
        }

        try {
            outputStream.write(buffer, 0 , length);
            outputStream.flush();
        } catch (IOException e) {
            hasError = true;
            logger.error("S7Client error [sendPacket]: " + e);
        }
    }

    /**
     * @return
     */
    private int receivePacket() {

        int size = 0;
        boolean done = false;

        while (!hasError && !done) {
            // Get TPKT (4 bytes)
            receivePacket(PDU, 0, 4);

            if (!hasError) {
                size= S7.getWordAt(PDU,2);
                // Check 0 bytes data Packet (only TPKT+COTP = 7 bytes)
                if (size == ISO_H_SIZE) {
                    // Skip remaining 3 bytes and Done is still false
                    receivePacket(PDU,4, 3);
                } else {
                    // a valid Length !=7 && >16 && <247
                    hasError = (size > MAX_PDU_SIZE) || (size < MIN_PDU_SIZE);
                    done = !hasError;
                }
            }
        }

        if (!hasError) {
            // Skip remaining 3 COTP bytes
            receivePacket(PDU, 4, 3);
            // Stores PDU Type, we need it
            lastPduType = PDU[5];
            // Receives the S7 Payload
            receivePacket(PDU, 7, size - ISO_H_SIZE);
        }

        return !hasError ? size : 0;
    }


    /**
     * @param Buffer
     * @param start
     * @param size
     */
    private void receivePacket(byte[] Buffer, int start, int size) {

        int bytesRead = 0;
        getData(size, TIMEOUT);

        if (!hasError) {
            try {
                bytesRead = inputStream.read(Buffer, start, size);
            } catch (IOException e) {
                logger.error("S7Client error [receivePacket]: " + e);
                hasError = true;
            }
            hasError = bytesRead == 0;
        }
    }

    /**
     * @param size
     * @param timeout
     */
    private void getData(int size, int timeout) {

        if (inputStream == null) {
            hasError = true;
            logger.error("S7Client error [getData]: input is null");
            return;
        }

        int cnt = 0;
        hasError = false;
        int availableSize;
        boolean expired = false;

        try {
            availableSize = inputStream.available();

            while ((availableSize < size) && (!expired) && !hasError) {
                cnt++;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    hasError = true;
                    logger.error("S7Client error [getData]: " + e);
                }
                availableSize = inputStream.available();
                expired = cnt > timeout;
                // If timeout we clean the buffer
                if (expired && (availableSize > 0) && !hasError) {
                    inputStream.read(PDU, 0, availableSize);
                }
            }
        } catch (IOException e) {
            hasError = true;
            logger.error("S7Client error [getData]: " + e);
        }

        hasError = cnt >= timeout ? true : false;
    }

    /**
     * @param id
     * @param index
     * @param szl
     * @return
     */
    public int readSZL(int id, int index, S7Szl szl) {

        int error = 0;
        int length;
        int dataSZL;
        int offset = 0;
        boolean done = false;
        boolean first = true;
        byte seqIn = 0x00;
        int seqOut = 0x0000;

        szl.setDataSize(0);

        do {
            if (first) {
                S7.setWordAt(S7Requests.S7_SZL_FIRST, 11, ++seqOut);
                S7.setWordAt(S7Requests.S7_SZL_FIRST, 29, id);
                S7.setWordAt(S7Requests.S7_SZL_FIRST, 31, index);
                sendPacket(S7Requests.S7_SZL_FIRST);
            } else {
                S7.setWordAt(S7Requests.S7_SZL_NEXT, 11, ++seqOut);
                PDU[24] = (byte)seqIn;
                sendPacket(S7Requests.S7_SZL_NEXT);
            }

            if (error != 0) {
                return error;
            }

            length = receivePacket();

            if (error == 0) {
                if (first) {
                    // the minimum expected
                    if (length > 32 && (S7.getWordAt(PDU,27) == 0) && (PDU[29] == (byte)0xFF)) {
                        dataSZL=S7.getWordAt(PDU, 31) - 8;
                        done = PDU[26] == 0x00;
                        seqIn = (byte)PDU[24];
                        szl.setLenThdr(S7.getWordAt(PDU, 37));
                        szl.setnDr(S7.getWordAt(PDU, 39));
                        szl.copy(PDU, 41, offset, dataSZL);
                        offset += dataSZL;
                        szl.setDataSize(szl.getDataSize() + dataSZL);
                    } else {
                        error = -1;
                    }
                } else {
                    // the minimum expected
                    if (length > 32 && (S7.getWordAt(PDU,27) == 0) && (PDU[29] == (byte)0xFF)) {
                        // Gets Amount of this slice
                        dataSZL = S7.getWordAt(PDU,31);
                        done = PDU[26] == 0x00;
                        seqIn = (byte)PDU[24];
                        szl.copy(PDU, 37, offset, dataSZL);
                        offset += dataSZL;
                        szl.setDataSize(szl.getDataSize() + dataSZL);
                    } else {
                        error = -1;
                    }
                }
            }
            first = false;
        } while (!done && (error == 0));

        return error;
    }


    /**
     * @return
     */
    public String getCpInfo() {

        StringBuilder sb = new StringBuilder();

        S7Szl szl = new S7Szl(1024);
        int error = readSZL(0x0131, 0x0001, szl);
        byte[] data = szl.getData();

        if (error == 0) {
            int MaxPduLength = S7.getShortAt(data, 2);
            int MaxConnections = S7.getShortAt(data, 4);
            int MaxMpiRate = S7.getDIntAt(data, 6);
            int MaxBusRate = S7.getDIntAt(data, 10);

            sb.append("Max PDU Length    : " + MaxPduLength + "\n");
            sb.append("Max connections   : " + MaxConnections + "\n");
            sb.append("Max MPI rate (bps): " + MaxMpiRate + "\n");
            sb.append("Max Bus rate (bps): " + MaxBusRate + "\n");

            return sb.toString();
        }

        return "Error reading info!";
    }

}
