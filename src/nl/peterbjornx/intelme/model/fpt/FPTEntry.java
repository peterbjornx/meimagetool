package nl.peterbjornx.intelme.model.fpt;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import nl.peterbjornx.intelme.io.ByteBufTools;
import nl.peterbjornx.intelme.util.MEFormatException;

import java.nio.ByteBuffer;


@XStreamAlias("FptEntry")
public class FPTEntry {

    public static final int FPT_ENTRY_SIZE = 32;
    @XStreamAsAttribute
    private String name;
    @XStreamOmitField
    private int reserved = 0;
    @XStreamAsAttribute
    private int offset;
    @XStreamAsAttribute
    private int length;
    @XStreamOmitField
    private int reserved1 = 0;
    @XStreamOmitField
    private int reserved2 = 0;
    @XStreamOmitField
    private int reserved3 = 0;
    @XStreamAsAttribute
    private int type;
    @XStreamOmitField
    private int rsvd0 = 0;
    @XStreamAsAttribute
    private boolean bwl0;
    @XStreamAsAttribute
    private boolean bwl1;
    @XStreamOmitField
    private int rsvd1 = 0;
    @XStreamAsAttribute
    @XStreamAlias("valid")
    private boolean entryValid;

    public FPTEntry() {}

    public FPTEntry(ByteBuffer buf) {
        name = ByteBufTools.readCString(buf,4);
        reserved = buf.getInt();
        offset = buf.getInt();
        length = buf.getInt();
        reserved1 = buf.getInt();
        reserved2 = buf.getInt();
        reserved3 = buf.getInt();
        int attributes = buf.getInt();
        type = attributes & 0x7F;
        rsvd0 = (attributes >> 7) & 0xFF;
        bwl0 = ((attributes >> 15) & 1) != 0;
        bwl1 = ((attributes >> 16) & 1) != 0;
        rsvd1 = (attributes >> 17) & 0x7F;
        int ev = ((attributes >> 24) & 0xFF);
        switch( ev ) {
            case 0:
                entryValid = true;
                break;
            case 255:
                entryValid = false;
                break;
            default:
                throw new RuntimeException("Bad entryvalid value: "+ev);
        }
        if (    reserved != 0 ||
                reserved1 != 0 ||
                reserved2 != 0 ||
                reserved3 != 0 ||
                rsvd0 != 0 ||
                rsvd1 != 0 )
            System.err.println("[ WARN] Nonzero reserved fields found, these will be lost in export!");
    }

    private void ensureDefaultValues() {
        reserved = 0;
        reserved1 = 0;
        reserved2 = 0;
        reserved3 = 0;
        rsvd0 = 0;
        rsvd1 = 0;
    }

    public void encode( ByteBuffer buf ) {
        ensureDefaultValues();
        ByteBufTools.putCString(buf, name, 4);
        buf.putInt(reserved);
        buf.putInt(offset);
        buf.putInt(length);
        buf.putInt(reserved1);
        buf.putInt(reserved2);
        buf.putInt(reserved3);
        int attributes = 0;
        attributes |= type & 0x7F;
        attributes |= (rsvd0 & 0xFF) << 7;
        attributes |= bwl0 ? (1<<15) : 0;
        attributes |= bwl1 ? (1<<16) : 0;
        attributes |= (rsvd1 & 0x7F) << 17;
        attributes |= entryValid ? 0 : 0xFF000000;
        buf.putInt(attributes);
    }

    public String getName() {
        return name;
    }

    public int getReserved() {
        return reserved;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public int getReserved1() {
        return reserved1;
    }

    public int getReserved2() {
        return reserved2;
    }

    public int getReserved3() {
        return reserved3;
    }

    public int getType() {
        return type;
    }

    public int getRsvd0() {
        return rsvd0;
    }

    public boolean isBwl0() {
        return bwl0;
    }

    public boolean isBwl1() {
        return bwl1;
    }

    public int getRsvd1() {
        return rsvd1;
    }

    public boolean getEntryValid() {
        return entryValid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setReserved1(int reserved1) {
        this.reserved1 = reserved1;
    }

    public void setReserved2(int reserved2) {
        this.reserved2 = reserved2;
    }

    public void setReserved3(int reserved3) {
        this.reserved3 = reserved3;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setRsvd0(int rsvd0) {
        this.rsvd0 = rsvd0;
    }

    public void setBwl0(boolean bwl0) {
        this.bwl0 = bwl0;
    }

    public void setBwl1(boolean bwl1) {
        this.bwl1 = bwl1;
    }

    public void setRsvd1(int rsvd1) {
        this.rsvd1 = rsvd1;
    }

    public void setEntryValid(boolean entryValid) {
        this.entryValid = entryValid;
    }

    @Override
    public String toString() {
        return "FPTEntry{" +
                "name='" + name + '\'' +
                ", reserved=" + reserved +
                ", offset=" + offset +
                ", length=" + length +
                ", reserved1=" + reserved1 +
                ", reserved2=" + reserved2 +
                ", reserved3=" + reserved3 +
                ", type=" + type +
                ", rsvd0=" + rsvd0 +
                ", bwl0=" + bwl0 +
                ", bwl1=" + bwl1 +
                ", rsvd1=" + rsvd1 +
                ", entryValid=" + entryValid +
                '}';
    }

    public void verify() throws MEFormatException {

    }
}
