package nl.peterbjornx.intelme.io;

import com.thoughtworks.xstream.XStream;
import nl.peterbjornx.intelme.model.MERegionHeader;
import nl.peterbjornx.intelme.model.cfg.ConfigArchive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class XmlTools {
    private static XStream ourXstream;
    static {
        ourXstream = new XStream();
        ourXstream.autodetectAnnotations(true);
        ourXstream.processAnnotations(MERegionHeader.class);
        ourXstream.processAnnotations(ConfigArchive.class);
    }
    public static void SerializeXML( Object o, File file ) throws FileNotFoundException {

        ourXstream.toXML( o, new FileOutputStream( file ) );
    }

    public static Object DeserializeXML( File file ) {
        return ourXstream.fromXML(file);
    }
}
