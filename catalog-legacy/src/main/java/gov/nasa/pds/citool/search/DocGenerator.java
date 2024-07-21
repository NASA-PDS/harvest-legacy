package gov.nasa.pds.citool.search;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import gov.nasa.pds.citool.registry.model.RegistryObject;
import gov.nasa.pds.citool.registry.model.Slots;
import gov.nasa.pds.citool.util.RegistryObjectCache;
import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.ToolsLogRecord;
import gov.nasa.pds.search.core.schema.DataType;
import gov.nasa.pds.search.core.schema.Field;
import gov.nasa.pds.search.core.schema.OutputString;
import gov.nasa.pds.search.core.schema.OutputStringFormat;
import gov.nasa.pds.search.core.schema.Product;
import gov.nasa.pds.search.core.util.PDSDateConvert;


public class DocGenerator 
{
	private static DocGenerator instance;
	
	private Logger log;
	private String outDir;
	private DocWriter writer;
	
	private DocGenerator(String outDir) throws Exception
	{
		log = Logger.getLogger(this.getClass().getName());
		this.outDir = outDir;
	}
	
	public static void init(String outDir) throws Exception
	{
		instance = new DocGenerator(outDir);
	}
	
	public static DocGenerator getInstance()
	{
		return instance;
	}
	
	public void close() throws Exception
	{
		writer.close();
	}
	
	
	public void addVolume(String volumeId) throws Exception
	{
		if(writer != null)
		{
			try 
			{ 
				writer.close(); 
			} 
			catch(Exception ex) 
			{
				// Ignore
			}
		}
		
		writer = new DocWriter(outDir, volumeId);
	}
	
	
	public void addDoc(RegistryObject ro) throws Exception
	{
		String objType = ro.getObjectType(); 
		
		Product conf = DocConfigManager.getInstance().getConfigByObjectType(objType);
		if(conf == null)
		{
			log.warning("No doc config for " + objType);
			return;
		}
		
		Map<String, List<String>> docFields = getDocFields(conf, ro.getSlots());
		writer.write(docFields);
	}

	
	private Map<String, List<String>> getDocFields(Product conf, Slots slots) throws Exception
	{
		Map<String, List<String>> docFields = new TreeMap<String, List<String>>(); 
		
		for(Field field: conf.getIndexFields().getField())
		{
			List<String> values = getFieldValues(field, slots);
			if(!values.isEmpty())
			{
				docFields.put(field.getName(), values);
			}
			else
			{
				if(field.getType() == DataType.REQUIRED)
				{
					log.warning("No value for required field " + field.getName());
				}
			}
		}

		return docFields;
	}
	
	
	private String normalizeDate(Field field, String value)
	{
		String fieldName = field.getName();
		
		try
		{
			value = PDSDateConvert.convert(fieldName, value);
		} 
		catch(Exception ex) 
		{
	        log.log(new ToolsLogRecord(ToolsLevel.WARNING, ex.getMessage() + " - " + fieldName));
			value = PDSDateConvert.getDefaultTime(fieldName);
		}
		
		return value;
	}


	private String convertValue(Field field, String value)
	{
		if(field.getType() == DataType.DATE)
		{
			return normalizeDate(field, value);
		}
		
		return value;
	}
	
	
	private List<String> handleComplexPath(String regPath, Slots slots)
	{
		String pathArray[] = regPath.split("\\.");
		if(pathArray == null || pathArray.length < 2)
		{
			log.warning("Invalid registry path: " + regPath);
			return null;
		}
		
		List<String> lids = slots.get(pathArray[0]);		
		if(lids == null || lids.isEmpty())
		{
			log.warning("Could not find slot " + pathArray[0]);
			return null;
		}

		List<String> values = new ArrayList<String>();
		
		for(String lid: lids)
		{
			RegistryObject ro = RegistryObjectCache.getInstance().get(lid);
			if(ro != null)
			{
				List<String> tmpVals = ro.getSlots().get(pathArray[1]);
				if(tmpVals != null)
				{
					values.addAll(tmpVals);
				}
			}
			else
			{
				log.warning("Could not find lid: " + lid);
			}
		}
		
		return values;
	}
	
	
	private List<String> getFieldValues(Field field, Slots slots) throws Exception
	{
		List<String> docValues = new ArrayList<String>(); 
		
		// Registry path
		for(String regPath: field.getRegistryPath())
		{			
			List<String> values = null;
		
			if(regPath.contains("."))
			{
				values = handleComplexPath(regPath, slots);
			}
			else
			{
				values = slots.get(regPath);
			}
				
			if(values != null)
			{
				for(String value: values)
				{
					docValues.add(convertValue(field, value));
				}
			}
		}
		
		// Output string
		if(docValues.isEmpty() && field.getOutputString() != null) 
		{
			String value = handleTemplate(field.getOutputString(), slots);
			if(value != null)
			{
				docValues.add(value);
			}
		}
	    
		// Default value
		if(docValues.isEmpty() && field.getDefault() != null) 
		{
			docValues.add(field.getDefault());
		}

		return docValues;
	}
	
	
	private String handleTemplate(OutputString outString, Slots slots) throws Exception
	{
		String str = outString.getValue();
		
	    while(str.contains("{")) 
	    {
			int start = str.indexOf("{");
			int end = str.indexOf("}", start);
			
			String key = str.substring(start + 1, end);
			String value = null;
			
			if(key.contains("."))
			{
				List<String> vals = handleComplexPath(key, slots);
				value = (vals != null && vals.size() > 0) ? vals.get(0) : null;
			}
			else
			{
				value = slots.getFirst(key);
			}
			
			if(value != null)
			{
				if(outString.getFormat().equals(OutputStringFormat.URL)) 
				{
					value = URLEncoder.encode(value, "UTF-8");
				} 

				str = str.replace("{" + key + "}", value);	          
			} 
			else 
			{
				log.warning("No value for key " + key);
				str = str.replace("{" + key + "}", "");
			}
	    }
	    
	    return str;
	}
	
}
