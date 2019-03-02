package nl.peterbjornx.intelme.model.cfg;

import nl.peterbjornx.intelme.io.ByteBufTools;

import java.nio.ByteBuffer;

public class ConfigRecord {
    private String filename;
    private int reserved = 0;
    private int mode;
    private int opt;
    private int length;
    private int uid;
    private int gid;
    private int start;

    private boolean integrity;
    private boolean encrypted;
    private boolean antiReplay;
    private boolean directory;

    public ConfigRecord(ByteBuffer data) {
        decode(data);
    }

    public ConfigRecord() {

    }

    public ConfigRecord createUpNode() {
        ConfigRecord up = new ConfigRecord();
        up.filename = "..";
        up.mode = mode;
        up.opt = 0;//opt;
        up.length = length;
        up.uid = uid;
        up.gid = gid;
        up.start = start;
        up.integrity = integrity;
        up.encrypted = encrypted;
        up.antiReplay = antiReplay;
        up.directory = directory;
        return up;
    }

    public void decode ( ByteBuffer buffer ) {
        filename = ByteBufTools.readCString(buffer,12);
        reserved = buffer.getShort();
        mode = buffer.getShort() & 0xFFFF;
        opt = buffer.getShort() & 0xFFFF;
        length = buffer.getShort() & 0xFFFF;
        uid = buffer.getShort() & 0xFFFF;
        gid = buffer.getShort() & 0xFFFF;
        start = buffer.getInt();
        integrity = (mode & (1 << 9)) != 0;
        encrypted = (mode & (1 << 10)) != 0;
        antiReplay = (mode & (1 << 11)) != 0;
        directory = (mode & (1 << 12)) != 0;
        mode &= 0777;
        assert reserved == 0;
        assert start == 5660 || !directory;

    }

    public void encode(ByteBuffer data) {
        int bits = 0;
        if ( integrity )
            bits |= (1 << 9);
        if ( encrypted )
            bits |= (1 << 10);
        if ( antiReplay )
            bits |= (1 << 11);
        if ( directory )
            bits |= (1 << 12);

        ByteBufTools.putCString(data,filename,12);//4
        data.putShort((short)reserved);//10
        data.putShort((short)(mode|bits));//12
        data.putShort((short)opt);//14
        data.putShort((short)length);//16
        data.putShort((short)uid);//18
        data.putShort((short)gid);//1c
        data.putInt(start);//22
    }

    public String getFilename() {
        return filename;
    }

    public int getReserved() {
        return reserved;
    }

    public int getMode() {
        return mode;
    }

    public int getOpt() {
        return opt;
    }

    public int getLength() {
        return length;
    }

    public int getUid() {
        return uid;
    }

    public int getGid() {
        return gid;
    }

    public int getStart() {
        return start;
    }

    public boolean isIntegrity() {
        return integrity;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public boolean isAntiReplay() {
        return antiReplay;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setOpt(int opt) {
        this.opt = opt;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setIntegrity(boolean integrity) {
        this.integrity = integrity;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public void setAntiReplay(boolean antiReplay) {
        this.antiReplay = antiReplay;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        return "ConfigRecord{" +
                "filename='" + filename + '\'' +
                ", reserved=" + reserved +
                ", mode=" + mode +
                ", opt=" + opt +
                ", length=" + length +
                ", uid=" + uid +
                ", gid=" + gid +
                ", start=" + start +
                ", integrity=" + integrity +
                ", encrypted=" + encrypted +
                ", antiReplay=" + antiReplay +
                ", directory=" + directory +
                '}';
    }
}
