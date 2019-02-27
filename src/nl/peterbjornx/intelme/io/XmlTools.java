package nl.peterbjornx.intelme.io;

import com.thoughtworks.xstream.XStream;
import nl.peterbjornx.intelme.model.MERegionHeader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class XmlTools {
    private static XStream ourXstream;
    static {
        ourXstream = new XStream();
        ourXstream.autodetectAnnotations(true);
        ourXstream.processAnnotations(MERegionHeader.class);
    }
    public static void SerializeXML( Object o, File file ) throws FileNotFoundException {

        ourXstream.toXML( o, new FileOutputStream( file ) );
    }

    public static Object DeserializeXML( File file ) {
        return ourXstream.fromXML(file);
    }
}
