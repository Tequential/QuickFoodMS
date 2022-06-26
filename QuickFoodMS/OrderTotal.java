package defaultpckg;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;

/** Represents the order total
 * @author Chevaun Martin
 */
public class OrderTotal{
	
	DecimalFormat df = new DecimalFormat("#.00");
	float totalValue;
	
	/** Constructor for total value
	 * @param totalValue
	 */
	public OrderTotal(float totalValue) {
		this.totalValue = totalValue;
	}

	/** Method to print total value
	 * @return the formatted output for the order total
	 */
	public String orderTotal() { 

			String output = "";
		  output = "\nTotal: " + "R" + df.format(totalValue);
		  return output;
	}
	
}
