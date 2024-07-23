// $Id: ToolInfo.java 8162 2010-11-10 22:05:09Z mcayanan $

package gov.nasa.pds.citool.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Class to get tool release information.
 *
 * @author mcayanan
 * @version $Id$
 */
public class ToolInfo {
    public static final String FILE = "catalog.properties";

    public static final String NAME = "catalog.name";

    public static final String VERSION = "catalog.version";

    public static final String RELEASE_DATE = "catalog.date";

    public static final String COPYRIGHT = "catalog.copyright";

    private static final Properties props = new Properties();

    static {
        try {
        URL propertyFile = ToolInfo.class.getResource(FILE);
         InputStream in = propertyFile.openStream();
         props.load(in);
        } catch (IOException io) {
            throw new RuntimeException(io.getMessage());
        }
    }

    /**
     * Get the name of the tool.
     *
     * @return The tool name.
     */
    public static String getName() {
        return props.getProperty(NAME);
    }

    /**
     * Get the version.
     *
     * @return The tool version.
     */
    public static String getVersion() {
        return props.getProperty(VERSION);
      }

    /**
     * Get the release date.
     *
     * @return The tool release date.
     */
    public static String getReleaseDate() {
        return props.getProperty(RELEASE_DATE);
    }

    /**
     * Get copyright information.
     *
     * @return The copyright info.
     */
    public static String getCopyright() {
        return props.getProperty(COPYRIGHT);
      }
}
