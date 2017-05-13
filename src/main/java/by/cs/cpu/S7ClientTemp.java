package by.cs.cpu;

import by.cs.cpu.s7.S7;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Implementation of s7 client based on
 * project Moka7 by David Nardella
 * @see "http://snap7.sourceforge.net/"
 *
 * @author Dmitriy V.Yefremov
 */
public class S7ClientTemp {

    private boolean connected;
    private boolean hasError;
    private CpuS7 cpu;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private final byte[] PDU = new byte[2048];
    //Default port
    private static final int PORT = 102;
    private static final int MinPduSize = 16;
    private static final int DefaultPduSizeRequested = 480;
    private static final int IsoHSize = 7; // TPKT+COTP Header Size
    private static final int MaxPduSize = DefaultPduSizeRequested+IsoHSize;
    private static final int TIMEOUT = 2000;
    //Used connection type OP
    private static final short CONNECTION_TYPE = 2;


    private byte LOCAL_TSAP_HI;
    private byte LOCAL_TSAP_LO;
    private byte REMOTE_TSAP_HI;
    private byte REMOTE_TSAP_LO;
    private byte lastPduType;

    /**
     * ISO Connection Request telegram (contains also ISO Header and COTP Header)
     */
    private static final byte ISO_CR[] = {
            // TPKT (RFC1006 Header)
            (byte)0x03, // RFC 1006 ID (3)
            (byte)0x00, // Reserved, always 0
            (byte)0x00, // High part of packet lenght (entire frame, payload and TPDU included)
            (byte)0x16, // Low part of packet lenght (entire frame, payload and TPDU included)
            // COTP (ISO 8073 Header)
            (byte)0x11, // PDU Size Length
            (byte)0xE0, // CR - Connection Request ID
            (byte)0x00, // Dst Reference HI
            (byte)0x00, // Dst Reference LO
            (byte)0x00, // Src Reference HI
            (byte)0x01, // Src Reference LO
            (byte)0x00, // Class + Options Flags
            (byte)0xC0, // PDU Max Length ID
            (byte)0x01, // PDU Max Length HI
            (byte)0x0A, // PDU Max Length LO
            (byte)0xC1, // Src TSAP Identifier
            (byte)0x02, // Src TSAP Length (2 bytes)
            (byte)0x01, // Src TSAP HI (will be overwritten)
            (byte)0x00, // Src TSAP LO (will be overwritten)
            (byte)0xC2, // Dst TSAP Identifier
            (byte)0x02, // Dst TSAP Length (2 bytes)
            (byte)0x01, // Dst TSAP HI (will be overwritten)
            (byte)0x02  // Dst TSAP LO (will be overwritten)
    };

    private static final Logger logger = LoggerFactory.getLogger(S7ClientTemp.class);

    public S7ClientTemp() {
        //Sets default cpu
        cpu = new CpuS7(0, 2, "127.0.0.1");
    }

    public S7ClientTemp(CpuS7 cpu) {
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
        int remoteTSAP=(CONNECTION_TYPE<<8)+ (cpu.getRack() * 0x20) + cpu.getSlot();
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
            connected =socket.isConnected();
        } catch (IOException e) {
            connected = false;
            disconnect();
            logger.error("S7ClientTemp error [connect]: " + e);
        }

        if (connected) {
            connected = isoConnect();
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
            logger.error("S7ClientTemp error [disconnect]: " + e);
        }
    }


    private boolean isoConnect() {

        int size;
        ISO_CR[16]= LOCAL_TSAP_HI;
        ISO_CR[17]= LOCAL_TSAP_LO;
        ISO_CR[20]= REMOTE_TSAP_HI;
        ISO_CR[21]= REMOTE_TSAP_LO;
        // Sends the connection request telegram
        sendPacket(ISO_CR, ISO_CR.length);

        if (!hasError) {
            size = recivePacket();
            return size == 22 && lastPduType !=(byte)0xD0;
        }

        return false;
    }


    /**
     * @param buffer
     * @param length
     */
    private void sendPacket(byte[] buffer, int length) {

        try {
            outputStream.write(buffer, 0 , length);
            outputStream.flush();
        } catch (IOException e) {
            hasError = true;
            logger.error("S7ClientTemp error [sendPacket]: " + e);
        }
    }


    /**
     * @return
     */
    private int recivePacket() {

        boolean done = false;
        int size = 0;

        while ((!hasError) && !done) {
            // Get TPKT (4 bytes)
            recvPacket(PDU, 0, 4);

            if (!hasError) {
                size= S7.GetWordAt(PDU,2);
                // Check 0 bytes Data Packet (only TPKT+COTP = 7 bytes)
                if (size == IsoHSize) {
                    recvPacket(PDU,4, 3); // Skip remaining 3 bytes and Done is still false
                } else {
                    if ((size > MaxPduSize) || (size < MinPduSize))
                       hasError = true;
                    else
                        done = true; // a valid Length !=7 && >16 && <247
                }
            }
        }

        if (!hasError) {
            recvPacket(PDU,4, 3); // Skip remaining 3 COTP bytes
            lastPduType =PDU[5];   // Stores PDU Type, we need it
            // Receives the S7 Payload
            recvPacket(PDU, 7, size - IsoHSize);
        }

        return !hasError ? size : 0;
    }


    /**
     * @param Buffer
     * @param start
     * @param size
     */
    private void recvPacket(byte[] Buffer, int start, int size) {

        int bytesRead=0;
        getData(size, TIMEOUT);

        if (!hasError) {
            try {
                bytesRead = inputStream.read(Buffer, start, size);
            } catch (IOException ex) {
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

        int cnt = 0;
        hasError=false;
        int availableSize;
        boolean expired = false;

        try  {
            availableSize = inputStream.available();
            while ((availableSize < size) && (!expired) && !hasError) {
                cnt++;
                try {
                    Thread.sleep(1);
                }
                catch (InterruptedException e) {
                    hasError = true;
                }
                availableSize=inputStream.available();
                expired = cnt > timeout;
                // If timeout we clean the buffer
                if (expired && (availableSize>0) && !hasError) {
                    inputStream.read(PDU, 0, availableSize);
                }
            }
        } catch (IOException e) {
            hasError = true;
        }

        if (cnt >= timeout) {
            hasError = true;
        }

        hasError = false;
    }

}