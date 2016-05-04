package crawler;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class StateNames {

	public static void main(String[] args) throws InterruptedException {
		Document doc;
		try {

			// need http protocol
			doc = Jsoup
				.connect("http://www.searchtempest.com/search?location="+10001+"&maxDist=25"
				+ "&region_us=1&search_string=&keytype=adv&Region=na&cityselect"
				+ "=zip&page=0&category=8&subcat=sss&minAsk=min&maxAsk=max&minYear"
				+ "=min&maxYear=max").get();

			Elements data = doc.getElementsByTag("script");
			
			System.out.println(data);
			
			try{
			
		System.out.println(getCityName(data.toString()));
		System.out.println(getURL(data.toString()));
		
		
			}catch(Exception e)
			{
				
				System.err.println("DATA NOT AVAILABLE");
				System.out.println(e.getMessage());
			}

	}catch (IOException e) {
				
		Thread.sleep(1000);
		e.printStackTrace();
	}
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
