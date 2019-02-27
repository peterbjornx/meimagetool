package nl.peterbjornx.intelme.model.parts.code;

import java.nio.ByteBuffer;

public class CodePartitionDirectory {

    private CPDHeader CpdHeader;
    private CPDEntry CpdEntries[];

    public CodePartitionDirectory( ByteBuffer buf ){
        CpdHeader = new CPDHeader(buf);

        int npart = CpdHeader.getEntries();

        CpdEntries = new CPDEntry[npart];

        for ( int i = 0; i < npart; i++ )
            CpdEntries[i] = new CPDEntry( buf );
    }

    public CPDHeader getCpdHeader() {
        return CpdHeader;
    }

    public CPDEntry[] getCpdEntries() {
        return CpdEntries;
    }
}
