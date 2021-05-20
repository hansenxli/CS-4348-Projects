/**
 * @author Hansen Li
 *
 * @date Jul 11, 2019
*/

// imports pircbot and regex packages
import org.jibble.pircbot.*;
import java.util.regex.*;

// bot class extending abstract pircbot class
public class ScryfallWeatherBot extends PircBot {

	// default constructor
	public ScryfallWeatherBot() {
		this.setName("NotEvilWeatherBot");
	}
	
	// info for bot's server and the channel
	static final String serverName = "chat.freenode.net";
	static final String channelName = "#HansenTestChannel";

	// default info for location used later if none is entered or not found in input
	static final String defaultLocation = "75075";
	
	// creates pattern for 5 digit zipcode with regex
	static final Pattern zipFinder = Pattern.compile("(\\d{5})");
	
	// Given code below
	// reads user input in chat to look for keywords
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
				
		// converts user input to lower case in order to match keywords
		message = message.toLowerCase();
		
		String[] messageList = message.split(" ");
		
		// does see card in message
		if (message.contains("card")) {

			//does not recognize first word as card
			if (messageList[0].equals("card")) {
				
				String name = messageList[1];
				
				CardInfo cardObjectInfo = Parser.getCard(name);
				
				if (cardObjectInfo == null) {
					sendMessage(channel, "Error: " + name + " information wasn't found.");
				}
				

				String cardName = cardObjectInfo.toNameString();
				String cardCost = cardObjectInfo.toCostString();
				String cardType = cardObjectInfo.toTypeString();
				String cardOracleText = cardObjectInfo.toOracleString();
				String directURL = cardObjectInfo.toURLString();

				// sends message to channel with information
				sendMessage(channel, cardName);	
				sendMessage(channel, cardCost);
				sendMessage(channel, cardType);
				sendMessage(channel, cardOracleText);
				sendMessage(channel, directURL);
			}
		}
		
		// searches for weather API keyword if "card" not found
		else if (message.contains("weather")) {
			
			String location = defaultLocation;

			// creates a matcher to find zipcode in user input
			Matcher scanZip = zipFinder.matcher(message);
				
			// if the match finds a zipcode, it pulls the text into group sets the location to it
			if (scanZip.find()) {
				location = scanZip.group(1);
			} 
			
			// if no zipcode, takes the second string as a city name and uses that
			else if (messageList.length == 2 && messageList[1] != null) {
				location = messageList[1];
			}
				
			// if no zipcode is found, it sets default zipcode to location
			else {
					sendMessage(channel, "Location input not found; attempting to return info for 75075 instead.");	
			}
			
			// creates WeatherInfo class by using the Parser class to call the API
			WeatherInfo weatherObjectInfo = Parser.getWeather(location);
		
			// if no proper information is returned by the API, it sets the default location and calls the API with that zipcode
			if (weatherObjectInfo == null) {
				sendMessage(channel, "Error: " + location + "'s information wasn't found. Will now try for " + defaultLocation + " instead.");
			
				// calls API with the default location
				weatherObjectInfo = Parser.getWeather(defaultLocation);
			
				// if the API call fails again, returns error message
				if (weatherObjectInfo == null) {
					sendMessage(channel, "Error: Weather API isn't functioning properly. Please try again later :c");
				}
			}
			
			String weather = weatherObjectInfo.toString();	
			
			// sends message to channel with information 
			sendMessage(channel, weather);
			
		}
	
	}
	
	// main function
	public static void main(String[] args) throws Exception {
		
		// Given code below
		
		// creates new bot
		ScryfallWeatherBot thisBot = new ScryfallWeatherBot();
		
		// enables debugging output
		thisBot.setVerbose(true);
		
		//connects to IRC server
		thisBot.connect(serverName);
	
		// sends connection message and instructions
		thisBot.joinChannel(channelName);
		thisBot.sendMessage(channelName, "Hello there! This is Totally-Not-Evil Weather Bot being developed by Hansen Li!");
		thisBot.sendMessage(channelName, "For weather, make sure you include 'weather' and the 5 digit zipcode or city!");
		thisBot.sendMessage(channelName, "For MtG card info, make sure you start with 'card' and then the card name!");
		thisBot.sendMessage(channelName, "For city and card names, use '+'s to replace spaces.");
		
		
	}
}
