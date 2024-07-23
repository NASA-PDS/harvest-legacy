// $Id: Report.java 6164 2010-03-04 22:11:42Z mcayanan $
//

package gov.nasa.pds.citool.report;

import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants;
import gov.nasa.pds.tools.constants.Constants.Severity;
import gov.nasa.pds.citool.status.Status;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class that represents a Report for the Vtool command line API. This
 * class handles basic utilities for reporting and calling customized portions
 * of reports.
 *
 * @author pramirez
 *
 */
public abstract class Report {
  private int totalWarnings;
  private int totalErrors;
  private int totalInfos;
  private int numSkipped;
  private int numFailed;
  private int numPassed;
  protected final List<String> parameters;
  protected final List<String> configurations;
  private PrintWriter writer;
  private Severity level;

  /**
   * Default constructor to initialize report variables. Initializes default
   * output to System.out if you wish to write the report to a different sourse
   * use the appropriate setOutput method.
   */
  public Report() {
    this.totalWarnings = 0;
    this.totalErrors = 0;
    this.totalInfos = 0;
    this.numFailed = 0;
    this.numPassed = 0;
    this.numSkipped = 0;
    this.parameters = new ArrayList<String>();
    this.configurations = new ArrayList<String>();
    this.writer = new PrintWriter(new OutputStreamWriter(System.out));
    this.level = Severity.INFO;
  }

  /**
   * Handles writing a Report to the writer interface. This is is useful if
   * someone would like to put the contents of the Report to something such as
   * {@link java.io.StringWriter}.
   *
   * @param writer
   *          which the report will be written to
   */
  public final void setOutput(Writer writer) {
    this.writer = new PrintWriter(writer);
  }

  /**
   * Handle writing a Report to an {@link java.io.OutputStream}. This is useful
   * to get the report to print to something such as System.out
   *
   * @param os
   *          stream which the report will be written to
   */
  public final void setOutput(OutputStream os) {
    this.setOutput(new OutputStreamWriter(os));
  }

  /**
   * Handles writing a Report to a {@link java.io.File}.
   *
   * @param file
   *          which the report will output to
   * @throws IOException
   *           if there is an issue in writing the report to the file
   */
  public final void setOutput(File file) throws IOException {
    this.setOutput(new FileWriter(file));
  }

  public final PrintWriter getOutput() {
      return this.writer;
  }

  /**
   * This method will display the default header for the Vtool command line
   * library reports. This is the standard header across all reports.
   */
  public void printHeader() {
    printHeader(this.writer);
  }

  /**
   * Adds the string supplied to the parameter section in the heading of the
   * report.
   *
   * @param parameter
   *          in a string form that represents something that was passed in when
   *          the tool was run
   */
  public void addParameter(String parameter) {
    this.parameters.add(parameter);
  }

  /**
   * Adds the string supplied to the configuration section in the heading of the
   * report.
   *
   * @param configuration
   *          in a string form that represents a configuration that was used
   *          during parsing and validation
   */
  public void addConfiguration(String configuration) {
    this.configurations.add(configuration);
  }

  /**
   * Allows a Report to customize the header portion of the Report if necessary.
   *
   * @param writer
   *          passed down to write header contents to
   */
  protected abstract void printHeader(PrintWriter writer);

  public Status record(File source, final List<LabelParserException> problems) {
      return record(source.toURI(), problems);
  }

