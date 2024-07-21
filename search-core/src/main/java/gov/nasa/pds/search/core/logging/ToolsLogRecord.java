// $Id: ToolsLogRecord.java 7917 2010-10-21 21:03:42Z mcayanan $

package gov.nasa.pds.search.core.logging;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ToolsLogRecord extends LogRecord {
	private static final long serialVersionUID = 1773070873765120569L;
	
	private String filename;
    private int line;

    public ToolsLogRecord(Level level, String message) {
        this(level, message, null, -1);
    }

    public ToolsLogRecord(Level level, File filename) {
        this(level, "", filename.toString(), -1);
    }

    public ToolsLogRecord(Level level, String message, String filename) {
        this(level, message, filename, -1);
    }

    public ToolsLogRecord(Level level, String message, File filename) {
        this(level, message, filename.toString(), -1);
    }

    public ToolsLogRecord(Level level, String message, String filename,
            int line) {
        super(level, message);
        this.filename = filename;
        this.line = line;
    }

    public String getFilename() {
        return filename;
    }

    public int getLine() {
        return line;
    }
}
