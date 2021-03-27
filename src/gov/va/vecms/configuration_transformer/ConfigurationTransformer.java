package gov.va.vecms.configuration_transformer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.commons.lang3.StringUtils;



import lombok.SneakyThrows;


public class ConfigurationTransformer {
	
	private Map<String, Map<String, Map<String, String>>> map;
	
	public boolean validInput(Map<String, String> customerInput) {
		for (String key : map.keySet()) {
			Map<String, String> transformInput = map.get(key).get("input");
			boolean inputMatchesTransform = true;
			for (String customerKey : customerInput.keySet()) {
				if (!transformInput.containsKey(customerKey)) {
					inputMatchesTransform = false;
				} else {
					if (customerInput.get(customerKey).equals(transformInput.get(customerKey))) {
						inputMatchesTransform = false;
					}
				}
			}
			if (inputMatchesTransform) {
				return true;
			}
		}
		return false;
	}

	public Map<String, String> transformConfiguration(Map<String, String> customerInput) {
		for (String key : map.keySet()) {
			Map<String, String> transformInput = map.get(key).get("input");
			boolean inputMatchesTransform = true;
			for (String customerKey : customerInput.keySet()) {
				if (!transformInput.containsKey(customerKey)) {
					inputMatchesTransform = false;
				} else {
					if (customerInput.get(customerKey).equals(transformInput.get(customerKey))) {
						inputMatchesTransform = false;
					}
				}
			}
			if (inputMatchesTransform) {
				return map.get(key).get("output");
			}
		}
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<String, String> convertJsonToMap(JsonArray json) {
		Map<String, String> input = new HashMap<String, String>();
		json.forEach(new Consumer() {
			@Override
			public void accept(Object t) {
				JsonObject jsonInputKeyValuePair = (JsonObject)t;
				for (String key : jsonInputKeyValuePair.keySet()) {
					input.put(key, jsonInputKeyValuePair.getString(key));
				} 
			}
		});
		return input;
	}
	
	@SneakyThrows
	private JsonObject readFile(String resourceName) throws FileNotFoundException {
		
        InputStream fis = new FileInputStream("person.json");
        
        JsonReader reader = Json.createReader(fis);
     
        JsonObject jsonObject = reader.readObject();
     
        reader.close();
        
        return jsonObject;
	}
	
	public void addTransformer(JsonObject json, String name) {
		Map<String, Map<String, String>> transformer = new HashMap<String, Map<String, String>>();
		transformer.put("input", convertJsonToMap(json.getJsonArray("input")));
		transformer.put("output", convertJsonToMap(json.getJsonArray("output")));
		map.put(name, transformer);
	}
	
	public void initializeFromDir(String directory) throws FileNotFoundException {
		
		// Cameron code from MPI-E
//	    Resource[] resources =
//	            ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
//	                .getResources("classpath*:" + env + "/*.json");
//	        for (Resource resource : resources) {
//	          String fileName = resource.getFilename();
//	          if (fileName.endsWith(".json")) {
//	            final String clientName = StringUtils.removeEnd(fileName, ".json");
//	            final String path = "/" + env + "/" + fileName;

		// First cut, use above instead.
		File dir = new File(directory);
		  File[] directoryListing = dir.listFiles();
		  if (directoryListing != null) {
		    for (File file : directoryListing) {
		    	if (file.getName().toLowerCase().endsWith(".json")) {
		    		addTransformer(readFile(file.getName()), StringUtils.removeEnd(file.getName(), ".json"));
		    	}
		    }
		  } else {
			  StringBuilder error = new StringBuilder();
			  error.append("Can only initialize given a directory of json files.");
			  error.append("Value ["+directory+"] is not a directory.");
			  throw new FileNotFoundException(error.toString());
		  }
	}

}
