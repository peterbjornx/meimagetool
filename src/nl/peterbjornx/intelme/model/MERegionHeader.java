package nl.peterbjornx.intelme.model;

import com.thoughtworks.xstream.annotations.*;
import nl.peterbjornx.intelme.model.fpt.FPTEntry;
import nl.peterbjornx.intelme.model.fpt.FPTHeader;
import nl.peterbjornx.intelme.util.MEFormatException;

import java.nio.ByteBuffer;

@XStreamAlias("Region")
public class MERegionHeader {
    private byte[]     RomBypass;
    @XStreamOmitField
    private boolean FullyDeserialized = false;
    private FPTHeader   FptHeader;
    @XStreamImplicit
    private FPTEntry[] FptPartitionEntries;

    public MERegionHeader()
    {
    }

    public MERegionHeader( ByteBuffer buf ) {
        FullyDeserialized = true;

        RomBypass = new byte[16];
        buf.get(RomBypass);
        FptHeader = new FPTHeader(buf);

        int npart = FptHeader.getNumFptEntries();

        FptPartitionEntries = new FPTEntry[npart];

        for ( int i = 0; i < npart; i++ )
            FptPartitionEntries[i] = new FPTEntry( buf );

    }

    private synchronized void ensureDeserialized() {
        if (FullyDeserialized)
            return;
        FptHeader.setNumFptEntries(FptPartitionEntries.length);
        FptHeader.updateChecksum();
        FullyDeserialized = true;
    }

    public void encode( ByteBuffer buf ) {
        ensureDeserialized();
        buf.put(RomBypass);
        FptHeader.encode(buf);
        for ( FPTEntry e : FptPartitionEntries )
            e.encode(buf);
    }

    public void verify() throws MEFormatException {
        ensureDeserialized();
        FptHeader.verify();
        for ( FPTEntry e : FptPartitionEntries )
            e.verify();
    }

    public byte[] getRomBypass() {
        ensureDeserialized();
        return RomBypass;
    }

    public FPTHeader getFptHeader() {
        ensureDeserialized();
        return FptHeader;
    }

    public FPTEntry[] getFptPartitionEntries() {
        ensureDeserialized();
        return FptPartitionEntries;
    }
}
