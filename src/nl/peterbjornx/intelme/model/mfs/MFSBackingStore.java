package nl.peterbjornx.intelme.model.mfs;

import nl.peterbjornx.intelme.util.MFSException;

import java.nio.ByteBuffer;

public interface MFSBackingStore {

    public void Write( int pos, byte[] data, int offset, int count);
    public void Write( int pos, ByteBuffer buffer );
    public void Read ( int pos, byte[] data, int offset, int count) throws MFSException;
    public ByteBuffer Read(int pos, int count ) throws MFSException;
    public void Erase( int page );

}
