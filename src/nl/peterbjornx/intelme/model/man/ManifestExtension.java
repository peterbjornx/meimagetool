package nl.peterbjornx.intelme.model.man;

import java.nio.ByteBuffer;

public class ManifestExtension {
    private int Type;
    private int Length;
    private ByteBuffer Data;

    public ManifestExtension( ByteBuffer buf ) {
        Type = buf.getInt();
        Length = buf.getInt();
        byte[] _data = new byte[Length - 8];
        buf.get(_data);
        Data = ByteBuffer.wrap(_data);
        Data.order(buf.order());
    }

    public int getType() {
        return Type;
    }

    public int getLength() {
        return Length;
    }

    public ByteBuffer getData() {
        return Data;
    }

    public static ManifestExtension decode(ByteBuffer b) {
        int oldPos = b.position();
        int t = b.getInt();
        b.position(oldPos);
        switch( t ) {
            default:
                return new ManifestExtension(b);
        }
    }

}
