package defaultpckg;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;

public class Invoice {
	/**
	* QuickFoodMS Orders
	* <p>
	* This is a program that saves orders for a restaurant to a database.
	* The user can enter customer details (Name, Address, Email), restaurant details (Name, address, details) and 
	* the items, quantities and prices for the customer's order. 
	* When the order is complete, an invoice will be generated for the customer.
	* @author Chevaun Martin
	*/

	public static void main(String[] args) {

		try {

			//Connect to the database, via the jdbc:mysql: channel
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/quickfoodms?useSSL=false",
					"chevaun", "swordfish");
			ResultSet results;

			
			 // create a new file to write to
			Formatter newFile = null;
			try {
				newFile = new Formatter(
						"/Users/taqua/Dropbox/CM21100000013/3. DB Skills Program/Task13/Task2/invoice.txt");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			//Call the main menu

			int menuItem = promptUserInt(
					"Welcome to QuickFoodMS!\n1.Place a New Order\n2.Show Incomplete Orders\n3.Search for an Order\n");
			switch (menuItem) {
			case 1:

				//Request customer location and find a driver in the same location
				String customerLocation = promptUserStr("Enter Customer City:");
				results = findCustomerDriver(connection, customerLocation);

				// check if the customer location matches a driver location. Terminate the
				// program and print an apology if a driver is not available.
				if (!results.isBeforeFirst()) {
					System.out.println(
							"Sorry! Our drivers are too far away from you to be able to deliver to your location.\n");
					break;

				}
				// Create restaurant details
				String restaurantLocation = promptUserStr("Enter the restaurant City:");
				String restaurantName = promptUserStr("Enter the restaurant name:");
				String restContact = promptUserStr("Enter the restaurant contact number:");
				createRestaurant(connection, restaurantName, restaurantLocation, restContact);

				// Check if a driver is available
				// If no driver is available, terminate the program
				Driver newDriver = findRestaurantDriver(connection, restaurantLocation);
				if (newDriver == null) {
					break;
				}

				// Request customer details
				int orderNum = promptUserInt("Enter you order number:");
				String customerName = promptUserStr("Enter customer name and surname:");
				String customerEmail = promptUserStr("Enter customer email address:");
				String customerContact = promptUserStr("Enter customer phone number:");
				String customerAddress = promptUserStr("Enter customer address:");
				int currentOrderID = getOrderID(connection);

				// Add customer, restaurant and order if to the database
				setCustomer(connection, customerName, customerContact, customerAddress, customerLocation, customerEmail,
						currentOrderID);

				setRestaurantID(connection, currentOrderID);
				setOrderID(connection, orderNum);

				// Request order details from the user
				getMealItems(connection, currentOrderID);
				String specPrep = promptUserStr(
						"Do you have any special preparation instructions to add? Type the instructions here, or type 'none' if you have none.");
				// set the status to complete
				setStatus(connection, currentOrderID);
				// Create new objects and write to txt file
				Customer cust = new Customer(orderNum, customerName, customerContact, customerAddress, customerLocation,
						customerEmail);
				Restaurant newRest = new Restaurant(restaurantName, restaurantLocation, restContact, specPrep);
				newFile.format("%s", cust.customerDetails());
				newFile.format("%s", newRest.restaurantDetails());
				results = createOrdersResults(connection, currentOrderID);
				newFile.format("%s", orderDetails(results));
				newFile.format("%s", newRest.specialInstructions());
				results = createTotalResults(connection, currentOrderID);
				OrderTotal newTotal = new OrderTotal(results.getFloat(1));
				newFile.format("%s", newTotal.orderTotal());
				newFile.format("%s", newDriver.driverDetails());
				newFile.format("%s", cust.customerAddress());
				newFile.format("%s", newRest.restaurantContact());
				newFile.close();

				break;

			case 2:
				getOrderStatus(connection);
				break;
			case 3:
				searchOrder(connection);
				break;
			}

		} catch (

		SQLException e) {
			e.printStackTrace();

		}
	}

	/**
	 *  add OrderID to the orders table
	 * @param connection connect to the JDBC
	 * @param orderNum the current order number
	 * @throws SQLException
	 */
	public static void setOrderID(Connection connection, int orderNum) throws SQLException {
		PreparedStatement pStatement = connection.prepareStatement("INSERT INTO orders (OrderNumber) VALUES (?);");
		pStatement.setInt(1, orderNum);

		pStatement.executeUpdate();
	}

