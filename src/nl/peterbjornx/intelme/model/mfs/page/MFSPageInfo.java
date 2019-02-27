package nl.peterbjornx.intelme.model.mfs.page;

import nl.peterbjornx.intelme.util.MFSException;

import java.nio.ByteBuffer;

public class MFSPageInfo {

    public static final int METADATA_SIZE = 18;

    private static final int MFS_PAGE_MAGIC = 0xAA557887;
    private int Magic;
    private int UpdateSerialNumber;
    private int EraseCount;
    private int NextEraseIndex;
    private int FirstChunk;
    private int Checksum;
    private int b0;

    public MFSPageInfo() {

    }

    public MFSPageInfo(ByteBuffer buf) {
        decode(buf);
    }

    public void decode( ByteBuffer buf ) {
        Magic = buf.getInt();//4
        UpdateSerialNumber = buf.getInt();//8
        EraseCount = buf.getInt();//12
        NextEraseIndex = buf.getShort();//14
        FirstChunk = buf.getShort();//16
        Checksum = buf.get() & 0xFF;
        b0 = buf.get();
    }

    public void verify() throws MFSException {
        if ( Magic != MFS_PAGE_MAGIC )
            throw new MFSException("Page magic mismatch");
        if ( UpdateSerialNumber == 0 )
            throw new MFSException("Update serial number is zero");
        if ( EraseCount == 0 )
            throw new MFSException("Erase count is zero");
        //TODO: Verify CRC16 of MFS page header
    }

    public int getMagic() {
        return Magic;
    }

    public int getUpdateSerialNumber() {
        return UpdateSerialNumber;
    }

    public int getEraseCount() {
        return EraseCount;
    }

    public int getNextEraseIndex() {
        return NextEraseIndex;
    }

    public int getFirstChunk() {
        return FirstChunk;
    }

    public int getChecksum() {
        return Checksum;
    }

    public int getB0() {
        return b0;
    }

}
