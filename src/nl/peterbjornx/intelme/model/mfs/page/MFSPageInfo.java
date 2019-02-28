package nl.peterbjornx.intelme.model.mfs.page;

import nl.peterbjornx.intelme.util.CrcUtil;
import nl.peterbjornx.intelme.util.MFSException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

    public MFSPageInfo(ByteBuffer buf) throws MFSException {
        decode(buf);
        verify();
    }

    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(18).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(Magic);
        buf.putInt(UpdateSerialNumber);
        buf.putInt(EraseCount);
        buf.putShort((short)NextEraseIndex);
        buf.putShort((short)FirstChunk);
        buf.put((byte) Checksum);
        buf.put((byte) b0);
        buf.position(0);
        return buf;
    }

    private int CalculateChecksum() {
        ByteBuffer buf = encode();
        buf.position(0);
        byte[] data = new byte[16];
        buf.get(data);
        return CrcUtil.Crc8(data, data.length);
    }

    public void UpdateChecksum() {
        Checksum = CalculateChecksum();
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
        if ( Magic == 0 )
            return;
        if ( Magic != MFS_PAGE_MAGIC )
            throw new MFSException("Page magic mismatch");
        if ( UpdateSerialNumber == 0 )
            throw new MFSException("Update serial number is zero");
        if ( EraseCount == 0 )
            throw new MFSException("Erase count is zero");
        if ( Checksum != CalculateChecksum() )
            throw new MFSException("Page info checksum invalid");
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
