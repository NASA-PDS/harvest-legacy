package gov.nasa.pds.harvest.search.file;

import java.util.ArrayList;
import java.util.List;

import org.apache.tika.Tika;

/**
 * Class that contains file information to be used in registering file objects
 * to the PDS4 Registry.
 *
 * @author mcayanan
 *
 */
public class FileObject {
  /** File name. */
  private String name;

  /** File location. */
  private String location;

  /** File size. */
  private FileSize size;

  /** File creation date time. */
  private String creationDateTime;

  /** md5 checksum. */
  private String checksum;

  /** The product identifier when registered to the PDS Storage Service. */
  private String storageServiceProductId;

  /** Access urls to the file object. */
  private List<String> accessUrls;

  private String mimeType;

  private String fileType;
  
  /**
   * Constructor.
   *
   * @param name File name.
   * @param location File location.
   * @param size File size.
   * @param creationDateTime File creation date time.
   * @param checksum checksum of the file.
   */
  public FileObject(String name, String location, FileSize size,
      String creationDateTime, String checksum, String fileType) {
    this.name = name;
    this.location = location;
    this.size = size;
    this.creationDateTime = creationDateTime;
    this.checksum = checksum;
    this.storageServiceProductId = null;
    this.accessUrls = new ArrayList<String>();
    this.fileType = fileType;
    this.mimeType = new Tika().detect(name);
  }

  public String getName() {return name;}

  public String getLocation() {return location;}

  public FileSize getSize() {return size;}

  public String getCreationDateTime() {return creationDateTime;}

  public String getChecksum() {return checksum;}

  public String getMimeType() {return mimeType;}

  public String getFileType() {return fileType;}

  public void setStorageServiceProductId(String productId) {
    this.storageServiceProductId = productId;
  }

  public String getStorageServiceProductId() {
    return storageServiceProductId;
  }

  public void setAccessUrls(List<String> accessUrls) {
    this.accessUrls = accessUrls;
  }

  public List<String> getAccessUrls() {
    return accessUrls;
  }
}
