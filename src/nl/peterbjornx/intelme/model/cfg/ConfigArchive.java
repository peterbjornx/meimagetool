package nl.peterbjornx.intelme.model.cfg;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import nl.peterbjornx.intelme.io.ByteBufTools;
import nl.peterbjornx.intelme.io.XmlTools;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

@XStreamAlias("CfgArchive")
public class ConfigArchive {
    @XStreamImplicit
    private List<ConfigFile> files = new LinkedList<>();

    public ConfigArchive(File dir) throws IOException {
        ConfigArchive ca = (ConfigArchive) XmlTools.DeserializeXML(new File(dir,"archive.xml"));
        files = ca.files;
        for ( ConfigFile f : files ) {
            if ( f.isDirectory() ) {
                f.setData(new byte[0]);
                continue;
            }
            f.setData(ByteBufTools.ReadToByteBuffer(new File(dir,f.getPath()).getAbsolutePath()).array());
        }
    }

    private class ConfigEntry {
        ConfigRecord record;
        ConfigFile file;

        ConfigEntry(ConfigRecord record, ConfigFile file) {
            this.record = record;
            this.file = file;
        }
    }

    public ConfigArchive( ByteBuffer data ){
        int p = data.position();
        ConfigRecord records[] = new ConfigRecord[ data.getInt() ];
        Stack<String> pathStack = new Stack<>();
        String path;
        for ( int i = 0; i < records.length; i++ )
            records[i] = new ConfigRecord(data);
        for (ConfigRecord record : records) {
            path = String.join("/", pathStack) + (pathStack.empty() ? "" : "/") + record.getFilename();
            if ( record.getFilename().equals("..") ) {
                pathStack.pop();
                continue;
            }
            byte[] d = new byte[record.getLength()];
            data.position(p + record.getStart());
            data.get(d);
            files.add(new ConfigFile(
                    path,
                    record.getMode(),
                    record.getOpt(),
                    record.getUid(),
                    record.getGid(),
                    record.isIntegrity(),
                    record.isEncrypted(),
                    record.isAntiReplay(),
                    record.isDirectory(),
                    d
            ));
            if ( record.isDirectory() )
                pathStack.push(record.getFilename());
        }


    }

    public void export(File destDir) throws IOException {
        if ( destDir.exists() ) {
            throw new IOException("Cowardly refusing to overwrite existing directory!");
        }
        if ( !destDir.mkdir() ) {
            throw new IOException("Could not create output directory!");
        }
        File xmlFile = new File(destDir, "archive.xml");
        XmlTools.SerializeXML(this, xmlFile);
        for( ConfigFile f : files ) {
            File partFile = new File( destDir, f.getPath() );
            if ( f.isDirectory() ) {
                if ( !partFile.mkdir() ) {
                    throw new IOException("Could not create output directory!");
                }
                continue;
            }
            ByteBufTools.WriteFromByteBuffer(ByteBuffer.wrap(f.getData()), partFile.getAbsolutePath() );
        }
    }

    public void compile(File outputFile) throws IOException {
        int size = 0;
        List<ConfigEntry> records = new LinkedList<>();
        int pos = 0;
        Stack<ConfigEntry> pathStack = new Stack<>();
        for ( ConfigFile f : files ) {
            while ( pathStack.size() + 1 > f.getPathDepth() ) {
                records.add(pathStack.pop());
            }
            ConfigRecord record = new ConfigRecord();
            record.setFilename(f.getFilename());
            record.setLength(f.getLength());
            record.setUid(f.getUid());
            record.setGid(f.getGid());
            record.setOpt(f.getOpt());
            record.setMode(f.getMode());
            record.setEncrypted(f.isEncrypted());
            record.setIntegrity(f.isIntegrity());
            record.setAntiReplay(f.isAntiReplay());
            record.setDirectory(f.isDirectory());
            if ( record.isDirectory() )
                record.setStart( 0 );
            else {
                record.setStart(pos);
                pos += record.getLength();
            }
            records.add(new ConfigEntry(record,f));
            if ( f.isDirectory() ) {
                pathStack.push(new ConfigEntry(record.createUpNode(),f));
            }
        }
        while (!pathStack.empty())
            records.add(pathStack.pop());
        int recordCount = records.size();
        int headerSize  = 4 + recordCount * 28;
        for ( ConfigEntry e : records ) {
            int extent = e.record.getStart() + e.record.getLength();
            size = size > extent ? size: extent;
        }
        ByteBuffer data = ByteBuffer.allocate( headerSize + size ).order(ByteOrder.LITTLE_ENDIAN);
        data.position(0);
        data.putInt(recordCount);
        for ( ConfigEntry e : records ) {
            e.record.setStart(e.record.getStart() + headerSize);
            e.record.encode(data);
        }
        for ( ConfigEntry e : records ) {
            if ( e.record.getLength() != 0 ) {
                data.position(e.record.getStart());
                e.file.encodeData( data );
            }
        }

        ByteBufTools.WriteFromByteBuffer( data, outputFile.getPath() );

    }


}