  /**
   * Allows a report to change how they manage reporting on a given file that
   * has been parsed and validated. Also handles generating a status for a file
   * and generating some summary statistics.
   *
   * @param sourceUri
   *          reference to the file that is being reported on
   * @param problems
   *          the set of issues found with the file. to be reported on
   * @return status of the file (i.e. PASS, FAIL, or SKIP)
   */
  public Status record(URI sourceUri, final List<LabelParserException> problems) {
    Status status = Status.PASS;
    int numErrors = 0;
    int numWarnings = 0;
    int numInfos = 0;

    // TODO: Handle null problems

    for (LabelParserException problem : problems) {
      if (problem.getType().getSeverity() == Constants.Severity.ERROR) {
        if (problem.getType().equals(Constants.ProblemType.INVALID_LABEL)
            || problem.getType().equals(
                Constants.ProblemType.INVALID_LABEL_FRAGMENT)) {
          status = Status.SKIP;
        }
        if (Constants.Severity.ERROR.getValue() <= this.level.getValue()) {
          numErrors++;
        }
      } else if (problem.getType().getSeverity() == Constants.Severity.WARNING) {
        if (Constants.Severity.WARNING.getValue() <= this.level.getValue()) {
          numWarnings++;
        }
      } else if (problem.getType().getSeverity() == Constants.Severity.INFO) {
        if (Constants.Severity.INFO.getValue() <= this.level.getValue()) {
          numInfos++;
        }
      }
    }
    // Filter out the problems based on severity level
    List<LabelParserException> filteredProblems = this.filterProblems(problems);

    // If the label was skipped we don't want to count the errors and warnings
    // in total but we still want them to print.
    if (status == Status.SKIP) {
      this.numSkipped++;
      printRecordMessages(this.writer, status, sourceUri, filteredProblems);
      return status;
    }

    this.totalErrors += numErrors;
    this.totalInfos += numInfos;
    this.totalWarnings += numWarnings;

    if (numErrors > 0) {
      this.numFailed++;
      status = Status.FAIL;
    } else {
      this.numPassed++;
    }
    printRecordMessages(this.writer, status, sourceUri, filteredProblems);

    return status;
  }

  protected List<LabelParserException> filterProblems(
      List<LabelParserException> problems) {
    List<LabelParserException> filteredProblems = new ArrayList<LabelParserException>();
    for (LabelParserException problem : problems) {
      if (problem.getType().getSeverity().getValue() <= this.level.getValue()) {
        filteredProblems.add(problem);
      }
    }
    return filteredProblems;
  }

  /**
   * Allows a report to change how they manage reporting on a given file that
   * has been parsed and validated. Also handles generating a status for a file
   * and generating some summary statistics.
   *
   * @param sourceUris
   *          reference to the file that is being reported on
   * @param problems
   *          the set of issues found with the file. to be reported on
   * @return status of the file (i.e. PASS, FAIL, or SKIP)
   */
  public Status record(List<String> sourceUris, final List<LabelParserException> problems) {
    Status status = Status.PASS;
    int numErrors = 0;
    int numWarnings = 0;
    int numInfos = 0;

    // TODO: Handle null problems

    for (LabelParserException problem : problems) {
      if (problem.getType().getSeverity() == Constants.Severity.ERROR) {
        if (problem.getType().equals(Constants.ProblemType.INVALID_LABEL)
            || problem.getType().equals(
                Constants.ProblemType.INVALID_LABEL_FRAGMENT)) {
          status = Status.SKIP;
        }
        if (Constants.Severity.ERROR.getValue() <= this.level.getValue()) {
          numErrors++;
        }
      } else if (problem.getType().getSeverity() == Constants.Severity.WARNING) {
        if (Constants.Severity.WARNING.getValue() <= this.level.getValue()) {
          numWarnings++;
        }
      } else if (problem.getType().getSeverity() == Constants.Severity.INFO) {
        if (Constants.Severity.INFO.getValue() <= this.level.getValue()) {
          numInfos++;
        }
      }
    }
    // Filter out the problems based on severity level
    List<LabelParserException> filteredProblems = this.filterProblems(problems);

    // If the label was skipped we don't want to count the errors and warnings
    // in total but we still want them to print.
    if (status == Status.SKIP) {
      this.numSkipped++;
      printRecordMessages(this.writer, status, sourceUris, filteredProblems);
      return status;
    }

    this.totalErrors += numErrors;
    this.totalInfos += numInfos;
    this.totalWarnings += numWarnings;

    if (numErrors > 0) {
      this.numFailed++;
      status = Status.FAIL;
    } else {
      this.numPassed++;
    }
    printRecordMessages(this.writer, status, sourceUris, filteredProblems);

    return status;
  }


