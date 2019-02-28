package nl.peterbjornx.intelme.model.mfs;

import nl.peterbjornx.intelme.model.mfs.chunk.MFSChunkStore;
import nl.peterbjornx.intelme.util.MFSException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class MFSVolume {

    private final MFSChunkStore ChunkStore;
    private final MFSVolumeHeader Header;
    private int FAT[];

    public MFSVolume( int offset, int size, MFSBackingStore buf ) throws MFSException {
        ChunkStore = new MFSChunkStore( offset, size, buf );

        Header = new MFSVolumeHeader( ChunkStore.ReadBytes(0, 14 ) );
        FAT = new int[ChunkStore.getDataChunkCount() + Header.getFileCount()];

        ByteBuffer fatb = ChunkStore.ReadBytes( 14, FAT.length * 2 );
        for ( int i = 0; i < FAT.length; i++ )
            FAT[i] = fatb.getShort() & 0xFFFF;
    }

    private ByteBuffer EncodeFAT() {
        ByteBuffer fatb = ByteBuffer.allocate(FAT.length * 2).order(ByteOrder.LITTLE_ENDIAN);
        for ( int i = 0; i < FAT.length; i++ )
            fatb.putShort( (short) FAT[i] );
        fatb.position(0);
        return fatb;
    }

    public void SyncMetadata() throws MFSException {
        ChunkStore.WriteSystemBytes(0, Header.encode());
        ChunkStore.WriteSystemBytes(14,EncodeFAT() );
        ChunkStore.SyncMetadata();
    }

    public void CreateFile( int filenum, byte[] buffer ) throws MFSException {
        byte[] tmp = new byte[MFSChunkStore.MFS_CHUNK_SIZE];

        if ( FAT[filenum] != 0x0000 )
            throw new MFSException("File exists");

        int remaining;
        int pos = 0;

        if ( buffer.length == 0 ) {
            FAT[filenum] = 0xFFFF;
            return;
        }
        int link = filenum;
        int blockSize = 1;
        while ( pos != buffer.length ) {
            blockSize = buffer.length - pos;
            if ( blockSize > MFSChunkStore.MFS_CHUNK_SIZE )
                blockSize = MFSChunkStore.MFS_CHUNK_SIZE;
            int chunk = ChunkStore.AllocateDataChunk();
            int nextlink = chunk + Header.getFileCount() - ChunkStore.getSystemChunkCount();
            FAT[link] = nextlink;
            link = nextlink;
            Arrays.fill(tmp,(byte)0xff);
            System.arraycopy(buffer,pos,tmp,0,blockSize);
            ChunkStore.WriteChunk(chunk, tmp);
            pos += blockSize;
        }

        FAT[link] = blockSize;

    }

    public int ReadFile( int filenum, int startPos, byte[] buffer, int offset, int count ) throws MFSException {
        byte[] tmp = new byte[MFSChunkStore.MFS_CHUNK_SIZE];
        int block = FAT[filenum];
        int nextBlock;
        if ( block == 0xFFFF )
            return 0;
        else if ( block == 0x0000 )
            throw new MFSException("File not found");

        int bSize = MFSChunkStore.MFS_CHUNK_SIZE;
        int currentPos = 0;
        int endPos = startPos + count;
        int tCount = 0;
        do {
            nextBlock = FAT[ block ];

            if ( nextBlock <= MFSChunkStore.MFS_CHUNK_SIZE )
                bSize = nextBlock;

            ChunkStore.ReadChunk( block - Header.getFileCount() + ChunkStore.getSystemChunkCount(), tmp );

            int startOffset = startPos - currentPos;
            int tSize       = endPos - currentPos;

            if ( startOffset < 0 )
                startOffset = 0;

            if ( tSize > bSize )
                tSize = bSize;

            tSize -= startOffset;

            if ( startOffset < bSize ) {
                System.arraycopy(tmp, startOffset, buffer, offset + tCount, tSize);
                tCount += tSize;
            }

            currentPos += bSize;

            if ( nextBlock < Header.getFileCount() )
                break;

            block = nextBlock;
        } while ( true );

        return tCount;

    }

    public int getFileSize(int filenum ) throws MFSException {
        int block = FAT[filenum];
        int nextBlock;
        if ( block == 0xFFFF )
            return 0;
        else if ( block == 0x0000 )
            throw new MFSException("File not found");
        int fileSize = 0;
        do {
            nextBlock = FAT[ block ];

            if ( nextBlock <= MFSChunkStore.MFS_CHUNK_SIZE )
                fileSize += nextBlock;
            else
                fileSize += MFSChunkStore.MFS_CHUNK_SIZE;

            if ( nextBlock < Header.getFileCount() )
                break;

            block = nextBlock;
        } while ( true );

        return fileSize;

    }

    public boolean FileExists( int filenum ) {
        return FAT[filenum] != 0x0000;
    }

    public int getFileCount() {
        return Header.getFileCount();
    }
}
