import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateTableQuery;

public class BTreeTester {
	private Database database;
	private BTreeDatabase btreeDatabase;
	private Table table;
	
	public BTreeTester() {
		database = new Database();
		btreeDatabase = new BTreeDatabase();
		table = new Table();
		database.addTable(table);
		table.setName("btreeTest");
		
	}

	public void run() {
		// TODO Auto-generated method stub
		table.addFirstRow(2);
		for (int i = 0; i < table.get(0).size(); i++) {
			String element = "1234";
			table.addElementToRow(1, i, element);
		}
		
		//BTree btree = new BTree(table, 4, GPA_Index, GPA);
		
	}
	
}
