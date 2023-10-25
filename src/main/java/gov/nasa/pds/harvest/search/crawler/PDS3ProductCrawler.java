package gov.nasa.pds.harvest.search.crawler;

import java.io.File;
import java.util.logging.Logger;

import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.jpl.oodt.cas.metadata.exceptions.MetExtractionException;
import gov.nasa.pds.harvest.search.crawler.metadata.extractor.Pds3MetExtractor;
import gov.nasa.pds.harvest.search.crawler.metadata.extractor.Pds3MetExtractorConfig;
import gov.nasa.pds.harvest.search.logging.ToolsLevel;
import gov.nasa.pds.harvest.search.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.search.stats.HarvestSolrStats;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ManualPathResolver;
import gov.nasa.pds.tools.label.parser.DefaultLabelParser;
import gov.nasa.pds.tools.util.MessageUtils;

/**
 * Class to crawl PDS3 data products.
 *
 * @author mcayanan
 *
 */
public class PDS3ProductCrawler extends PDSProductCrawler {
  /** Logger object. */
  private static Logger log = Logger.getLogger(
          PDS3ProductCrawler.class.getName());

  /** The configuration object for PDS3 product metadata registrations. */
  private Pds3MetExtractorConfig config;

  /** Gets the PDS3 metextractor configuration object.
   *
   * @return Return the configuration object.
   */
  public Pds3MetExtractorConfig getPDS3MetExtractorConfig() {
    return this.config;
  }

  /** Sets the PDS3 metextractor configuration.
   *
   *  @param config A configuration object.
   */
  public void setPDS3MetExtractorConfig(Pds3MetExtractorConfig config) {
    this.config = config;
  }

  /**
   * Extracts metadata from the given product.
   *
   * @param product A PDS file.
   *
   * @return A Metadata object, which holds metadata from the product.
   *
   */
  @Override
  protected Metadata getMetadataForProduct(File product) {
      Pds3MetExtractor metExtractor = new Pds3MetExtractor(config);
      try {
          return metExtractor.extractMetadata(product);
      } catch (MetExtractionException m) {
          log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
                  "Error while gathering metadata: " + m.getMessage(),
                  product));
          return new Metadata();
      }
  }

  /**
   * Determines whether the supplied file passes the necessary
   * pre-conditions for the file to be registered.
   *
   * @param product A file.
   *
   * @return true if the file passes.
   */
  @Override
  protected boolean passesPreconditions(File product) {
    if (inPersistanceMode) {
      if (touchedFiles.containsKey(product)) {
        long lastModified = touchedFiles.get(product);
        if (product.lastModified() == lastModified) {
          return false;
        } else {
          touchedFiles.put(product, product.lastModified());
        }
      } else {
        touchedFiles.put(product, product.lastModified());
      }
    }
    log.log(new ToolsLogRecord(ToolsLevel.INFO, "Begin processing.",
        product));
    boolean passFlag = true;
    ManualPathResolver resolver = new ManualPathResolver();
    resolver.setBaseURI(ManualPathResolver.getBaseURI(product.toURI()));
    DefaultLabelParser parser = new DefaultLabelParser(false, false, resolver);
    Label label = null;
    try {
      label = parser.parseLabel(product.toURI().toURL());
    } catch (LabelParserException lp) {
      passFlag = false;
      ++HarvestSolrStats.numFilesSkipped;
      log.log(new ToolsLogRecord(ToolsLevel.SKIP,
          MessageUtils.getProblemMessage(lp), product));
    } catch (Exception e) {
      passFlag = false;
      ++HarvestSolrStats.numFilesSkipped;
      log.log(new ToolsLogRecord(ToolsLevel.SKIP, e.getMessage(), product));
    }
    if (passFlag == true) {
      ++HarvestSolrStats.numGoodFiles;
    }
    return passFlag;



    //TODO: This reports problems in a PDS3 label. Can we suppress this?
/*
    if (!label.getProblems().isEmpty()) {
      passFlag = false;
      for (LabelParserException problem : label.getProblems()) {
        report(problem, product);
      }
    }
*/
  }

  private void report(LabelParserException problem, File product) {
    String message = MessageUtils.getProblemMessage(problem);
    if ("INFO".equalsIgnoreCase(problem.getType().getSeverity().getName())) {
      log.log(new ToolsLogRecord(ToolsLevel.INFO, message,
          product.toString(),problem.getLineNumber()));
    } else if ("WARNING".equalsIgnoreCase(
        problem.getType().getSeverity().getName())) {
      log.log(new ToolsLogRecord(ToolsLevel.WARNING, message,
          product.toString(),problem.getLineNumber()));
    } else {
      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, message,
          product.toString(),problem.getLineNumber()));
    }
  }
}
