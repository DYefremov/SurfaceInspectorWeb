package by.cs.cpu.s7;

/**
 * Step 7 Constants and Conversion
 * based on S7 class from
 * project Moka7 by David Nardella
 * @see "http://snap7.sourceforge.net/"
 *
 * It will used minimum necessary components
 *
 * @author Dmitriy V.Yefremov
 */
public class S7 {

    // S7 ID Area (Area that we want to read/write)
    public static final int S7AreaMK = 0x83;
    public static final int S7AreaDB = 0x84;
    // Connection types
    public static final byte OP = 0x02;
    // Block type
    public static final int Block_DB   = 0x41;

    /**
     * @param buffer
     * @param pos
     * @param bit
     * @return the bit at Pos.Bit
     */
    public static boolean getBitAt(byte[] buffer, int pos, int bit) {

        int value = buffer[pos] & 0x0FF;

        byte[] mask = {(byte)0x01, (byte)0x02, (byte)0x04, (byte)0x08, (byte)0x10, (byte)0x20, (byte)0x40, (byte)0x80};

        if (bit<0) bit=0;
        if (bit>7) bit=7;

        return (value & mask[bit])!= 0;
    }

    /**
     * @param buffer
     * @param pos start position
     * @return 16 bit unsigned value : from 0 to 65535 (2^16-1)
     */
    public static int getWordAt(byte[] buffer, int pos) {

        int hi = (buffer[pos] & 0x00FF);
        int lo = (buffer[pos+1] & 0x00FF);

        return (hi<<8)+lo;
    }

    /**
     * @param buffer
     * @param pos
     * @return 16 bit signed value : from -32768 to 32767
     */
    public static int getShortAt(byte[] buffer, int pos) {

        int hi = (buffer[pos]);
        int lo = (buffer[pos+1] & 0x00FF);

        return ((hi<<8)+lo);
    }

    /**
     *
     * @param buffer
     * @param pos
     * @return 32 bit unsigned value : from 0 to 4294967295 (2^32-1)
     */
    public static long getDWordAt(byte[] buffer, int pos) {

        long result;
        result = (long)(buffer[pos] & 0x0FF);
        result <<= 8;
        result += (long)(buffer[pos+1] & 0x0FF);
        result <<= 8;
        result += (long)(buffer[pos+2] & 0x0FF);
        result <<= 8;
        result += (long)(buffer[pos+3] & 0x0FF);

        return result;
    }

    /**
     * @param buffer
     * @param pos
     * @return 32 bit signed value : from 0 to 4294967295 (2^32-1)
     */
    public static int getDIntAt(byte[] buffer, int pos) {

        int result;
        result = buffer[pos];
        result <<= 8;
        result += (buffer[pos+1] & 0x0FF);
        result <<= 8;
        result += (buffer[pos+2] & 0x0FF);
        result <<= 8;
        result += (buffer[pos+3] & 0x0FF);

        return result;
    }

    /**
     * @param buffer
     * @param pos
     * @param bit
     * @param value
     */
    public static void setBitAt(byte[] buffer, int pos, int bit, boolean value) {

        byte[] mask = {(byte)0x01, (byte)0x02, (byte)0x04, (byte)0x08, (byte)0x10, (byte)0x20, (byte)0x40, (byte)0x80};

        if (bit<0) bit = 0;
        if (bit>7) bit = 7;

        if (value) {
            buffer[pos] = (byte) (buffer[pos] | mask[bit]);
        } else {
            buffer[pos] = (byte) (buffer[pos] & ~mask[bit]);
        }
    }

    /**
     * @param buffer
     * @param pos
     * @param value
     */
    public static void setWordAt(byte[] buffer, int pos, int value) {

        int word = value & 0x0FFFF;
        buffer[pos] = (byte) (word >> 8);
        buffer[pos+1] = (byte) (word & 0x00FF);
    }

    public static void setShortAt(byte[] buffer, int pos, int value) {

        buffer[pos] = (byte) (value >> 8);
        buffer[pos+1] = (byte) (value & 0x00FF);
    }

    public static void setDWordAt(byte[] buffer, int pos, long value) {

        long DWord = value &0x0FFFFFFFF;
        buffer[pos+3] = (byte) (DWord & 0xFF);
        buffer[pos+2] = (byte) ((DWord >> 8) & 0xFF);
        buffer[pos+1] = (byte) ((DWord >> 16) & 0xFF);
        buffer[pos] = (byte) ((DWord >> 24) & 0xFF);
    }

    public static void setDIntAt(byte[] buffer, int pos, int value) {

        buffer[pos+3] = (byte) (value & 0xFF);
        buffer[pos+2] = (byte) ((value >> 8) & 0xFF);
        buffer[pos+1] = (byte) ((value >> 16) & 0xFF);
        buffer[pos] = (byte) ((value >> 24) & 0xFF);
    }
}
