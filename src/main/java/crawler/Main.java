package crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class Main {

	public static void main(String[] args) throws InterruptedException,
			ExecutionException, IOException {

		CSVReader reader = new CSVReader(new FileReader("postal_data.csv"),',');

		String row[];
		while ((row = reader.readNext()) != null) {

			String postalCode=row[0];
			String Long=row[5];
			String Lat=row[6];
			String cityName="";
			String cityUrl="";
			String stateName="";
			Document doc;
			try {

				// need http protocol
				doc = Jsoup
					.connect("http://www.searchtempest.com/search?location="+postalCode+"&maxDist=25"
					+ "&region_us=1&search_string=&keytype=adv&Region=na&cityselect"
					+ "=zip&page=0&category=8&subcat=sss&minAsk=min&maxAsk=max&minYear"
					+ "=min&maxYear=max").get();

				Elements data = doc.getElementsByTag("script");

				try{
				
				cityName=getCityName(data.toString());
				cityUrl=getURL(data.toString());
				stateName=getStateName(data.toString());
				}catch(Exception e)
				{
					writeNotAvailable(postalCode,Long,Lat);
					System.err.println("DATA NOT AVAILABLE");
					System.out.println(e.getMessage());
				}
				
				
			System.err.println(postalCode+ " "+ Long +" "+ Lat +" "+cityName +" "+cityUrl+" "+stateName );
			if((cityName!=null||cityName!="") && (cityUrl!=null||cityUrl!="") && (stateName!=null ||stateName!="") )		
			{
				writeInFilledFile(postalCode,Long,Lat,cityName,cityUrl,stateName);
			}else{
				System.out.println("<<<<<<<<<<< DATA IS MISSING >>>>>>>>>>>>>>>");
			}
			} catch (IOException e) {
				
				System.out.println("Error at :" + postalCode);
				
				writeInMissingFile(postalCode,Long,Lat);
				
				
				Thread.sleep(5000);
				e.printStackTrace();
			}
		}

		
	}

	private static String getStateName(String input) {
		String result = "";
		int start = input.lastIndexOf("statenames = [\"");

		result = (String) input.subSequence(start, start + 40);
		System.out.println(result);
		result = trimName(result);		
		return result;
	}

	private static void writeNotAvailable(String postalCode, String l,
			String lat) throws IOException {
		CSVWriter csvWriter=new CSVWriter(new FileWriter("invalid.csv",true));
    	csvWriter.writeNext(new String[]{postalCode,l,lat});
    	csvWriter.close();
		
	}

	private static void writeInFilledFile(String postalCode, String l,
			String lat, String cityName, String cityUrl ,String stateName) throws IOException {
		
		CSVWriter csvWriter=new CSVWriter(new FileWriter("search_data.csv",true));
    	csvWriter.writeNext(new String[]{postalCode,l,lat,cityName,cityUrl,stateName});
    	csvWriter.close();
		
		
	}

	private static void writeInMissingFile(String postalCode, String l,
			String lat) throws IOException {
		CSVWriter csvWriter=new CSVWriter(new FileWriter("bad.csv",true));
    	csvWriter.writeNext(new String[]{postalCode,l,lat});
    	csvWriter.close();
			
	}

	private static String getCityName(String input) {
		
		String result = "";
		int start = input.lastIndexOf("citynames = [\"");

		result = (String) input.subSequence(start, start + 40);
		System.out.println(result);
		result = trimName(result);		
		return result;
	}

	private static String trimName(String result) {
		result = result.replace("\"", "");
		result=result.replace("\\","");
		result=result.replace("/","");
		int count = 0;
		String name = "";
		char[] src = result.toCharArray();
		char[] dst = new char[100];

		int start = 1 + result.indexOf("[");
		int end=0;
		
		for (int i = 1 + result.indexOf("["); i < src.length; i++) {

			if (src[i] == ']' || src[i] == ',') {
				
				end=i;
				break;
			} else {
				dst[count++] = src[i];
			}

		}

		name=result.substring(start, end);
		return name;
	}

	private static String getURL(String input) {

		String result = "";
		int start = input.lastIndexOf("refs = [\"");

		result = (String) input.subSequence(start, start + 50);
		System.out.println(result);
		result = trimUrl(result);
		return result;
	}

	private static String trimUrl(String result) {

		result = result.replace("\"", "");
		result=result.replace("\\","");
		result=result.replace("/","");
		int count = 0;
		String name = "";
		char[] src = result.toCharArray();
		char[] dst = new char[100];

		int start = 1 + result.indexOf("[");
		int end=0;
		
		for (int i = 1 + result.indexOf("["); i < src.length; i++) {

			if (src[i] == ']' || src[i] == ',') {
				
				end=i;
				break;
			} else {
				dst[count++] = src[i];
			}

		}

		name=result.substring(start, end);
		return "http://"+name + ".craigslist" + ".org";

	}

}
