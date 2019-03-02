package nl.peterbjornx.intelme.entry;

import nl.peterbjornx.intelme.io.ByteBufTools;
import nl.peterbjornx.intelme.model.cfg.ConfigArchive;

import java.io.File;
import java.io.IOException;

public class ConfigTool {

    public static void main(String[] args) throws IOException {
        if ( args.length == 1 && (args[0].equals("-h") ||
                    args[0].equals("--help") ||
                    args[0].equals("/?")) ) {
                usage("");
        } else if ( args.length != 3 ) {
            usage("Not enough arguments");
        }

        File bin = new File(args[1]);
        File dir = new File(args[2]);

        ConfigArchive region;

        switch (args[0]) {
            case "x":
                region = new ConfigArchive(ByteBufTools.ReadToByteBuffer(bin.getPath()));
                break;
            case "c":
                region = new ConfigArchive(dir);
                break;
            default:
                usage("Invalid mode");
                return;
        }

        switch (args[0]) {
            case "x":
                region.export(dir);
                break;
            case "c":
                region.compile(bin);
                break;
        }

    }

    private static void usage(String reason) {
        System.err.println(reason);
        System.err.println("Usage: meconfigtool [x|c] <binary> <directory>");
        System.err.println("       meconfigtool   --help");
        System.err.println("       meconfigtool   -h");
        System.err.println("       meconfigtool   /?");
        System.err.println();
        System.err.println("The config archive tool allows compiling and extracting ME config files");
        System.err.println("  The format used by the tool consists of a directory containing a metadata XML");
        System.err.println("  and a separate binary for each file. The names of these binaries must");
        System.err.println("  match the short partition name as configured in the metadata.");
        System.err.println();
        System.err.println("  The modes of operation supported by the tool are:");
        System.err.println("      x     - Extract a archive to its constituent files and metadata.");
        System.err.println("      c     - Create a archive from its constituent files and metadata.");
        System.err.println("     -h     - Displays this message.");
        System.err.println("     /?     - Displays this message.");
        System.err.println("     --help - Displays this message.");
        System.exit(255);
    }
}
