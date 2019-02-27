package nl.peterbjornx.intelme.model.parts;

import nl.peterbjornx.intelme.model.fpt.FPTEntry;
import nl.peterbjornx.intelme.model.parts.code.CodePartition;
import nl.peterbjornx.intelme.model.parts.code.MFSPartition;

import java.nio.ByteBuffer;

public class Partition {

    private FPTEntry FptEntry;
    protected ByteBuffer Data;

    public Partition(FPTEntry entry, ByteBuffer buf) {
        FptEntry = entry;
        byte[] data = new byte[ FptEntry.getLength() ];
        if ( buf.position() != 0 || buf.limit() != entry.getLength() )
            buf.position( FptEntry.getOffset() );
        buf.get(data);
        Data = ByteBuffer.wrap(data);
        Data.order(buf.order());
    }

    public FPTEntry getFptEntry() {
        return FptEntry;
    }

    public ByteBuffer getData() {
        return Data;
    }

    public static Partition decode(FPTEntry entry, ByteBuffer b) {
        if ( entry.getLength() == 0 )
            return new Partition(entry,b);
        switch( entry.getName() ) {
            case "ISHC":
            case "DLMP":
            case "FTUP":
            case "FTPR":
            case "NFTP":
                return new CodePartition(entry, b);
            case "MFS":
                return new MFSPartition(entry, b);
            default:
                return new Partition(entry, b);
        }
    }
}
