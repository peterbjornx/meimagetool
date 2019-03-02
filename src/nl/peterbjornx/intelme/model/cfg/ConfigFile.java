package nl.peterbjornx.intelme.model.cfg;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.nio.ByteBuffer;

@XStreamAlias("CfgFile")
public class ConfigFile {
    @XStreamAsAttribute
    private String path;
    @XStreamAsAttribute
    private int mode;
    @XStreamAsAttribute
    private int opt;
    @XStreamAsAttribute
    private int uid;
    @XStreamAsAttribute
    private int gid;
    @XStreamAsAttribute
    private boolean integrity;
    @XStreamAsAttribute
    private boolean encrypted;
    @XStreamAsAttribute
    private boolean antiReplay;
    @XStreamAsAttribute
    private boolean directory;
    @XStreamOmitField
    private byte[] data;

    public ConfigFile(String path, int mode, int opt, int uid, int gid, boolean integrity, boolean encrypted, boolean antiReplay, boolean directory, byte[] data) {
        this.path = path;
        this.mode = mode;
        this.opt = opt;
        this.uid = uid;
        this.gid = gid;
        this.integrity = integrity;
        this.encrypted = encrypted;
        this.antiReplay = antiReplay;
        this.directory = directory;
        this.data = data;
    }

    public String getPath() {
        return path;
    }

    public String getFilename() {
        String[] pathels = path.split("/");
        return pathels[pathels.length - 1];
    }

    public int getPathDepth() {
        return path.split("/").length;
    }

    public int getMode() {
        return mode;
    }

    public int getOpt() {
        return opt;
    }

    public int getUid() {
        return uid;
    }

    public int getGid() {
        return gid;
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

    public byte[] getData() {
        return data;
    }

    public int getLength() {
        return data.length;
    }

    public void encodeData(ByteBuffer buffer) {
        buffer.put(this.data);
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
