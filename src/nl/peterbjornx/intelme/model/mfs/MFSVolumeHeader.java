package nl.peterbjornx.intelme.model.mfs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class MFSVolumeHeader {
    private int Magic;
    private int Version;
    private int Capacity;
    private int FileCount;

    MFSVolumeHeader(ByteBuffer buf) {
        Magic = buf.getInt();
        Version = buf.getInt();
        Capacity = buf.getInt();
        FileCount = buf.getShort();
    }

    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(14).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(Magic);
        buf.putInt(Version);
        buf.putInt(Capacity);
        buf.putShort((short) FileCount);
        buf.position(0);
        return buf;
    }

    public int getMagic() {
        return Magic;
    }

    public int getVersion() {
        return Version;
    }

    public int getCapacity() {
        return Capacity;
    }

    public int getFileCount() {
        return FileCount;
    }
}
