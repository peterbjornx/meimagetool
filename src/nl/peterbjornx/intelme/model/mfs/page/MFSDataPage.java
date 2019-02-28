package nl.peterbjornx.intelme.model.mfs.page;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MFSDataPage {

    private int PageIndex;
    private int FreeCount;
    private boolean FreeChunks[] = new boolean[122];
    public static final int METADATA_SIZE = 122;

    public MFSDataPage( int index ) {
        PageIndex = index;
    }

    public MFSDataPage(int i, ByteBuffer buffer) {
        PageIndex = i;
        decode( buffer );
    }

    public void decode( ByteBuffer buffer ) {
        for ( int i = 0; i < FreeChunks.length; i++ ) {
            FreeChunks[i] = buffer.get() == (byte) 0xFF;
            if ( FreeChunks[i] )
                FreeCount++;
        }
    }

    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(METADATA_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        for (boolean FreeChunk : FreeChunks) buf.put(FreeChunk ? (byte) 0xff : 0);
        buf.position(0);
        return buf;
    }

    public int AllocateChunk() {
        for ( int i = 0; i < FreeChunks.length; i++ ) {
            if ( !FreeChunks[i] )
                continue;
            FreeCount--;
            FreeChunks[i] = false;
            return i;
        }
        return -1;
    }

    public boolean[] getFreeChunks() {
        return FreeChunks;
    }

    public int getFreeCount() {
        return FreeCount;
    }

    public int getPageIndex() {
        return PageIndex;
    }
}
