package gov.nasa.pds.citool.ri;

import gov.nasa.pds.tools.label.Label;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class RIFileSorter {
    private List<Label> parents;
    private List<Label> children;
    private List<Label> catalogs;

    /**
     * Class to sort the given list of catalog files into
     * parents and children.
     *
     * @param catalogs A list of catalog files.
     */
    public RIFileSorter(List<Label> catalogs) {
        this.catalogs = catalogs;
    }

    /**
     * Sort the catalog files.
     *
     * @param type An RIType. This determines how
     * the files get sorted.
     */
    public void sort(RIType type) {
        parents = new ArrayList<Label>();
        children = new ArrayList<Label>();
        for(Label catalog : catalogs) {
            if(!catalog.getObjects(type.getName().toUpperCase()).isEmpty()) {
                parents.add(catalog);
            }
            //Add a DATA_SET_COLLECTION.CAT file to both parent and child
            else if(RIType.DATA_SET.equals(type) &&
               !(catalog.getObjects(
                       RIType.DATA_SET_COLLECTION.getName().toUpperCase())
                       .isEmpty())) {
                parents.add(catalog);
                children.add(catalog);
            }
            else {
                children.add(catalog);
            }
        }
    }

    public List<Label> getParents() { return parents; }

    public List<Label> getChildren() { return children; }

    public List<String> getParentFiles() {
        List<String> files = new ArrayList<String>();
        for (Label parent : parents) {
            files.add(FilenameUtils.getName(parent.getSourceNameString()));
        }
        return files;
    }

    public List<String> getChildrenFiles() {
        List<String> files = new ArrayList<String>();
        for (Label child : children) {
            files.add(FilenameUtils.getName(child.getSourceNameString()));
        }
        return files;
    }
}
