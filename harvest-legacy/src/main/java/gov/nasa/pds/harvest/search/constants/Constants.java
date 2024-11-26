package gov.nasa.pds.harvest.search.constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import gov.nasa.pds.harvest.search.util.LidVid;
import gov.nasa.pds.registry.model.ExtrinsicObject;

/**
 * Class that holds constants used in Harvest.
 *
 * @author mcayanan
 *
 */
public class Constants {

  /** Directory name where Solr Documents will be stored after data is extracted from data source **/
  public static final String SOLR_DOC_DIR = "solr-docs";
  
  public static final String BUNDLE = "Product_Bundle";

  public static final String COLLECTION = "Product_Collection";

  public static final List<String> CONTEXT_REFS =
      Arrays.asList(
          new String[] {"investigation_ref", "instrument_host_ref", "instrument_ref", "target_ref",
              "facility_ref", "telescope_ref", "observatory_ref", "node_ref", "agency_ref"});

  /** The LID in a product label. */
  public static final String LOGICAL_ID = "logical_identifier";

  /** The VID in a product label. */
  public static final String PRODUCT_VERSION = "version_id";

  /** The object type (product_class) in a product label. */
  public static final String OBJECT_TYPE = "object_type";

  /** The product_class tag in a product label. */
  public static final String PRODUCT_CLASS = "product_class";

  /** The associations in a product label. */
  public static final String REFERENCES = "references";

  /** The File objects of a product label. */
  public static final String FILE_OBJECTS = "file_objects";

  /** The include paths for a PDS3 label. */
  public static final String INCLUDE_PATHS = "include_paths";

  /** The title of a product label. */
  public static final String TITLE = "title";

  /** The unique identifier of a product in the registry. */
  public static final String PRODUCT_GUID = "product_guid";

  /** Metadata key to indicate if an association was verified. */
  public static final String VERIFIED = "verified";

  /** The file name. */
  public static final String FILE_NAME = "file_name";

  /** The file size. */
  public static final String FILE_SIZE = "file_size";

  /** The file location. */
  public static final String FILE_LOCATION = "file_location";

  /** The MD5 checksum of a file. */
  public static final String MD5_CHECKSUM = "md5_checksum";

  /** The creation datetime of a file. */
  public static final String CREATION_DATE_TIME = "creation_date_time";

  /** The access urls to get the registered product. */
  public static final String ACCESS_URLS = "access_url";

  /** The mime type of the registered file object. */
  public static final String MIME_TYPE = "mime_type";

  /** The file type of the registered file object. */
  public static final String FILE_TYPE = "file_type";

  /** The product identifier associated to a product registered with the
   *  PDS Storage Service.
   */
  public static final String STORAGE_SERVICE_PRODUCT_ID = "storage_product_id";

  /** The data classes found in a PDS4 label. */
  public static final String DATA_CLASS = "data_class";

  public static final String FILE_OBJECT_PRODUCT_TYPE = "Product_File_Repository";

  /** The slots to add to a product to be registered. */
  public static final String SLOT_METADATA = "slot_metadata";

  /** The unit attribute stores the units for an element in a PDS4 product
   *  label.
   */
  public static final String UNIT = "unit";

  /** The byte unit value is the de facto unit value when determining file
   * sizes for a registered product.
   */
  public static final String BYTE = "byte";

  /** Page type of the object. */
  public static final String COLLECTION_PAGE_TYPE = "page_type";

  /**
   * The XPath to the data classes in the PDS4 label.
   */
  public static final String DATA_CLASS_XPATH =
    "//File_Area_Observational/*[not(self::File)] | "
    + "/Product_Context/*[not(self::Identification_Area) and not(self::Reference_List) and not(self::Discipline_Area)]";

  /** Holds the XPaths to the expected metadata in a product label. */
  public static final HashMap<String, String> coreXpathsMap =
    new HashMap<String, String>();

  /** The Identification Area XPath in a product label. */
  public static final String IDENTIFICATION_AREA_XPATH =
    "//*[starts-with(name(),'Identification_Area')]";

  /** XPath that will indicate if a collection is primary. */
  public static final String PRIMARY_COLLECTION_XPATH =
    "//Inventory[reference_type='inventory_has_LIDVID_Primary']";

  /** Xpath to indicate if a collection is secondary. */
  public static final String SECONDARY_COLLECTION_XPATH =
    "//Inventory[reference_type='inventory_has_LIDVID_Secondary'] "
    + " | //Inventory[reference_type='inventory_has_LID_Secondary']";

  /**
   * XPath for determining collection page type
   */
  public static final String COLLECTION_PAGE_TYPE_XPATH =
      "if (//Collection/collection_type = 'Document' ) then \"Resource\" else \"Data\"";

  static {
    coreXpathsMap.put(LOGICAL_ID, IDENTIFICATION_AREA_XPATH + "/"
        + LOGICAL_ID);
    coreXpathsMap.put(PRODUCT_VERSION, IDENTIFICATION_AREA_XPATH + "/"
        + PRODUCT_VERSION);
    coreXpathsMap.put(PRODUCT_CLASS, IDENTIFICATION_AREA_XPATH + "/"
        + PRODUCT_CLASS);
    coreXpathsMap.put(TITLE, IDENTIFICATION_AREA_XPATH + "/" + TITLE);
    coreXpathsMap.put(REFERENCES,
        "//*[ends-with(name(),'Member_Entry')]"
        + " | //Internal_Reference | //Source_Product_Internal");
    coreXpathsMap.put(FILE_OBJECTS, "//*[starts-with(name(), 'File_Area')]/"
        + "File | //Document_File");
    coreXpathsMap.put(DATA_CLASS, DATA_CLASS_XPATH);
    coreXpathsMap.put(COLLECTION_PAGE_TYPE, COLLECTION_PAGE_TYPE_XPATH);
  }

  public static final String URN_ILLEGAL_CHARACTERS = "[%/\\\\?#\"&<>\\[\\]^`\\{\\|\\}~]";

  public static List<LidVid> nonPrimaryMembers = new ArrayList<LidVid>();

  public static List<File> collections = new ArrayList<File>();

  public static final int DEFAULT_BATCH_MODE = 50;

  public static final int TITLE_MAX_LENGTH = 255;
  
  public static Map<String, ExtrinsicObject> collectionMap = new LinkedHashMap<String, ExtrinsicObject>();
}
