package by.cs.cpu.s7;

/**
 * Based on S7Szl class
 * from Moka7 by David Nardella
 *  @see "http://snap7.sourceforge.net/"
 *
 * @author Dmitriy V.Yefremov
 */
public class S7Szl {

    private int lenThdr;
    private int nDr;
    private int dataSize;
    private byte data[];

    public S7Szl(int bufferSize) {
        data = new byte[bufferSize];
    }

    public int getLenThdr() {
        return lenThdr;
    }

    public void setLenThdr(int lenThdr) {
        this.lenThdr = lenThdr;
    }

    public int getnDr() {
        return nDr;
    }

    public void setnDr(int nDr) {
        this.nDr = nDr;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    protected void copy(byte[] src, int srcPos, int destPos, int size) {
        System.arraycopy(src, srcPos, data, destPos, size);
    }
}
