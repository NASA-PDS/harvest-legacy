package gov.nasa.pds.harvest.search.policy;

import gov.nasa.pds.harvest.search.util.XMLValidationEventHandler;

import java.io.File;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Class to read the Harvest Policy file.
 * 
 * @author mcayanan
 *
 */
public class PolicyReader {
    public final static String POLICY_PACKAGE = "gov.nasa.pds.harvest.search.policy";
    public final static String POLICY_SCHEMA = "harvest-policy.xsd";

    public static Policy unmarshall(URL policyXML)
    throws SAXParseException, JAXBException, SAXException {
      return unmarshall(new StreamSource(policyXML.toString()));
    }

    public static Policy unmarshall(File policyXML)
    throws SAXParseException, JAXBException, SAXException {
        return unmarshall(new StreamSource(policyXML));
    }

    public static Policy unmarshall(StreamSource policyXML)
    throws JAXBException, SAXException, SAXParseException {
        JAXBContext jc = JAXBContext.newInstance(POLICY_PACKAGE);
        Unmarshaller um = jc.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(
                javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = null;
        try {
            schema = sf.newSchema(
                    PolicyReader.class.getResource(POLICY_SCHEMA));
        } catch (SAXException se) {
            throw new SAXException("Problems parsing harvest policy schema: "
                    + se.getMessage());
        }
        um.setListener(new UnmarshallerListener());
        um.setSchema(schema);
        um.setEventHandler(new XMLValidationEventHandler(
            policyXML.getSystemId()));
        JAXBElement<Policy> policy = um.unmarshal(policyXML, Policy.class);
        return policy.getValue();
    }
}
