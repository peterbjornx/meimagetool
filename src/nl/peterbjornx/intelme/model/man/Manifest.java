package nl.peterbjornx.intelme.model.man;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class Manifest {
    private ManifestHeader      Header;
    private CryptoBlock         Crypto;
    private ManifestExtension[] Extensions;

    public Manifest(ByteBuffer buf, boolean header){
        int i = buf.position();
        Header = header ? new ManifestHeader( buf ) : null;
        Crypto = header ? new CryptoBlock( buf ) : null;
        LinkedList<ManifestExtension> x =new LinkedList<>();
        int sz = buf.limit();
        if ( header )
            sz = Header.getSize() * 4;
        int e = sz + i;
        for ( int p = buf.position(); p < e; p = buf.position() )
            x.add(ManifestExtension.decode(buf));
        Extensions = x.toArray(new ManifestExtension[0]);
    }

    public ManifestHeader getHeader() {
        return Header;
    }

    public CryptoBlock getCrypto() {
        return Crypto;
    }

    public ManifestExtension[] getExtensions() {
        return Extensions;
    }
}
