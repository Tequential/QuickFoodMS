package defaultpckg;

/** Represents a restaurant
 * @author taqua
 */
public class Restaurant {
	String restaurantName = "";
	String restaurantLocation = "";
	String restaurantContact = "";
	String specInstructions = "";
	
	/** Constructor
	 * @param restaurantName set the restaurant name
	 * @param restaurantLocation set the restaurant city
	 * @param restaurantContact set the restaurant contact details
	 * @param specInstructions set the special instructions
	 */
	public Restaurant(String restaurantName, String restaurantLocation, String restaurantContact, String specInstructions) {
	   this.restaurantName = restaurantName;
	   this.restaurantLocation = restaurantLocation;
	   this.restaurantContact = restaurantContact;
	   this.specInstructions = specInstructions;
   
	}
	
	/** Method to print restaurant details
	 * @return the formatted output for the resturant name and location
	 */
	public String restaurantDetails() { 
		  String output = "";
		  output += "\n\nYou have ordered the following from " + restaurantName + " in " + restaurantLocation + ":";

		  return output;
	}
	
	/** Method to print special instructions
	 * @return the formatted output for the special instructions
	 */
	public String specialInstructions() { 
		  String output = "\n\nSpecial Instructions: ";
		  output += specInstructions + "\n";
		  
		  return output;
	}
	
	/** Method to print restaurant contact
	 * @return return the formatted output for the restaurant contact details
	 */
	public String restaurantContact() { 
		  String output = "\nIf you need to contact the restaurant, their number is " + restaurantContact + ".";
		  
		  return output;
	}
}
