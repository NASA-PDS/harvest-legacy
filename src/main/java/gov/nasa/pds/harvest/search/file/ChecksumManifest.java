package gov.nasa.pds.harvest.search.file;

import gov.nasa.pds.harvest.search.logging.ToolsLevel;
import gov.nasa.pds.harvest.search.logging.ToolsLogRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;

/**
 * Class that reads a checksum manifest file.
 *
 * @author mcayanan
 *
 */
public class ChecksumManifest {
  /** logger object. */
  private static Logger log = Logger.getLogger(
      ChecksumManifest.class.getName());

  private File basePath;
  
  /**
   * Constructor.
   * 
   * @param basePath A base path for resolving relative file references.
   */
  public ChecksumManifest(String basePath) {
    this.basePath = new File(basePath);
  }
  
  /**
   * Reads a checksum manifest file.
   *
   * @param manifest The checksum manifest.
   *
   * @return A hash map of absolute file pathnames to checksum values.
   *
   * @throws IOException If there was an error reading the checksum manifest.
   */
  public HashMap<File, String> read(File manifest)
  throws IOException {
    HashMap<File, String> checksums = new HashMap<File, String>();
    LineNumberReader reader = new LineNumberReader(new FileReader(manifest));
    String line = "";
    try {
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
          "Processing checksum manifest.", manifest));
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.equals("")) {
          continue;
        }
        String[] tokens = line.split("\\s{1,2}", 2);
        File file = new File(tokens[1]);
        file = new File(FilenameUtils.normalize(file.toString()));
        if (!file.isAbsolute()) {
          file = new File(basePath, file.toString());
        }
        //Normalize the file
        file = new File(FilenameUtils.normalize(file.toString()));
        checksums.put(file, tokens[0]);
        log.log(new ToolsLogRecord(ToolsLevel.DEBUG, "Map contains file '"
            + file.toString() + "' with checksum of '"
            + tokens[0] + "'.", manifest));
      }
    } catch (ArrayIndexOutOfBoundsException ae) {
      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, "Could not tokenize: "
          + line, manifest.toString(), reader.getLineNumber()));
      throw new IOException(ae.getMessage());
    } finally {
      reader.close();
    }
    return checksums;
  }
}
