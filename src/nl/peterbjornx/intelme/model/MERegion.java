package nl.peterbjornx.intelme.model;

import nl.peterbjornx.intelme.io.ByteBufTools;
import nl.peterbjornx.intelme.io.XmlTools;
import nl.peterbjornx.intelme.model.fpt.FPTEntry;
import nl.peterbjornx.intelme.model.parts.Partition;
import nl.peterbjornx.intelme.util.MEFormatException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MERegion {
    private MERegionHeader header;
    private Partition[] partitions;

    public MERegion(File sourceDir) throws IOException {
        File xmlFile = new File(sourceDir, "region.xml");
        header = (MERegionHeader) XmlTools.DeserializeXML(xmlFile);

        FPTEntry[] entries = header.getFptPartitionEntries();
        partitions = new Partition[entries.length];
        for ( int i = 0; i < entries.length; i++ ) {
            ByteBuffer partBuf;
            int partLen = entries[i].getLength();
            File partFile = new File(sourceDir, entries[i].getName() + ".mep" );
            if ( partLen == 0 || !partFile.exists()) {
                if ( partLen != 0 )
                    System.err.println("[ WARN] Partition "+entries[i].getName()+" has nonzero size but no"+
                        " contents have been found!");
                partBuf = ByteBuffer.allocate( partLen );
            } else
                partBuf = ByteBufTools.ReadToByteBuffer(partFile.getAbsolutePath());
            partitions[i] = new Partition(entries[i], partBuf);
        }

    }

    public MERegion(ByteBuffer buf, boolean decodeDeep){
        header = new MERegionHeader(buf);
        FPTEntry[] es = header.getFptPartitionEntries();
        partitions = new Partition[es.length];
        for ( int i = 0; i < es.length; i++ ) {
            if ( decodeDeep )
                partitions[i] = Partition.decode(es[i], buf);
            else
                partitions[i] = new Partition(es[i], buf);
        }
    }

    public MERegionHeader getHeader() {
        return header;
    }

    public Partition[] getPartitions() {
        return partitions;
    }

    public void extract(File destDir) throws IOException {
        if ( destDir.exists() ) {
            throw new IOException("Cowardly refusing to overwrite existing directory!");
        }
        if ( !destDir.mkdir() ) {
            throw new IOException("Could not create output directory!");
        }
        File xmlFile = new File(destDir, "region.xml");
        XmlTools.SerializeXML(header, xmlFile);
        for( Partition p : partitions ) {
            if ( p.getFptEntry().getLength() == 0 )
                continue;
            File partFile = new File( destDir, p.getFptEntry().getName() + ".mep" );
            ByteBufTools.WriteFromByteBuffer( p.getData(), partFile.getAbsolutePath() );
        }
    }

    public void verify() throws MEFormatException {
        header.verify();
    }

    public void compile(File outputFile) throws IOException {
        int fileMinSize = 0;
        byte[] fileData;

        for ( Partition p : partitions ) {
            int endOffset = p.getFptEntry().getOffset() + p.getFptEntry().getLength();
            if ( endOffset > fileMinSize )
                fileMinSize = endOffset;
        }

        fileData = new byte[fileMinSize];
        for ( int i = 0; i < fileData.length; i++ )
            fileData[i] = (byte) 0xFF;
        ByteBuffer fileBuf = ByteBuffer.wrap( fileData );
        fileBuf.order(ByteOrder.LITTLE_ENDIAN);

        header.encode(fileBuf);

        for ( Partition p : partitions ) {
            p.getData().position(0);
            fileBuf.position(p.getFptEntry().getOffset());
            fileBuf.put(p.getData().asReadOnlyBuffer());
        }

        ByteBufTools.WriteFromByteBuffer( fileBuf, outputFile.getPath() );

    }
}
