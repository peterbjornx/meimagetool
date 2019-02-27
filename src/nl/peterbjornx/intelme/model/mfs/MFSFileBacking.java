package nl.peterbjornx.intelme.model.mfs;

import nl.peterbjornx.intelme.util.MFSException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MFSFileBacking implements MFSBackingStore {

    private RandomAccessFile file;

    public MFSFileBacking(File _file) throws FileNotFoundException {
        file = new RandomAccessFile( _file, "rw" );
    }

    @Override
    public void Write(int pos, byte[] data, int offset, int count) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void Write(int pos, ByteBuffer buffer) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void Read(int pos, byte[] data, int offset, int count) throws MFSException {
        try {
            file.seek(pos);
            file.read(data,offset,count);
        } catch (IOException e) {
            throw new MFSException(e);
        }
    }

    @Override
    public ByteBuffer Read(int pos, int count) throws MFSException {
        byte[] temp = new byte[count];
        Read(pos, temp, 0, count);
        return ByteBuffer.wrap(temp).order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public void Erase(int page) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public int Size() throws MFSException {
        try {
            return (int) file.length();
        } catch (IOException e) {
            throw new MFSException(e);
        }
    }
}
