import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.*;

/**
 * Code to create a mashup using 3 APIs that helps to find a 
 * doctor of your need in your nearby search location. It also
 * provides you with a fare estimate to visit the doctor.
 * 
 * @author Priyanka Samanta
 * @date    20-Feb-2016
 */
public class ProgrammingAssignment1 {

	//class variables
	static HttpURLConnection connection;
	static double start_latitude, start_longitude,end_latitude,end_longitude;
	static URL url;
	static List<Double> location_list;

	/**
	 * @param connection HttpURLConnection
	 * 
	 * method to establish the connection and
	 * return the output
	 */
	public static String hitConnection(HttpURLConnection connection){
		String str="";
		try{
			connection.setRequestMethod("GET");
			connection.connect();
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader
					(connection.getInputStream()));

			StringBuffer stringBuffer = new StringBuffer();
			while ((str = bufferReader.readLine()) != null) {
				stringBuffer.append(str);
				stringBuffer.append("\n");
			}
			str = stringBuffer.toString();
		}
		catch(Exception e){
			System.out.println("Invalid request");
		}
		return str;
	}

	/**
	 *
	 * @param seconds int
	 * @return String
	 * 
	 * method to convert seconds to hh mm and ss
	 */
	public static String secondsToMinConvertor(int seconds){
		int hour=0,min=0,sec=0;
		sec = seconds%60;
		min = seconds/60;
		if(min>=60){
			hour=min/60;
			min=min%60;
		}
		return (hour+"hr "+ min+"mm " + sec+ "ss");
	}

	/**
	 * @param zipcode String
	 * 
	 * method to invoke the Map Api that provides
	 * the geo location of the entered zipcode
	 */
	public static void callMapApi(String zipcode){
		//Google Map API to get the user location given the zipcode
		try {
			url = new URL 
					("http://maps.googleapis.com/maps/api/geocode/"
							+ "json?address="+zipcode);
			connection =(HttpURLConnection) url.openConnection ();
			String str=hitConnection(connection);

			//Parsing JSON Object of location
			JSONObject jsonObject = new JSONObject(str);
			start_latitude=(double) Float.parseFloat(jsonObject.getJSONArray("results").
					getJSONObject(0).getJSONObject("geometry").
					getJSONObject("location").get("lat").toString());
			start_longitude=(double)Float.parseFloat(jsonObject.getJSONArray("results").
					getJSONObject(0).getJSONObject("geometry").getJSONObject("location").
					get("lng").toString());
		}
		catch(Exception e){
			System.out.println("Invalid request");
		}

	}

	/**
	 * @param speciality String
	 * @param miles int
	 * 
	 * method to invoke the BetterDoctor that provides
	 * the 10 doctors as per as the search
	 */
	public static void callDoctorApi(String speciality, int miles){
		//BetterDoctor API to get the list of doctors satisfying the user requirement
		// of speciality and search radius in miles
		try{
			url = new URL("https://api.betterdoctor.com/2015-09-22/doctors?query="
					+speciality+"&location="+start_latitude+"%2C"+
					start_longitude+"%2C"+miles+"&user_location="+start_latitude+
					"%2C"+start_longitude+"&fields=practices"
					+ "(distance%2Clat%2Clon%2Cname%2Cvisit_address%2Coffice_hours"
					+ "%2Cphones)&skip=0&limit=10&user_key=224f143bec1df1f6fff2742"
					+ "6cf1d068b");
			connection =(HttpURLConnection) url.openConnection ();
			String str=hitConnection(connection);

			//Parsing JSON Object of BetterDoctor API

			//Storing the geo locations of the returned doctors
			location_list = new ArrayList<>();
			JSONObject jsonObject = new JSONObject(str);
			int count = jsonObject.getJSONObject("meta").getInt("count");

			//If search returned no result
			if(count==0){
				System.out.println("Sorry no doctors found for your search!!");
				System.exit(0);
			}
			JSONArray data = jsonObject.getJSONArray("data");
			for(int i=0;i<data.length();i++){
				String name=data.getJSONObject(i).getJSONArray("practices").
						getJSONObject(0).getString("name");

				Double lat=data.getJSONObject(i).getJSONArray("practices").
						getJSONObject(0).getDouble("lat");

				Double lon=data.getJSONObject(i).getJSONArray("practices").
						getJSONObject(0).getDouble("lon");

				String address=data.getJSONObject(i).getJSONArray("practices").
						getJSONObject(0).getJSONObject("visit_address").
						getString("street")+", "+data.getJSONObject(i).
						getJSONArray("practices").getJSONObject(0).
						getJSONObject("visit_address").getString("city")+", " +
						data.getJSONObject(i).getJSONArray("practices").
						getJSONObject(0).getJSONObject("visit_address").
						getString("state")+", " +data.getJSONObject(i).
						getJSONArray("practices").getJSONObject(0).
						getJSONObject("visit_address").getString("zip");

				double distance=data.getJSONObject(i).getJSONArray("practices").
						getJSONObject(0).getDouble("distance");

				String phone_number=data.getJSONObject(i).getJSONArray("practices").
						getJSONObject(0).getJSONArray("phones").getJSONObject(0).
						getString("number");

				//Printing the data returned
				System.out.println((i+1) +". " + name+"\n" + "   Address: " + address+"\n"
						+ "   Phone Number: "+ phone_number +"\n" + "   Distance: "
						+ "" +Math.round(distance*100.0)/100.0+" miles"+"\n" );
				//Storing the locations
				location_list.add(lat);
				location_list.add(lon);
			}
		}
		catch(Exception e){
			System.out.println("Invalid request!");
		}

	}

	/**
	 * 
	 * method to invoke the Uber API that provides
	 * fare estimate for visiting the selected doctor
	 * using Uber cab 
	 */
	public static void callUberApi(){
		//Calling the Uber API to get the fare estimate
		try{
			url = new URL 
					("https://api.uber.com/v1/estimates/price?start_latitude="
							+ ""+start_latitude+"&start_longitude="
							+ ""+start_longitude+"&end_latitude="+end_latitude+
							"&end_longitude="+end_longitude);
			connection =(HttpURLConnection) url.openConnection ();
			connection.setRequestProperty
			("Authorization","Token dNErg6E0NPr18o-ubWIK7KD-C93KNX0tUeVPvN9E");
			String str=hitConnection(connection);

			//Parsing the data returned by Uber API
			JSONObject jsonObject = new JSONObject(str);
			JSONArray uberPriceEstimate = jsonObject.getJSONArray("prices");

			//Print the uber options of car along with the fare estimate
			System.out.println("Your fare estimate:" +"\n"+ 
					"-----------------------");
			int i=0;
			for (i=0;i<uberPriceEstimate.length();i++){

				//Handling the case when Uber is not operational
				//in the given search area
				if(uberPriceEstimate.getJSONObject(i).
						get("localized_display_name").toString().equals("update")){
					System.out.println("Sorrry!!"
							+ "Uber is not available in your search location.");
					System.exit(0);
				}
				System.out.println();
				System.out.print(uberPriceEstimate.getJSONObject(i).
						get("localized_display_name")+":");
				int duration_in_seconds=uberPriceEstimate.getJSONObject(i).
						getInt("duration");

				//Converting the seconds to hh mm ss form
				String duration=secondsToMinConvertor(duration_in_seconds);
				System.out.print(" Away from you by "+ duration);
				System.out.print("\n" +"Estimated price:"+ 
						uberPriceEstimate.getJSONObject(i).get("estimate")+"\n");
			}
		}
		catch(Exception e){
			System.out.println("Invalid request!");
		}
	}


	/**
	 * @param args String[]
	 * @throws Exception 
	 * 
	 * main driver method
	 */
	public static void main(String[] args){
		
		System.out.println("-----------------"+"\n" 
				+ "RUSH TO DOCTOR" + "\n" +"------------------");
		Scanner scan = new Scanner(System.in);

		//Taking the user input of zipcode
		System.out.println("Enter your zipcode:");
		String zipcode=scan.nextLine();

		//Calling the Map API
		callMapApi(zipcode);

		//Getting the specality of doctor and miles of search
		//: user input
		System.out.println("Enter the speciality of doctor you are looking "
				+ "for(eye,cardiologist,dentist, etc) :");
		String speciality = scan.nextLine();
		speciality=speciality.replace(" ","%20");
		System.out.println("Enter the radius for your search in miles:");
		int miles = scan.nextInt();

		//Callingg the BetterDoctor API
		callDoctorApi(speciality,miles);

		//Getting the choice of doctor: user input
		System.out.println("Enter the doctor number you want to visit: ");
		int choice = scan.nextInt();
		//Handling the invalid choice option
		if( (choice >10) || (choice <1)){
			System.out.println("Invalid option. Please try again!");
			System.exit(0);
		}
		scan.close();

		//Fetching th geo-locations for th doctors chamber
		//based on user's choice
		end_latitude=location_list.get((choice-1)*2);
		end_longitude=location_list.get((choice-1)*2+1);

		//Calling the Uber API to get the fare estimate, 
		//given the source and destination's geo location
		callUberApi();
	} 

}