  public Status recordSkip(final URI sourceUri, final Exception exception) {
    this.numSkipped++;
    if (exception instanceof LabelParserException) {
      LabelParserException problem = (LabelParserException) exception;
      if (problem.getType().getSeverity().getValue() <= this.level.getValue()) {
        printRecordSkip(this.writer, sourceUri, exception);
      }
    } else {
      printRecordSkip(this.writer, sourceUri, exception);
    }
    return Status.SKIP;
  }

  public Status recordSkip(final List<String> sourceUris,
          final Exception exception) {
      this.numSkipped++;
      if (exception instanceof LabelParserException) {
        LabelParserException problem = (LabelParserException) exception;
        if (problem.getType().getSeverity().getValue() <= this.level.getValue()) {
          printRecordSkip(this.writer, sourceUris, exception);
        }
      } else {
        printRecordSkip(this.writer, sourceUris, exception);
      }
      return Status.SKIP;
  }

  protected void printRecordSkip(PrintWriter writer, final URI sourceUri,
      final Exception exception) {
    // no op
  }

  protected void printRecordSkip(PrintWriter writer,
          final List<String> sourceUris, final Exception exception) {
        // no op
  }

  /**
   * Allows a report to customize how it handles reporting on a particular
   * label.
   *
   * @param writer
   *          passed on to write customized messages to
   * @param sourceUri
   *          reference to the file that is being reported on
   * @param problems
   *          which to report on for this source
   */
  protected abstract void printRecordMessages(PrintWriter writer,
      final Status status, final URI sourceUri,
      final List<LabelParserException> problems);


  protected abstract void printRecordMessages(PrintWriter writer,
          final Status status, final List<String> sourceUris,
          final List<LabelParserException> problems);

  /**
   * Prints out the footer or the report and calls the customized footer
   * section.
   */
  public void printFooter() {
    printFooter(writer);
    this.writer.flush();
  }

  /**
   * Allows customization of the footer section of the report
   *
   * @param writer
   *          passed on to writer customized footer contents
   */
  protected abstract void printFooter(PrintWriter writer);

  /**
   *
   * @return number of labels that passed (had no errors)
   */
  public int getNumPassed() {
    return this.numPassed;
  }

  /**
   *
   * @return number of labels that failed (had one or more errors)
   */
  public int getNumFailed() {
    return this.numFailed;
  }

  /**
   *
   * @return number of files that were not recognized as a label
   */
  public int getNumSkipped() {
    return this.numSkipped;
  }

  /**
   *
   * @return total number of errors that were found across all labels inspected.
   *         Will not count errors generated from files that were considered
   *         skipped.
   */
  public int getTotalErrors() {
    return this.totalErrors;
  }

  /**
   *
   * @return total number of warning that were found across all labels
   *         inspected. Will not count warnings generated from files that were
   *         considered skipped.
   */
  public int getTotalWarnings() {
    return this.totalWarnings;
  }

  /**
   *
   * @return total number of info messages that were found across all labels
   *         inspected. Will not count info messages from files that were
   *         considered skipped.
   */
  public int getTotalInfos() {
    return this.totalInfos;
  }

  /**
   *
   * @return flag indicating if errors were found in the inspected files
   */
  public boolean hasErrors() {
    return (this.totalErrors > 0) ? true : false;
  }

  /**
   *
   * @return flag indicating if warnings were found in the inspected files
   */
  public boolean hasWarnings() {
    return (this.totalWarnings > 0) ? true : false;
  }

  /**
   * Anything at or above the level will be reported. Default severity level is
   * info and above
   *
   * @param severity
   *          level on which items will be reported
   */
  public void setLevel(Severity severity) {
    this.level = severity;
  }

  /**
   *
   * @return severity level of items that will be reported on. Anything at or
   *         above this level will be reported on
   */
  public Severity getLevel() {
    return this.level;
  }
}
