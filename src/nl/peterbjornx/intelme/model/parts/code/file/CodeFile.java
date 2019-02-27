package nl.peterbjornx.intelme.model.parts.code.file;

import nl.peterbjornx.intelme.model.parts.code.CPDEntry;

import java.nio.ByteBuffer;

public class CodeFile {
    private CPDEntry CpdEntry;
    protected ByteBuffer Data;

    public CodeFile(CPDEntry entry, ByteBuffer buf) {
        CpdEntry = entry;
        buf.position( CpdEntry.getAddress() );
        byte[] data = new byte[ CpdEntry.getLength() ];
        buf.get(data);
        Data = ByteBuffer.wrap(data);
        Data.order(buf.order());
    }

    public CPDEntry getCpdEntry() {
        return CpdEntry;
    }

    public ByteBuffer getData() {
        return Data;
    }

    public static CodeFile decode(CPDEntry entry, ByteBuffer b) {
        if ( entry.getLength() == 0 )
            return new CodeFile(entry,b);
        else if ( entry.getName().endsWith(".man") )
            return new ManifestFile(entry, b, true);
        else if ( entry.getName().endsWith(".met") )
            return new ManifestFile(entry, b, false);
        return new CodeFile(entry,b);
    }
}
