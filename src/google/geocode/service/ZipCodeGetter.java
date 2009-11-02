package google.geocode.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

public class ZipCodeGetter {
	
	@SuppressWarnings("static-access")
	public static void main(String[] args)
	{
		/**
		 * 1. read input file
		 * 2. copy contents of input file to output
		 * 3. make two calls to Google GeoCode 
		 * 4. Format sort the output 
		 * 5. Log the number of records read
		 * 
		 */
		String filePath = args[0];
		//filePath = "C:\\Users\\sajit\\Documents\\Downloads\\Memphis&Jackson_Crime_data.csv";
		Logger logger = Logger.getLogger("ZipCodeGetter");
		FileHandler handler = null;
		int nextReadRecord = new Integer(args[1]).intValue();
		File inputFile = new File(filePath);
		
		FileWriter fstream = null;
		try {
			fstream = new FileWriter("ArunGuns"+args[1]+".txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        BufferedWriter out = new BufferedWriter(fstream);
		BufferedReader br  = null;
		try{
			
		br = new BufferedReader(new FileReader(inputFile));
		}
		catch(FileNotFoundException ffe)
		{
			ffe.printStackTrace();
		}
		
		int recordCount = 1;
		/**
		 * In case we have finished getting zip codes for certain records we dont want to do that again
		 */
		try{
			handler = new FileHandler("log"+args[1]+".log");
		while(recordCount <nextReadRecord)
		{
			
				br.readLine();
						recordCount++;
		}
		String record;
		while((record = br.readLine())!= null)
		{
			
             CrimeRecord crimerecord = new CrimeRecord();
			 String[] curRecord = record.split(",");
			 if(curRecord.length<2)
				 continue;
             crimerecord.setDate(curRecord[0]);
             crimerecord.setCrimeType(curRecord[1]);
             crimerecord.setAddress(curRecord[2]);
             GeoCoder geoCoder = new GeoCoder();
             String lowaddress = replace(crimerecord.getAddress(),"XX","00");
             //System.out.println("Calling zipcode get method with " + lowaddress+" memphis tn");
             JSONObject googleObject = geoCoder.getGoogleObject(lowaddress+" memphis tn");
             List<String> lzipcodes = geoCoder.getZipCodes(googleObject);
             String highaddress = replace(crimerecord.getAddress(),"XX","99");
             //System.out.println("Calling zipcode get method with " + highaddress+" memphis tn");
             JSONObject googleObject2 = geoCoder.getGoogleObject(highaddress+" memphis tn");
             List<String> hzipcodes = geoCoder.getZipCodes(googleObject2);
             if(lzipcodes.size()==1 && hzipcodes.size()==1)
             {
            	 if(lzipcodes.get(0).compareTo(hzipcodes.get(0))==0)
            	 {
            		 crimerecord.setZipCodes(lzipcodes);
            	 }
            	 else
            	 {
            		 List<String> tempList = new ArrayList<String>();
            		 tempList.add(lzipcodes.get(0));
            		 tempList.add(hzipcodes.get(0));
            		 crimerecord.setZipCodes(tempList);
            	 }
             }
             else if(lzipcodes.size() > 1 && hzipcodes.size() == 1)
             {
            	 List<String> sortedZipCodes = new ArrayList<String>();
            	 sortedZipCodes.addAll(arrangeZipCodes(lzipcodes));
            	 sortedZipCodes.add(hzipcodes.get(0));
            	 crimerecord.setZipCodes(sortedZipCodes);
             }
             else if(lzipcodes.size()==1 && hzipcodes.size() > 1)
             {
            	 List<String> sortedZipCodes = new ArrayList<String>();
            	 sortedZipCodes.add(lzipcodes.get(0));
            	 sortedZipCodes.addAll(arrangeZipCodes(hzipcodes));
            	 crimerecord.setZipCodes(sortedZipCodes);
             }
             else
             {
            	 List<String> sortedZipCodes = new ArrayList<String>();
            	 sortedZipCodes.addAll(arrangeZipCodes(lzipcodes));
            	 sortedZipCodes.addAll(arrangeZipCodes(hzipcodes));
            	 crimerecord.setZipCodes(sortedZipCodes);
             }
             
             
             //write to file 
             String crimeRecordString = crimerecord.getDate()+","+crimerecord.getCrimeType()+","+crimerecord.getAddress()+",";
             for(int i=0;i<crimerecord.getZipCodes().size();i++)
             {
            	 crimeRecordString = crimeRecordString.concat(crimerecord.getZipCodes().get(i)+",");
             }
             //System.out.println("crime Record string = "+ crimeRecordString);
             out.write(crimeRecordString);
             
             out.write("\n");
             
             
             if(recordCount%100==0)
            	 logger.log(Level.INFO, recordCount + " number of messages logged");
             recordCount++;
             
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				out.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				logger.log(Level.SEVERE, e1.getLocalizedMessage());
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		finally
		{
			try {
				out.close();
				logger.log(Level.INFO, new Integer(recordCount-1).toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	private static String replace(String x,String init,String replaceby)
	{
		return x.replaceFirst(init, replaceby);
	}

	private static List<String> arrangeZipCodes(List<String> zipcodes)
	{
		List<String> finalZipCodeList = new ArrayList<String>();
		
		
		HashMap<String,Integer> zipCodeCount = new HashMap<String,Integer>();
		for(int i=0;i<zipcodes.size();i++)
		{
			if(!zipCodeCount.containsKey(zipcodes.get(i)))
				zipCodeCount.put(zipcodes.get(i), 1);
			else
			{
				int prevCount = zipCodeCount.get(zipcodes.get(i));
				zipCodeCount.put(zipcodes.get(i), prevCount + 1);
			}
		}
		int max = 0;
		for(int i=0;i<zipcodes.size();i++)
		{
			 if(max<zipCodeCount.get(zipcodes.get(i)))
			 {
				 max = zipCodeCount.get(zipcodes.get(i));
				 finalZipCodeList.add(zipcodes.get(i));
			 }
		}
		for(int i=1;i<zipcodes.size();i++)
		{
			if(!finalZipCodeList.contains(zipcodes.get(i)))
			{
				finalZipCodeList.add(zipcodes.get(i));
			}
		}
		
		return finalZipCodeList;
		
		
		
		
	}
}

