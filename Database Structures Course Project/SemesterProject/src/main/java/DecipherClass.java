import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateIndexQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateTableQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.DeleteQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.InsertQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.UpdateQuery;

public class DecipherClass {
	// move the program on to the appropriate class to deal with what the user wants.
		// what instanceOf is the object being passed from the SQLParser: is it a select, insert, etc.
		// use a switch statement to then place the arraylist of info of the object into its appropriate decipherFields class.
	private ResultSet resultSet;
	
	public DecipherClass() {
		resultSet = new ResultSet();
	}
	
	public void dispatchToCorrectPlace (Object theObject) {
		// what instanceOf is the object being passed from the SQLParser: is it a select, insert, etc.
		String className = theObject.getClass().getSimpleName();
		// use a switch statement to then place the arraylist of info of the object into its appropriate decipherFields class.
		try {
			switch (className) { // NEED TO CHECK REGARDING THIS JRE 1.7
				// create table
			case "CreateTableQuery":
				// go to the MainOfCreateTable class
				if (theObject instanceof CreateTableQuery) {
					MainOfCreateTable newTable = new MainOfCreateTable((CreateTableQuery) theObject); 
				}
				break;
				// create index
			case "CreateIndexQuery":
				// go to the MainOfCreateIndex class
					if (theObject instanceof CreateIndexQuery) {
						resultSet.setTableBoolean(true);
						MainOfCreateIndex newIndex = new MainOfCreateIndex((CreateIndexQuery) theObject); 
					}
				break;
				// delete
			case "DeleteQuery":
				// go to the MainOfDelete class
				// set resultSet to true and if one of the parts of the query fails, then it will be changed to false in that class
				if (theObject instanceof DeleteQuery) {
					resultSet.setTableBoolean(true);
					MainOfDelete newTable = new MainOfDelete((DeleteQuery) theObject); 
				}
				break;
				// insert
			case "InsertQuery":
				// go to the MainOfInsert class
				// set resultSet to true and if one of the parts of the query fails, then it will be changed to false in that class
				if (theObject instanceof InsertQuery) {
					resultSet.setTableBoolean(true);
					MainOfInsert newTable = new MainOfInsert((InsertQuery) theObject); 
				}
				break;
				// select
			case "SelectQuery":
				// go to the FieldsOfSelect class
				if (theObject instanceof SelectQuery) {
					MainOfSelect newTable = new MainOfSelect((SelectQuery) theObject); 
				}
				break;
				// update
			case "UpdateQuery":
				// go to the MainOfUpdate class
				// set resultSet to true and if one of the parts of the query fails, then it will be changed to false in that class
				if (theObject instanceof UpdateQuery) {
					resultSet.setTableBoolean(true);
					MainOfUpdate newTable = new MainOfUpdate((UpdateQuery) theObject); 
				}
				break;
				// default
				default:
					System.out.println("ERROR!");
					break;
			}
		}
		catch (Exception e) {
			resultSet.resetResultSet();
			resultSet.setTableBoolean(false);
			e.printStackTrace();
			return;
		}
		
	}
		
}
