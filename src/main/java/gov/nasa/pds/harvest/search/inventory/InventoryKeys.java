package gov.nasa.pds.harvest.search.inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Class containing XPaths used when reading a PDS4 Inventory file.
 *
 * @author mcayanan
 *
 */
public class InventoryKeys {

  /**
   * XPath to determine the field delimiter being used in the inventory table.
   */
  public static final String FIELD_DELIMITER_XPATH = "//Inventory/field_delimiter";

  /**
   * XPath to determine the field location of the member status field in the
   * inventory table.
   */
  public static final String MEMBER_STATUS_FIELD_NUMBER_XPATH =
      "//Inventory/Record_Delimited/Field_Delimited[name='Member_Status' or name='Member Status']/field_number";

  /**
   * XPath to determine the field location of the LID-LIDVID field in the
   * inventory table.
   */
  public static final String LIDVID_LID_FIELD_NUMBER_XPATH =
    "//Inventory/Record_Delimited/Field_Delimited[data_type='ASCII_LIDVID_LID']/field_number";

  /** XPath to the external table file of a collection. */
  public static final String DATA_FILE_XPATH = "//*[starts-with(name(),"
    + "'File_Area')]/File/file_name";

  /** XPath to grab the Member_Entry tags in a bundle. */
  public static final String MEMBER_ENTRY_XPATH =
    "//*[ends-with(name(),'Member_Entry')]";

  /** The MD5 checksum XPath in an Inventory file. */
  public static final String CHECKSUM_XPATH = "md5_checksum";

  /** The member status XPath in an Inventory file. */
  public static final String MEMBER_STATUS_XPATH = "member_status";

  /** The LID-VID or LID XPath for an association. */
  public static final String IDENTITY_REFERENCE_XPATH =
      "lidvid_reference | lid_reference";

  public static final Map<String, String> fieldDelimiters =
    new HashMap<String, String>();

  static {
    fieldDelimiters.put("comma",",");
    fieldDelimiters.put("horizontal_tab", "\\t");
    fieldDelimiters.put("semicolon", ";");
    fieldDelimiters.put("vertical_bar", "\\|");
  }
}