	/**
	 *  find a driver from the drivers table in the same city as the customer
	 * @param connection connect to the JDBC
	 * @param customerLocation the customer's city
	 * @return the resultset
	 * @throws SQLException
	 */
	public static ResultSet findCustomerDriver(Connection connection, String customerLocation) throws SQLException {
		String findDriver = ("SELECT DriverName FROM drivers WHERE DriverCity = ? ORDER BY DriverLoad LIMIT 1;");
		PreparedStatement pStatement = null;
		try {
			pStatement = connection.prepareStatement(findDriver);
			pStatement.setString(1, customerLocation);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ResultSet results = pStatement.executeQuery();

		return results;
	}

	/**
	 * find a driver from the drivers table in the same city as the restaurant.
	 *  If a driver is found, add the DriverID to the orders table
	 * @param connection connect to the JDBC
	 * @param restaurantLocation the restaurant city
	 * @return a Driver object
	 * @throws SQLException
	 */

	public static Driver findRestaurantDriver(Connection connection, String restaurantLocation) throws SQLException {
		String findRestDriver = ("SELECT DriverName, DriverID FROM drivers WHERE DriverCity = ? ORDER BY DriverLoad LIMIT 1;");
		Driver newDriver = null;
		PreparedStatement pStatement = null;
		try {
			pStatement = connection.prepareStatement(findRestDriver);
			pStatement.setString(1, restaurantLocation);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ResultSet results = pStatement.executeQuery();

		// If the select query does not return anything, then try again
		if (!results.isBeforeFirst()) {
			System.out.println("Sorry, no drivers available for this restaurant\n");

		} else {
			results.next();
			int driverID = results.getInt("DriverID");
			String driverName = results.getString("DriverName");
			newDriver = new Driver(driverName);
			// Insert order details into the orders table

			pStatement = connection.prepareStatement("INSERT INTO orders (DriverID) VALUES (?);");
			pStatement.setInt(1, driverID);

			pStatement.executeUpdate();
		}
		return newDriver;
	}
	/**
	 * add restaurant details to the restaurant table
	 * @param connection connect to the JDBC
	 * @param restaurantName the restaurant's name
	 * @param restaurantLocation the restaurant's city
	 * @param restContact the restaurant's contact details
	 * @throws SQLException
	 */

	public static void createRestaurant(Connection connection, String restaurantName, String restaurantLocation,
			String restContact) throws SQLException {
		PreparedStatement pStatement = null;

		pStatement = connection.prepareStatement(
				"INSERT INTO restaurant " + "(RestaurantName, RestaurantCity, RestaurantNumber) VALUES (?, ?, ?);");
		pStatement.setString(1, restaurantName);
		pStatement.setString(2, restaurantLocation);
		pStatement.setString(3, restContact);

		pStatement.executeUpdate();
	}
	/**
	 * add Customer details into the customer table
	 * @param connection connect to the JDBC
	 * @param customerName the customer's name
	 * @param customerContact the customer's contact details
	 * @param customerAddress the customer's address
	 * @param customerLocation the customer's city
	 * @param customerEmail the customer's email
	 * @param currentOrderID the current order ID from the orders table
	 * @throws SQLException
	 */

	public static void setCustomer(Connection connection, String customerName, String customerContact,
			String customerAddress, String customerLocation, String customerEmail, int currentOrderID)
			throws SQLException {
		PreparedStatement pStatement = connection.prepareStatement(
				"INSERT INTO customer (CustomerName, CustomerNumber, CustomerAddress, CustomerCity, CustomerEmail) "
						+ "VALUES (?, ?, ?, ?, ?);");
		pStatement.setString(1, customerName);
		pStatement.setString(2, customerContact);
		pStatement.setString(3, customerAddress);
		pStatement.setString(4, customerLocation);
		pStatement.setString(5, customerEmail);

		pStatement.executeUpdate();

		// Insert customerId into the orders table
		pStatement = connection.prepareStatement("UPDATE orders SET CustomerID = LAST_INSERT_ID() WHERE OrderID = ?;");
		pStatement.setInt(1, currentOrderID);
		pStatement.executeUpdate();
	}
	/**
	 * method to fetch the current OrderID from the orders table
	 * @param connection connect to the JDBC
	 * @return the current OrderID from the orders table
	 * @throws SQLException
	 */

	public static int getOrderID(Connection connection) throws SQLException {
		String selectQuery = ("SELECT OrderID from Orders ORDER BY OrderID DESC LIMIT 1;");
		PreparedStatement pStatement = null;
		try {
			pStatement = connection.prepareStatement(selectQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ResultSet results = pStatement.executeQuery();
		results.next();
		int currentOrderID = results.getInt("OrderID");
		return currentOrderID;
	}
	/**
	 * add the RestaurantID to the orders table
	 * @param connection connect to the JDBC
	 * @param currentOrderID the current orderID
	 * @throws SQLException
	 */
	public static void setRestaurantID(Connection connection, int currentOrderID) throws SQLException {
		PreparedStatement pStatement = connection
				.prepareStatement("UPDATE orders SET RestaurantID = LAST_INSERT_ID() WHERE OrderID = ?;");
		pStatement.setInt(1, currentOrderID);
		pStatement.executeUpdate();
	}
	/**
	 * request meal items from the user and add them to the orderdetails table
	 * @param connection connect to the JDBC
	 * @param currentOrderID the current orderID
	 * @throws SQLException
	 */
	public static void getMealItems(Connection connection, int currentOrderID) throws SQLException {
		String menuItem = promptUserStr("What menu item would you like to order?");

		int mealQty = promptUserInt("How many?");

		float mealPrice = promptUserFloat("What is the price of this item?");

		float mealTotal = (float) mealQty * mealPrice;

		PreparedStatement pStatement = connection.prepareStatement("INSERT INTO orderdetails "
				+ "(MealDescription, MealQty, MealPrice, MealTotal, OrderID) VALUES (?, ?, ?, ?, ?);");
		pStatement.setString(1, menuItem);
		pStatement.setInt(2, mealQty);
		pStatement.setFloat(3, mealPrice);
		pStatement.setFloat(4, mealTotal);
		pStatement.setInt(5, currentOrderID);

		pStatement.executeUpdate();

		// Request a new menu item
		menuItem = promptUserStr(
				"Add another menu item to your order:\nPress y to continue, or if you have nothing else to add, type in 'done'");
		if (!menuItem.equalsIgnoreCase("done")) {
			getMealItems(connection, currentOrderID);
		}

	}
	/**
	 * add special preparation instructions to the orders table
	 * @param connection connect to the JDBC
	 * @param currentOrderID the current order ID
	 * @param specPrep special preparations for the order
	 * @throws SQLException
	 */
	public static void setSpecialPrep(Connection connection, int currentOrderID, String specPrep) throws SQLException {

		// Insert order item details into the orderdetails table
		PreparedStatement pStatement = connection
				.prepareStatement("UPDATE orders SET SpecialPrep = ? WHERE OrderID = ?;");
		pStatement.setString(1, specPrep);
		pStatement.setInt(2, currentOrderID);

		pStatement.executeUpdate();

	}
	/**
	 * create a resultset for all items ordered for current OrderID
	 * @param connection connect to the JDBC
	 * @param currentOrderID the current order ID
	 * @return the resultset
	 * @throws SQLException
	 */
	public static ResultSet createOrdersResults(Connection connection, int currentOrderID)
			throws SQLException {

		PreparedStatement pStatement = connection
				.prepareStatement("SELECT MealDescription, MealQty, MealPrice FROM orderdetails WHERE OrderID = ?");
		pStatement.setInt(1, currentOrderID);

		ResultSet results = pStatement.executeQuery();
		return results;

	}
	/**
	 * get the total value of the order
	 * @param connection connect to the JDBC
	 * @param currentOrderID the current order ID
	 * @return the resultset
	 * @throws SQLException
	 */
	public static ResultSet createTotalResults(Connection connection, int currentOrderID)
			throws SQLException {
		PreparedStatement pStatement = connection.prepareStatement(
				"UPDATE orders SET TotalValue = (SELECT SUM(MealTotal) FROM orderdetails WHERE OrderID = ?);");
		pStatement.setInt(1, currentOrderID);

		pStatement.executeUpdate();

		pStatement = connection.prepareStatement("SELECT TotalValue FROM orders WHERE OrderID = ?");
		pStatement.setInt(1, currentOrderID);

		ResultSet results = pStatement.executeQuery();

		results.next();
		return results;
	}
	/**
	 * set status
	 * @param connection connect to the JDBC
	 * @param currentOrderID the current order ID
	 * @throws SQLException
	 */
	public static void setStatus(Connection connection, int currentOrderID) throws SQLException {
		PreparedStatement pStatement = connection.prepareStatement("UPDATE orders SET Status = ? WHERE OrderID = ?");
		pStatement.setString(1, "Complete");
		pStatement.setInt(2, currentOrderID);

		pStatement.executeUpdate();

	}
	/**
	 * find incomplete orders
	 * @param connection connect to the JDBC
	 * @throws SQLException
	 */
	public static void getOrderStatus(Connection connection) throws SQLException {
		PreparedStatement pStatement = connection.prepareStatement("SELECT * from orders WHERE Status IS NULL");
		ResultSet results = pStatement.executeQuery();
		printRowFromTable(results);
	}
	/**
	 * Print all rows from a orders resultset
	 * @param results the resultset to print
	 * @throws SQLException
	 */
	public static void printRowFromTable(ResultSet results) throws SQLException {
		System.out.println("Results:\n");
		while (results.next()) {
			System.out.println("Order ID: " + results.getInt("OrderID") + ", Order Number: "
					+ results.getInt("OrderNumber") + ", Special Instructions: " + results.getString("SpecialPrep")
					+ ", Total Value: " + results.getFloat("TotalValue") + ", Status: " + results.getString("Status"));
		}

	}
	/**
	 * Print results from an order search
	 * @param results the resultset to print
	 * @throws SQLException
	 */
	public static void printSearchResults(ResultSet results) throws SQLException {
		System.out.println("Results:\n");
		while (results.next()) {
			System.out.println("OrderID: " + results.getInt("OrderID") + ", Customer Name: "
					+ results.getString("CustomerName") + ", OrderNumber " + results.getInt("OrderNumber")
					+ ", Special Instructions: " + results.getString("SpecialPrep") + ", Total Value: "
					+ results.getFloat("TotalValue") + ", Status: " + results.getString("Status"));
		}

	}
	/**
	 * Method to print order details
	 * @param results the resultset to print
	 * @return the formatted output
	 * @throws SQLException
	 */
	public static String orderDetails(ResultSet results) throws SQLException {
		DecimalFormat df = new DecimalFormat("#.00");
		String output = "";
		while (results.next()) {
			output += "\n" + results.getInt("MealQty") + " x " + results.getString("MealDescription") + " (R"
					+ df.format(results.getFloat("MealPrice")) + ")";
		}

		return output;
	}
	/**
	 * Method to search for an order
	 * @param connection connect to the JDBC
	 * @throws SQLException
	 */
	private static void searchOrder(Connection connection) throws SQLException {
		int searchOrderKey = promptUserInt("1. Search by Order Number\n2. Search by Customer Name\n");

		switch (searchOrderKey) {
		case 1:
			// Search by ID
			int searchOrderID = promptUserInt("Please enter the Order ID to search for:\n");
			String searchQueryID = ("SELECT * FROM orders INNER JOIN customer ON orders.CustomerID = customer.CustomerID WHERE "
					+ "OrderNumber LIKE ?;");

			try {
				PreparedStatement pStatement = null;
				pStatement = connection.prepareStatement(searchQueryID);
				pStatement.setString(1, "%" + searchOrderID + "%");
				ResultSet results = pStatement.executeQuery();
				// If the toy is found, print the results.
				if (!results.isBeforeFirst()) {
					System.out.println("Sorry, can't find that order\n");
				} else {
					System.out.println("The following order/s were found:\n");
					printSearchResults(results);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			break;

		case 2:
			// Search by Name
			String searchOrderName = promptUserStr("Please enter the Customer Name to search for:\n");
			String searchQueryName = ("SELECT * FROM orders INNER JOIN customer ON orders.CustomerID = customer.CustomerID WHERE "
					+ "CustomerName LIKE ?;");

			try {
				PreparedStatement pStatement = null;
				pStatement = connection.prepareStatement(searchQueryName);
				pStatement.setString(1, "%" + searchOrderName + "%");
				ResultSet results = pStatement.executeQuery();
				if (!results.isBeforeFirst()) {
					System.out.println("Sorry, can't find that order\n");
				} else {
					System.out.println("The following order/s were found:\n");
					printSearchResults(results);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		}

	}
	/**
	 * method to request a string from a user
	 * @param input string input from user
	 * @return promptUserStr if the input was not a string
	 */
	private static String promptUserStr(String input) {

		System.out.println(input);
		Scanner methodScan = new Scanner(System.in);
		String inputLine = methodScan.nextLine();
		if (inputLine != "") {
			return inputLine;
		}
		return promptUserStr(input);
	}
	/**
	 * method to request a float from a user
	 * @param input float input from user
	 * @return promptUserFloat if the input was not a float
	 */
	private static Float promptUserFloat(String input) {

		System.out.println(input);
		Scanner methodScan = new Scanner(System.in);
		if (methodScan.hasNextFloat()) {
			return methodScan.nextFloat();
		}
		System.out.print("Please only enter numbers - decimal separator must be a comma\n");
		return promptUserFloat(input);
	}
	/**
	 *  method to request an integer from a user
	 * @param input int input from user
	 * @return promptUserInt if the input was not an int
	 */
	private static int promptUserInt(String input) {

		System.out.println(input);
		Scanner methodScan = new Scanner(System.in);
		if (methodScan.hasNextInt()) {
			return methodScan.nextInt();
		}
		System.out.print("Please only enter a whole number");
		return promptUserInt(input);
	}
}
