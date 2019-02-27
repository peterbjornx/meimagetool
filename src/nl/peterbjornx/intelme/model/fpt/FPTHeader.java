package nl.peterbjornx.intelme.model.fpt;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import nl.peterbjornx.intelme.io.ByteBufTools;
import nl.peterbjornx.intelme.util.MEFormatException;

import java.nio.ByteBuffer;

@XStreamAlias("FptHeader")
public class FPTHeader {

    private static final String FPT_MARKER = "$FPT";
    private static final int FPT_HEADER_VERSION = 32;
    private static final int FPT_ENTRY_VERSION = 16;
    public  static final int FPT_HEADER_LENGTH = 32;
    @XStreamOmitField
    private String HeaderMarker;
    @XStreamOmitField
    private int    NumFptEntries;
    @XStreamOmitField
    private int    HeaderVersion;
    @XStreamOmitField
    private int    EntryVersion;
    @XStreamOmitField
    private int    HeaderLength;
    @XStreamOmitField
    private int    HeaderChecksum;
    private int    TicksToAdd;
    private int    TokensToAdd;
    @XStreamOmitField
    private int    reserved;
    private int    FlashLayout;
    private int    FitcMajor;
    private int    FitcMinor;
    private int    FitcHotfix;
    private int    FitcBuild;

    public FPTHeader() {
        HeaderMarker = FPT_MARKER;
        HeaderVersion = FPT_HEADER_VERSION;
        EntryVersion = FPT_ENTRY_VERSION;
        HeaderLength = FPT_HEADER_LENGTH;
    }

    private void ensureDefaultValues() {
        if ( HeaderMarker != null )
            return;
        HeaderMarker = FPT_MARKER;
        HeaderVersion = FPT_HEADER_VERSION;
        EntryVersion = FPT_ENTRY_VERSION;
        HeaderLength = FPT_HEADER_LENGTH;
        reserved = 0;

    }

    public FPTHeader( ByteBuffer buf )
    {
        HeaderMarker = ByteBufTools.readCString(buf,4);
        NumFptEntries = buf.getInt();
        HeaderVersion = buf.get() & 0xFF;
        EntryVersion = buf.get() & 0xFF;
        HeaderLength = buf.get() & 0xFF;
        HeaderChecksum = buf.get() & 0xFF;
        TicksToAdd = buf.getShort();
        TokensToAdd = buf.getShort();
        reserved = buf.getInt();
        FlashLayout = buf.getInt();
        FitcMajor = buf.getShort() & 0xFFFF;
        FitcMinor = buf.getShort() & 0xFFFF;
        FitcHotfix = buf.getShort() & 0xFFFF;
        FitcBuild = buf.getShort() & 0xFFFF;
    }

    public void encode( ByteBuffer buf ) {
        ensureDefaultValues();
        ByteBufTools.putCString( buf, HeaderMarker, 4 );
        buf.putInt(NumFptEntries);
        buf.put((byte) HeaderVersion);
        buf.put((byte) EntryVersion);
        buf.put((byte) HeaderLength);
        buf.put((byte) HeaderChecksum);
        buf.putShort((short) TicksToAdd);
        buf.putShort((short) TokensToAdd);
        buf.putInt(reserved);
        buf.putInt(FlashLayout);
        buf.putShort((short) FitcMajor);
        buf.putShort((short) FitcMinor);
        buf.putShort((short) FitcHotfix);
        buf.putShort((short) FitcBuild);
    }

    private int calculateChecksum() {
        ByteBuffer buf = ByteBuffer.allocate( FPTHeader.FPT_HEADER_LENGTH );
        encode(buf);
        buf.rewind();
        return ByteBufTools.ByteChecksum(buf, buf.limit());
    }

    public void updateChecksum() {
        int sum = calculateChecksum();
        HeaderChecksum = (HeaderChecksum - sum) & 0xFF;
    }

    public void verify() throws MEFormatException {
        if (!HeaderMarker.equals(FPT_MARKER))
            throw new MEFormatException("FPT HeaderMarker invalid: "+HeaderMarker);
        if ( HeaderVersion != FPT_HEADER_VERSION )
            throw new MEFormatException("FPT HeaderVersion invalid: "+HeaderVersion);
        if ( EntryVersion != FPT_ENTRY_VERSION )
            throw new MEFormatException("FPT EntryVersion invalid: "+EntryVersion);
        if ( HeaderLength != FPT_HEADER_LENGTH )
            throw new MEFormatException("FPT HeaderLength invalid: "+HeaderLength);
        int sum = calculateChecksum();
        if ( sum != 0 )
            throw new MEFormatException("FPT HeaderChecksum did not match: "+sum);
    }

    public String getHeaderMarker() {
        return HeaderMarker;
    }

    public int getNumFptEntries() {
        return NumFptEntries;
    }

    public int getHeaderVersion() {
        return HeaderVersion;
    }

    public int getEntryVersion() {
        return EntryVersion;
    }

    public int getHeaderLength() {
        return HeaderLength;
    }

    public int getHeaderChecksum() {
        return HeaderChecksum;
    }

    public int getTicksToAdd() {
        return TicksToAdd;
    }

    public int getTokensToAdd() {
        return TokensToAdd;
    }

    public int getReserved() {
        return reserved;
    }

    public int getFlashLayout() {
        return FlashLayout;
    }

    public int getFitcMajor() {
        return FitcMajor;
    }

    public int getFitcMinor() {
        return FitcMinor;
    }

    public int getFitcHotfix() {
        return FitcHotfix;
    }

    public int getFitcBuild() {
        return FitcBuild;
    }

    public void setNumFptEntries(int numFptEntries) {
        NumFptEntries = numFptEntries;
    }

    public void setTicksToAdd(int ticksToAdd) {
        TicksToAdd = ticksToAdd;
    }

    public void setTokensToAdd(int tokensToAdd) {
        TokensToAdd = tokensToAdd;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public void setFlashLayout(int flashLayout) {
        FlashLayout = flashLayout;
    }

    public void setFitcMajor(int fitcMajor) {
        FitcMajor = fitcMajor;
    }

    public void setFitcMinor(int fitcMinor) {
        FitcMinor = fitcMinor;
    }

    public void setFitcHotfix(int fitcHotfix) {
        FitcHotfix = fitcHotfix;
    }

    public void setFitcBuild(int fitcBuild) {
        FitcBuild = fitcBuild;
    }

    @Override
    public String toString() {
        return "FPTHeader{" +
                "HeaderMarker='" + HeaderMarker + '\'' +
                ", NumFptEntries=" + NumFptEntries +
                ", HeaderVersion=" + HeaderVersion +
                ", EntryVersion=" + EntryVersion +
                ", HeaderLength=" + HeaderLength +
                ", HeaderChecksum=" + HeaderChecksum +
                ", TicksToAdd=" + TicksToAdd +
                ", TokensToAdd=" + TokensToAdd +
                ", reserved=" + reserved +
                ", FlashLayout=" + FlashLayout +
                ", FitcMajor=" + FitcMajor +
                ", FitcMinor=" + FitcMinor +
                ", FitcHotfix=" + FitcHotfix +
                ", FitcBuild=" + FitcBuild +
                '}';
    }
}
