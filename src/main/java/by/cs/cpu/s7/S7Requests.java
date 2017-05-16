package by.cs.cpu.s7;

/**
 * Requests constants
 *
 *Taken from S7 Client from project Moka7 by David Nardella
 * @see "http://snap7.sourceforge.net/"
 *
 * @author Dmitriy V.Yefremov
 */
public class S7Requests {
    // WordLength
    private static final byte S7WLByte    =0x02;
    private static final byte S7WLCounter =0x1C;
    private static final byte S7WLTimer   =0x1D;

    private static final int Size_RD = 31;
    private static final int Size_WR = 35;

    /**
     * ISO Connection Request telegram (contains also ISO Header and COTP Header)
     *
     *     TPKT (RFC1006 Header)
     * [0] - RFC 1006 ID (3); [1] - Reserved, always 0; [2] - High part of packet lenght (entire frame, payload and TPDU included);
     * [3] - Low part of packet lenght (entire frame, payload and TPDU included);
     *     COTP (ISO 8073 Header)
     * [4] -  PDU Size Length; [5] - CR - Connection Request ID; [6] - Dst Reference HI; [7] - Dst Reference LO; [8] - Src Reference HI;
     * [9] - Src Reference LO; [10] - Class + Options Flags; [11] - PDU Max Length ID; [12] -  PDU Max Length HI;
     * [13] - PDU Max Length LO; [14] - Src TSAP Identifier; [15] - Src TSAP Length (2 bytes);
     * [16] - Src TSAP HI (will be overwritten); [17] - Src TSAP LO (will be overwritten); [18] - Dst TSAP Identifier;
     * [19] -  Dst TSAP Length (2 bytes); [20] - Dst TSAP HI (will be overwritten); [21] -  Dst TSAP LO (will be overwritten)
     *
     */
    public static final byte ISO_CR[] = {
            (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x16, (byte)0x11, (byte)0xE0, (byte)0x00, (byte)0x00, (byte)0x00,
            (byte)0x01, (byte)0x00, (byte)0xC0, (byte)0x01, (byte)0x0A, (byte)0xC1, (byte)0x02, (byte)0x01,
            (byte)0x00, (byte)0xC2, (byte)0x02, (byte)0x01, (byte)0x02
    };

    /**
     *  SZL First telegram request
     *
     * [12] - Sequence out; [29] - ID; [31] - Index
     */
    public static final byte S7_SZL_FIRST[] = {
            (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x21, (byte)0x02, (byte)0xf0, (byte)0x80, (byte)0x32, (byte)0x07,
            (byte)0x00, (byte)0x00, (byte)0x05, (byte)0x00, (byte)0x00, (byte)0x08, (byte)0x00, (byte)0x08, (byte)0x00,
            (byte)0x01, (byte)0x12, (byte)0x04, (byte)0x11, (byte)0x44, (byte)0x01, (byte)0x00, (byte)0xff, (byte)0x09,
            (byte)0x00, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00
    };

    /**
     * SZL Next telegram request
     *
     * [24] - Sequence
     */
    public static final byte S7_SZL_NEXT[] = {
            (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x21, (byte)0x02, (byte)0xf0, (byte)0x80, (byte)0x32, (byte)0x07,
            (byte)0x00, (byte)0x00, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x0c, (byte)0x00, (byte)0x04, (byte)0x00,
            (byte)0x01, (byte)0x12, (byte)0x08, (byte)0x12, (byte)0x44, (byte)0x01, (byte)0x01, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x0a, (byte)0x00, (byte)0x00, (byte)0x00
    };

    /**
     * S7 PDU Negotiation Telegram (contains also ISO Header and COTP Header)
     */
    private static final byte S7_PN[] = {
            (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x19,
            (byte)0x02, (byte)0xf0, (byte)0x80, // TPKT + COTP (see above for info)
            (byte)0x32, (byte)0x01, (byte)0x00, (byte)0x00,
            (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x08,
            (byte)0x00, (byte)0x00, (byte)0xf0, (byte)0x00,
            (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x01,
            (byte)0x00, (byte)0x1e // PDU Length Requested = HI-LO 480 bytes
    };

    /**
     * S7 Read/Write Request Header (contains also ISO Header and COTP Header)
     */
    private static final byte S7_RW[] = { // 31-35 bytes
            (byte)0x03,(byte)0x00,
            (byte)0x00,(byte)0x1f,  // Telegram Length (data Size + 31 or 35)
            (byte)0x02,(byte)0xf0, (byte)0x80, // COTP (see above for info)
            (byte)0x32,             // S7 Protocol ID
            (byte)0x01,             // Job Type
            (byte)0x00,(byte)0x00,  // Redundancy identification
            (byte)0x05,(byte)0x00,  // PDU Reference
            (byte)0x00,(byte)0x0e,  // Parameters Length
            (byte)0x00,(byte)0x00,  // data Length = Size(bytes) + 4
            (byte)0x04,             // Function 4 Read Var, 5 Write Var
            (byte)0x01,             // Items count
            (byte)0x12,             // Var spec.
            (byte)0x0a,             // Length of remaining bytes
            (byte)0x10,             // Syntax ID
            S7WLByte,               // Transport Size
            (byte)0x00,(byte)0x00,  // Num Elements
            (byte)0x00,(byte)0x00,  // DB Number (if any, else 0)
            (byte)0x84,             // Area Type
            (byte)0x00,(byte)0x00,(byte)0x00, // Area Offset
            // WR area
            (byte)0x00,             // Reserved
            (byte)0x04,             // Transport size
            (byte)0x00,(byte)0x00,  // data Length * 8 (if not timer or counter)
    };

}
