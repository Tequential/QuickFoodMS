package defaultpckg;

import java.io.Serializable;

/** Represents a Customer
 * @author Chevaun Martin
 */
public class Customer implements Serializable {
	int orderNum;
	String customerName;
	String customerContact;
	String customerAddress;
	String customerLocation;
	String customerEmail;
	
	/** Constructor
	 * @param orderNum the order number
	 * @param customerName the cutomer's name
	 * @param customerContact the customer's contact details
	 * @param customerAddress the customer's address
	 * @param customerLocation the customer's city
	 * @param customerEmail the customer's email
	 */
	public Customer(int orderNum, String customerName, String customerContact, String customerAddress, String customerLocation, String customerEmail) {
	   this.orderNum = orderNum;
	   this.customerName = customerName;
	   this.customerContact = customerContact;
	   this.customerAddress = customerAddress;
	   this.customerLocation = customerLocation;
	   this.customerEmail = customerEmail;
	    
	}
	
	/** Method to print customer details
	 * @return the formatted output for the customer
	 */
	public String customerDetails() { 
		  String output = "";
		  output += "\nCustomer: " + customerName;
		  output += "\nEmail: " + customerEmail;
		  output += "\nPhone Number: " + customerContact;
		  output += "\nLocation: " + customerLocation;

		  return output;
		  }
	
	/** Method to print customer address
	 * @return the formatted output for the cusomter's address
	 */
	public String customerAddress() { 
		  String output = "\n";
		  output += customerAddress + "\n" + customerLocation + "\n";

		  return output;
		  }
	
}


