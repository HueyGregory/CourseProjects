import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnID;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SQLParser;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery;
import net.sf.jsqlparser.JSQLParserException;

public class MainClass {
	
	private ResultSet resultSet;
	private Database theDatabase;
	private BTreeDatabase btreeDatabase;
	
	public void parseSomeSQl(String someSQL) throws JSQLParserException
	{
		SQLParser parser = new SQLParser();
		SelectQuery result = (SelectQuery)parser.parse("SELECT first, last, id FROM students,teachers,schools;");
		ColumnID[] colsNamedInQuery = result.getSelectedColumnNames();
		System.out.println("The query asked to select the following columns: ");
		for (ColumnID col : colsNamedInQuery) {
			System.out.println(col.getTableName() + "." + col.getColumnName());
		}
	}
	
	public ResultSet execute(String SQL)
	{
		resultSet = new ResultSet();
		theDatabase = new Database();
		btreeDatabase = new BTreeDatabase();
		resultSet.resetResultSet();
		SQLParser parser = new SQLParser();
		try {
			Object result = parser.parse(SQL);
			// send result to the decipher class
			DecipherClass theDecipherClass = new DecipherClass();
			theDecipherClass.dispatchToCorrectPlace(result);
			
			//System.out.println("By the end");
			resultSet.printResultSet();
	//		System.out.println("\nPrint the Database:");
	//		theDatabase.printDatabase();
	//		System.out.println("\nPrint the Btree Database");
	//		btreeDatabase.printBtreeDatabase();
			
		}
		catch (JSQLParserException e) {
			e.printStackTrace();
			resultSet.setTableBoolean(false);
			resultSet.printResultSet();
		}
		catch (Exception e) {
			e.printStackTrace();
			resultSet.setTableBoolean(false);
			resultSet.printResultSet();
		}
		return resultSet;
	}
}
