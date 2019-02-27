package nl.peterbjornx.intelme.model.man;

import nl.peterbjornx.intelme.io.ByteBufTools;

import java.nio.ByteBuffer;

public class ManifestHeader {
    private int Type;
    private int Length;
    private int Version;
    private int Flags;
    private int Vendor;
    private int Date;
    private int Size;
    private String HeaderID;
    private int Reserved0;
    private int VersionMajor;
    private int VersionMinor;
    private int VersionHotfix;
    private int VersionBuild;
    private int SecureVersion;
    private long Reserved1;
    private byte[] Reserved2;
    private int ModulusSize;
    private int ExponentSize;

    public ManifestHeader( ByteBuffer buf ) {
        Type = buf.getInt();
        Length = buf.getInt();
        Version = buf.getInt();
        Flags = buf.getInt();
        Vendor = buf.getInt();
        Date = buf.getInt();
        Size = buf.getInt();
        HeaderID = ByteBufTools.readCString(buf,4);
        Reserved0 = buf.getInt();
        VersionMajor = buf.getShort();
        VersionMinor = buf.getShort();
        VersionHotfix = buf.getShort();
        VersionBuild = buf.getShort();
        SecureVersion = buf.getInt();
        Reserved1 = buf.getLong();
        Reserved2 = new byte[64];
        buf.get(Reserved2);
        ModulusSize = buf.getInt();
        ExponentSize = buf.getInt();
    }

    public int getType() {
        return Type;
    }

    public int getLength() {
        return Length;
    }

    public int getVersion() {
        return Version;
    }

    public int getFlags() {
        return Flags;
    }

    public int getVendor() {
        return Vendor;
    }

    public int getDate() {
        return Date;
    }

    public int getSize() {
        return Size;
    }

    public String getHeaderID() {
        return HeaderID;
    }

    public int getReserved0() {
        return Reserved0;
    }

    public int getVersionMajor() {
        return VersionMajor;
    }

    public int getVersionMinor() {
        return VersionMinor;
    }

    public int getVersionHotfix() {
        return VersionHotfix;
    }

    public int getVersionBuild() {
        return VersionBuild;
    }

    public int getSecureVersion() {
        return SecureVersion;
    }

    public long getReserved1() {
        return Reserved1;
    }

    public byte[] getReserved2() {
        return Reserved2;
    }

    public int getModulusSize() {
        return ModulusSize;
    }

    public int getExponentSize() {
        return ExponentSize;
    }
}
