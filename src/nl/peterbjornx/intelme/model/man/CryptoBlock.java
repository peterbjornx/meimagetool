package nl.peterbjornx.intelme.model.man;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class CryptoBlock {

    private BigInteger PublicKey;
    private BigInteger Exponent;
    private BigInteger Signature;

    public CryptoBlock( ByteBuffer buf ) {
        byte[] pk = new byte[256];
        byte[] ex = new byte[4];
        byte[] sg = new byte[256];
        buf.get(pk);
        buf.get(ex);
        buf.get(sg);
        PublicKey = new BigInteger(pk);//TODO: Figure out endianness
        Exponent = new BigInteger(ex);//TODO: Figure out endianness
        Signature = new BigInteger(sg);//TODO: Figure out endianness
    }

    public BigInteger getPublicKey() {
        return PublicKey;
    }

    public BigInteger getExponent() {
        return Exponent;
    }

    public BigInteger getSignature() {
        return Signature;
    }
}
