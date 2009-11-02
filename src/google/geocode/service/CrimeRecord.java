package google.geocode.service;

import java.util.ArrayList;
import java.util.List;
public class CrimeRecord {
	
	private String date;
	private String crimeType;
	private String address;
	private List<String> zipCodes;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCrimeType() {
		return crimeType;
	}
	public void setCrimeType(String crimeType) {
		this.crimeType = crimeType;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public List<String> getZipCodes() {
		return zipCodes;
	}
	public void setZipCodes(List<String> zipCodes) {
		this.zipCodes = new ArrayList<String>();
		this.zipCodes = zipCodes;
	}

}
