package nl.peterbjornx.intelme.model.mfs;

import nl.peterbjornx.intelme.model.mfs.chunk.MFSChunkStore;
import nl.peterbjornx.intelme.util.MFSException;

import java.nio.ByteBuffer;

public class MFSVolume {

    private final MFSChunkStore ChunkStore;
    private final MFSVolumeHeader Header;
    private int FAT[];

    public MFSVolume( int offset, int size, MFSBackingStore buf ) throws MFSException {
        ChunkStore = new MFSChunkStore( offset, size, buf );

        Header = new MFSVolumeHeader( ChunkStore.ReadBytes(0, 14 ));
        FAT = new int[ChunkStore.getDataChunkCount() + Header.getFileCount()];

        ByteBuffer fatb = ChunkStore.ReadBytes( 14, FAT.length * 2 );
        for ( int i = 0; i < FAT.length; i++ )
            FAT[i] = fatb.getShort();
    }
}
