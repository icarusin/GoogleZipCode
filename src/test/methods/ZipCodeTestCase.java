package test.methods;

import java.io.IOException;
import java.util.List;

import net.sf.json.JSONObject;
import google.geocode.service.GeoCoder;
import junit.framework.*;

public class ZipCodeTestCase extends TestCase{
	
	public void testAddress() throws IOException
	{
		String address = "4500 POPLAR memphis tn";
		GeoCoder gc = new GeoCoder();
		JSONObject json = gc.getGoogleObject(address);
		System.out.println(json.toString());
		List<String> zipCodes = GeoCoder.getZipCodes(json);
		
		System.out.println();
		for(int i=0;i<zipCodes.size();i++)
			System.out.print(" " + zipCodes.get(i));
	}
	
	

}
