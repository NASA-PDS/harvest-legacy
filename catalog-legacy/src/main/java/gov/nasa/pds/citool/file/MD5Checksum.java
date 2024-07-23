// $Id: MD5Checksum.java 9285 2011-08-08 22:25:41Z mcayanan $
package gov.nasa.pds.citool.file;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * A class that calculates the MD5 checksum of a file.
 *
 * @author mcayanan
 *
 */
public class MD5Checksum {

  /** HEX values. */
  private static final String HEXES = "0123456789abcdef";


  /**
   * Gets the MD5 checksum value.
   *
   * @param filename The filename.
   * @return The MD5 checksum of the given filename.
   *
   * @throws Exception If an error occurred while calculating the checksum.
   */
  public static String getMD5Checksum(String filename) throws Exception {
    byte[] b = createChecksum(filename);
    return getHex(b);
  }

  /**
   * Creates the checksum.
   *
   * @param filename The filename.
   *
   * @return a byte array of the checksum.
   *
   * @throws Exception If an error occurred while calculating the checksum.
   */
  private static byte[] createChecksum(String filename) throws Exception {
    InputStream input = null;
    try {
      input =  new FileInputStream(filename);
      byte[] buffer = new byte[1024];
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      int bytesRead = 0;
      do {
        bytesRead = input.read(buffer);
        if (bytesRead > 0) {
          md5.update(buffer, 0, bytesRead);
        }
      } while (bytesRead != -1);
      return md5.digest();
    } finally {
      input.close();
    }
  }

  /**
   * Gets the HEX equivalent of the given byte array.
   *
   * @param bytes The bytes to convert.
   *
   * @return The HEX value of the given byte array.
   */
  private static String getHex(byte [] bytes) {
    if (bytes == null) {
      return null;
    }
    final StringBuilder hex = new StringBuilder(2 * bytes.length);
    for (byte b : bytes ) {
      hex.append(HEXES.charAt((b & 0xF0) >> 4))
      .append(HEXES.charAt((b & 0x0F)));
    }
    return hex.toString();
  }
}
