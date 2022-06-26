package defaultpckg;
/** Represents a driver
 * @author Chevaun Martin
 */
public class Driver {
	
	String driverName = "";

	/** Constructor
	 * @param driverName set the driver name
	 */
	//
	public Driver(String driverName) {
	   this.driverName = driverName;

	}
	/** Method to print closest driver details
	 * @return the formatted output
	 */
	public String driverDetails() { 
		String output = "\n\n";
		output += driverName + " is the nearest to the restaurant so they will be delivering your order to you at:\n" ;

		return output;
	}
	/** Method to print driver error message
	 * @return the formatted output
	 */
	public String noDriver() { 
		String output = "Sorry, no drivers";
		
		return output;
	}
}
