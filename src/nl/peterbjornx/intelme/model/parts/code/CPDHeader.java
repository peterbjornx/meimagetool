package nl.peterbjornx.intelme.model.parts.code;

import nl.peterbjornx.intelme.io.ByteBufTools;

import java.nio.ByteBuffer;

public class CPDHeader {
    private String marker;
    private int entries;
    private int HeaderVersion;
    private int EntryVersion;
    private int HeaderLength;
    private int HeaderChecksum;
    private String PartitionName;

    public CPDHeader(ByteBuffer buf) {
        marker = ByteBufTools.readCString(buf,4);
        entries = buf.getInt();
        HeaderVersion = buf.get() & 0xFF;
        EntryVersion = buf.get() & 0xFF;
        HeaderLength = buf.get() & 0xFF;
        HeaderChecksum = buf.get() & 0xFF;
        PartitionName = ByteBufTools.readCString(buf,4);
    }

    public String getMarker() {
        return marker;
    }

    public int getEntries() {
        return entries;
    }

    public int getHeaderVersion() {
        return HeaderVersion;
    }

    public int getEntryVersion() {
        return EntryVersion;
    }

    public int getHeaderLength() {
        return HeaderLength;
    }

    public int getHeaderChecksum() {
        return HeaderChecksum;
    }

    public String getPartitionName() {
        return PartitionName;
    }
}
