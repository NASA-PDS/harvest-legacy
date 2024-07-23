package gov.nasa.pds.citool.ri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.GroupStatement;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.Statement;

/**
 * Class that finds statements in a PDS label that matches a user supplied
 * list of keywords.
 *
 * @author mcayanan
 *
 */
public class StatementFinder {

    /**
     * Recursively searches down a PDS label to find attributes that matches
     * a user supplied keyword.
     *
     * @param label A PDS label file.
     * @param identifier An identifier to search for in the labels.
     *
     * @return A list of attributes that match the supplied identifier.
     */
    public static List<AttributeStatement> getStatementsRecursively(Label label, String identifier) {
        List<String> identifiers = new ArrayList<String>();
        List<AttributeStatement> statements = new ArrayList<AttributeStatement>();
        identifiers.add(identifier);
        Map<String, List<AttributeStatement>> results = getStatementsRecursively(label, identifiers);
        List<AttributeStatement> list = results.get(identifier);
        if(list != null)
            statements.addAll(list);

        return statements;
    }

    /**
     * Recursively searches down a PDS label to find attributes that match
     * a user supplied list of keywords.
     *
     * @param labels A list of PDS label files.
     * @param identifier An identifier to search for in the labels.
     *
     * @return A list of attributes that match the supplied identifier.
     */
    public static List<AttributeStatement> getStatementsRecursively(List<Label> labels, String identifier) {
        List<String> identifiers = new ArrayList<String>();
        List<AttributeStatement> statements = new ArrayList<AttributeStatement>();
        identifiers.add(identifier);
        Map<String, List<AttributeStatement>> results = getStatementsRecursively(labels, identifiers);
        List<AttributeStatement> list = results.get(identifier);
        if(list != null)
            statements.addAll(list);

        return statements;
    }

    /**
     * Recursively searches down a PDS label to find attributes that match
     * a user supplied list of keywords.
     *
     * @param labels A list of PDS label files.
     * @param identifiers A list of keywords to find in a PDS label.
     *
     * @return A map containing the attribute statements that were found
     * in the PDS label.
     */
    public static Map<String, List<AttributeStatement>> getStatementsRecursively(List<Label> labels, List<String> identifiers) {
        Map<String, List<AttributeStatement>> statements = new LinkedHashMap<String, List<AttributeStatement>>();
        for(Label label : labels) {
            Map<String, List<AttributeStatement>> results = getStatementsRecursively(label, identifiers);
            for(Map.Entry<String, List<AttributeStatement>> entry : results.entrySet()) {
                List<AttributeStatement> list = statements.get(entry.getKey());
                if(list == null) {
                    list = new ArrayList<AttributeStatement>();
                    statements.put(entry.getKey(), list);
                }
                list.addAll(entry.getValue());
            }
        }
        return statements;
    }

    /**
     * Recursively searches down a PDS label to find attributes that match
     * a user supplied list of keywords.
     *
     * @param label An object representation of a PDS label.
     * @param identifiers A list of keywords to find in a PDS label.
     *
     * @return A map containing the attribute statements that were found
     * in the PDS label.
     */
    public static Map< String, List<AttributeStatement> > getStatementsRecursively(Label label, List<String> identifiers) {
          Map< String, List<AttributeStatement> > results = new HashMap< String, List<AttributeStatement> >();

          List<Statement> unprocessed = new ArrayList<Statement>(label.getStatements());

          while (unprocessed.size() > 0) {
             Statement statement = unprocessed.remove(0);
             // Check that the statement belongs to the parent label.
             if (statement.getSourceURI().toString().equals(label.getLabelURI().toString())
                 && statement instanceof AttributeStatement
                 && identifiers.contains(statement.getIdentifier().getId())) {
                List<AttributeStatement> statements = (ArrayList<AttributeStatement>) results.get(statement.getIdentifier().getId());
                if (statements == null) {
                    //Create a new entry in the hashmap
                   statements = new ArrayList<AttributeStatement>();
                   results.put(statement.getIdentifier().getId(), statements);
                   statements.add((AttributeStatement) statement);
                }
                else
                    statements.add((AttributeStatement) statement);
             } else if (statement instanceof ObjectStatement) {
                ObjectStatement object = (ObjectStatement) statement;
                unprocessed.addAll(object.getStatements());
             } else if (statement instanceof GroupStatement) {
                GroupStatement group = (GroupStatement) statement;
                unprocessed.addAll(group.getStatements());
             }
         }
         return results;
    }
}
