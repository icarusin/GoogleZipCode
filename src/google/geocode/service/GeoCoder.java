package google.geocode.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;

public class GeoCoder {
	
	private final static String ENCODING = "UTF-8";
	private final static String KEY = "ABQIAAAAm2adf4ycplePyzGtvEis3xR4Xva3xSOqF_LWWgxV1bSIDXeFuxQLv9WSC55xSyQ9Z10UD9UMbfu6Nw";

	public JSONObject getGoogleObject(String address) throws IOException
	{
		
		String theURL = "http://maps.google.com/maps/geo?q="
			+ URLEncoder.encode(address, ENCODING)
			+ "&sensor=false&output=json&key=" + KEY;
		URLConnection conn = new URL(
				"http://maps.google.com/maps/geo?q="
				+ URLEncoder.encode(address, ENCODING)
				+ "&sensor=false&output=json&key=" + KEY).openConnection();
	 ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
	 IOUtils.copy(conn.getInputStream(), output);
	 
	    output.close();
	    JSONObject json = new JSONObject();
	    json = JSONObject.fromObject(output.toString());
	    
	    return json;
	}
	
	public static List<String> getZipCodes(JSONObject json) throws IOException
	{
		List<String> addresses = new ArrayList<String>();
		 
		    // that means the reply was OK 
		    if(json.getJSONObject("Status").getInt("code")==200)
		    {
		    JSONArray addrList = (JSONArray)json.get("Placemark");
		    for(int i = 0;i<addrList.size();i++)
		    {
		    	
		    	JSONObject curAddr = (JSONObject)addrList.get(i);
		    	//System.out.println(curAddr.containsKey("AddressDetails"));
		    	if(curAddr.getJSONObject("AddressDetails").getInt("Accuracy")>=5)
		    	{
		    		
		    	String fullAddress = curAddr.getString("address");
		    		//System.out.println(fullAddress);
		    		String[] elementsOfAddress = fullAddress.split(",");
		    		String zipCodePart = elementsOfAddress[elementsOfAddress.length-2];
		    		Pattern p = Pattern.compile("[0-9]+");
		    		Matcher m = p.matcher(zipCodePart);
		    		if(m.find())
		    		{
		    		String zipCode = zipCodePart.substring(zipCodePart.length()-5);
		    		//System.out.println("Zipcode = "+ zipCode);
//		    		addresses.add(elementsOfAddress[elementsOfAddress.length-2].substring(beginIndex))
		    	    addresses.add(zipCode);
		    		}
		    	}
		    	
		    }
		    }
		    else
		    	addresses.add("Error in address");
		return addresses;
	}
	
	public Coordinate getCoordinate(JSONObject json)
	{
		Coordinate coordinates = new Coordinate();
		// code to parse json and get the coordinates
		return coordinates;
	}

}
