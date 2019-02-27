package nl.peterbjornx.intelme.model.parts.code;

import nl.peterbjornx.intelme.io.ByteBufTools;

import java.nio.ByteBuffer;

public class CPDEntry {
    private String Name;
    private int Address;
    private boolean CompressFlag;
    private int Reserved0;
    private int Length;
    private int reserved;

    public CPDEntry(ByteBuffer buf) {
        Name = ByteBufTools.readCString(buf, 12);
        int bf = buf.getInt();
        Address = bf & 0x1FFFFFF;
        CompressFlag = (bf & 0x2000000) != 0;
        Reserved0 = (bf >> 26) & 0x3F;
        Length = buf.getInt();
        reserved = buf.getInt();
    }

    public String getName() {
        return Name;
    }

    public int getAddress() {
        return Address;
    }

    public boolean isCompressFlag() {
        return CompressFlag;
    }

    public int getReserved0() {
        return Reserved0;
    }

    public int getLength() {
        return Length;
    }

    public int getReserved() {
        return reserved;
    }

    @Override
    public String toString() {
        return "CPDEntry{" +
                "Name='" + Name + '\'' +
                ", Address=" + Address +
                ", CompressFlag=" + CompressFlag +
                ", Reserved0=" + Reserved0 +
                ", Length=" + Length +
                ", reserved=" + reserved +
                '}';
    }
}
