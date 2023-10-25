package gov.nasa.pds.harvest.search.crawler.actions;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerAction;
import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerActionPhases;
import gov.nasa.jpl.oodt.cas.crawl.structs.exceptions.CrawlerActionException;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.pds.harvest.search.constants.Constants;
import gov.nasa.pds.harvest.search.logging.ToolsLevel;
import gov.nasa.pds.harvest.search.logging.ToolsLogRecord;

/**
 * Pre-ingest Crawler Action that checks to see if the logical identifier
 * of a PDS4 data product contains URN reserved and/or excluded characters.
 *
 * @author mcayanan
 *
 */
public class LidCheckerAction extends CrawlerAction {
  /** Logger object. */
  private static Logger log = Logger.getLogger(
          CreateAccessUrlsAction.class.getName());

  /** The list of URN reserved and/or excluded characters. */
  private final static String URN_ILLEGAL_CHARACTERS = "[^%/\\\\?#\"&<>\\[\\]\\^`\\{\\|\\}~]*";

  /** Crawler action id. */
  private final static String ID = "LidCheckerAction";

  /** Crawler action description. */
  private final static String DESCRIPTION = "Checks to see if the logical "
    + "identifier contains urn reserved and/or excluded characters.";

  /**
   * Constructor.
   *
   */
  public LidCheckerAction() {
    super();
    String []phases = {CrawlerActionPhases.PRE_INGEST};
    setPhases(Arrays.asList(phases));
    setId(ID);
    setDescription(DESCRIPTION);
  }

  /**
   * Performs the crawler action that looks for URN reserved and
   *  excluded characters within a lid.
   *
   * @param product The product file.
   * @param metadata The product metadata.
   *
   * @return true if there are no URN reserved and/or excluded characters
   *  in the lid.
   *
   *  @throws CrawlerActionException None thrown.
   */
  public boolean performAction(File product, Metadata metadata)
      throws CrawlerActionException {
    boolean passFlag = true;
    if (metadata.containsKey(Constants.LOGICAL_ID)) {
      String lid = metadata.getMetadata(Constants.LOGICAL_ID);
      if (!lid.matches(URN_ILLEGAL_CHARACTERS)) {
        log.log(new ToolsLogRecord(ToolsLevel.SEVERE, "Lid contains URN "
            + "reserved and/or excluded characters: " + lid, product));
        passFlag = false;
      }
      for (String badEnding : Arrays.asList(new String[]{".xml", ".json"})) {
        if (lid.endsWith(badEnding)) {
          log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
              "Lid cannot end in '" + badEnding + "': " + lid, product));
          passFlag = false;
        }
      }
    }
    return passFlag;
  }
}
