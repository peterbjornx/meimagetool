package nl.peterbjornx.intelme.model.mfs;

import java.nio.ByteBuffer;

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
