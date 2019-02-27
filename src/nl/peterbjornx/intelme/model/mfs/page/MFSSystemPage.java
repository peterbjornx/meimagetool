package nl.peterbjornx.intelme.model.mfs.page;

import nl.peterbjornx.intelme.util.CrcUtil;

import java.nio.ByteBuffer;

public class MFSSystemPage {
    public static final int METADATA_SIZE = 242;
    private final int PageIndex;
    private int FreeIndex = -1;
    private int[] Indices = new int[121];
    private int ScrambledIndices[] = new int[121];

    public MFSSystemPage( int index) {
        PageIndex = index;
    }

    public MFSSystemPage(int index, ByteBuffer buf) {
        PageIndex = index;
        decode(buf);
    }

    public void decode( ByteBuffer buf ) {

        for (int i = 0; i < ScrambledIndices.length; i++ )
            ScrambledIndices[i] = buf.getShort() & 0xFFFF;

        unscrambleIndices();

    }

    private void unscrambleIndices() {
        int lastIndex = 0;
        int j = 0;
        int maxIndex = 0;

        for (int i = 0; i < ScrambledIndices.length; i++ ) {
            if ( (ScrambledIndices[i] & 0xC000) == 0x4000 ) {
                FreeIndex = i;
                continue;
            } else if ( (ScrambledIndices[i] & 0xC000) == 0xC000 ) {
                break;
            }
            lastIndex = (CrcUtil.Crc16_14(lastIndex) ^ ScrambledIndices[i]) & 0xFFFF;
            Indices[j++] = lastIndex;
            if ( lastIndex > maxIndex )
                maxIndex = lastIndex;
        }

        for ( ; j < Indices.length; j++ )
            Indices[j] = -1;

    }

    public int AllocateChunk( int index ) {
        if ( FreeIndex == -1 )
            return -1;
        int lastIndex = 0;
        if ( FreeIndex != 0 )
            lastIndex = Indices[FreeIndex - 1];
        Indices[FreeIndex] = index;
        ScrambledIndices[FreeIndex] = CrcUtil.Crc16_14(lastIndex) ^ Indices[FreeIndex];
        FreeIndex++;
        if ( FreeIndex == Indices.length )
            return FreeIndex-1;
        ScrambledIndices[FreeIndex] &= ~0x8000;
        return FreeIndex-1;
    }

    public int[] getScrambledIndices() {
        return ScrambledIndices;
    }

    public int[] getIndices() {
        return Indices;
    }

    public int getFreeCount() {
        return Indices.length - FreeIndex;
    }

    public int getPageIndex() {
        return PageIndex;
    }
}
