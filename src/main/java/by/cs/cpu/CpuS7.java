package by.cs.cpu;

/**
 * @author Dmitriy V.Yefremov
 */
public class CpuS7 implements Cpu {

    private int rack;
    private int slot;
    private String Ip;

    public CpuS7() {

    }

    public CpuS7(int rack, int slot, String ip) {
        this.rack = rack;
        this.slot = slot;
        this.Ip = ip;
    }

    public int getRack() {
        return rack;
    }

    public void setRack(int rack) {
        this.rack = rack;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String getIp() {
        return Ip;
    }

    public void setIp(String ip) {
        Ip = ip;
    }

    @Override
    public String toString() {
        return "CpuS7{" +
                "rack=" + rack +
                ", slot=" + slot +
                ", Ip='" + Ip + '\'' +
                '}';
    }
}
