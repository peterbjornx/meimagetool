package nl.peterbjornx.intelme.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;

public class ByteBufTools {

    public static String readCString(ByteBuffer buf, int length){
        byte[] bstr = new byte[length];
        buf.get(bstr);
        int sl = 0; for ( sl=0; sl < length;sl++) if ( bstr[sl] == 0 ) break;
        return new String(bstr,0,sl);
    }

    public static void putCString(ByteBuffer buf, String value, int length) {
        int strlen = value.length();
        if ( strlen > length )
            throw new RuntimeException("Value does not fit string field");
        for ( int i = 0; i < length; i++ ) {
            byte out;
            if ( i < strlen )
                out = (byte)value.charAt(i);
            else
                out = 0;
            buf.put(out);
        }
    }

    public static int ByteChecksum(ByteBuffer buf, int length) {
        byte[] data = new byte[length];
        int sum = 0;
        buf.get(data);
        for ( byte b : data )
            sum = ( sum + ( b & 0xFF ) ) & 0xFF;
        return sum;
    }

    public static ByteBuffer ReadToByteBuffer(String path) throws IOException {
        byte[] data = Files.readAllBytes(Paths.get(path));
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
    }

    public static void WriteFromByteBuffer( ByteBuffer buf, String path ) throws IOException {
        byte[] data = new byte[buf.limit()];
        buf.position(0);
        buf.get(data);
        Files.write(Paths.get(path), data);
    }

}
