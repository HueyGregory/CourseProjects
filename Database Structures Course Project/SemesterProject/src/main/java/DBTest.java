
public class DBTest {
	
	private MainClass mainClass;
	private BTreeDatabase btreeDatabase = new BTreeDatabase();

	public static void main(String[] args) {
		
//		TesterClasses testerClasses = new TesterClasses();
//		testerClasses.run();
//		
//		smallTests();
		
		DBTest dbTest = new DBTest();
		dbTest.run();
	}
	
	private static void smallTests() {
		// TODO Auto-generated method stub
		String value = "false";
		Character char2 = '\10';
		String value2 = Character.toString(char2);
		System.out.println(value.compareTo(value2));
//		Double theDouble = Double.valueOf(value);
//		Double theDouble2 = Double.valueOf(value2);
//		Boolean comparison = theDouble < theDouble2;
//		System.out.println(comparison);
//		System.out.println(theDouble);
		
	}

	public DBTest() {
		mainClass = new MainClass();
	}
	
	public void run() {
		// set up the tables
		setUpTables();
		
		// select
		select();
		
		// create index test
		createIndexTest();
		
		// insert test
		insertTest();
		
		// strange SQL Queries just to make sure it returns a false resultSet
		SQLTest();
		
		// update test
		updateTest();
		
		// select test
		selectTest();
		
		// delete test
		deleteTest();
		
		// delete values from the tables
		clear();		
		
	}
	
	private void SQLTest() {
		System.out.println("DELETE asdf kj;lqwe r");
		mainClass.execute("DELETE asdf kj;lqwe r");

		System.out.println("Create qwe rqwe rq qwr");
		mainClass.execute("Create qwe rqwe rq qwr");
		
	}

	private void createIndexTest() {
		// TODO Auto-generated method stub
		System.out.println();
		System.out.println("\nTesting for strange Create Indexes\n");
		
		System.out.println();
		System.out.println("column in CREATE INDEX does not exist - false");
		System.out.println("CREATE INDEX nonExistant_INDEX on MalcheiYisroel(nonExistantColumn)");
		mainClass.execute("CREATE INDEX nonExistant_INDEX on MalcheiYisroel(nonExistantColumn)");
		
		
		System.out.println();
		System.out.println("table in CREATE INDEX does not exist - false");
		System.out.println("CREATE INDEX reign_INDEX on MalcheiSedom(reign)");
		mainClass.execute("CREATE INDEX reign_INDEX on MalcheiSedom(reign)");
		
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");
	}

	private void deleteTest() {
		System.out.println();
		System.out.println("\nTesting for strange Deletes\n");
		
		System.out.println();
		System.out.println("column in DELETE does not exist - false");
		System.out.println("DELETE FROM MalcheiYisroel WHERE nonExistantColumn = 21 AND assassinated = false");
		mainClass.execute("DELETE FROM MalcheiYisroel WHERE nonExistantColumn = 21 AND assassinated = false");
		
		System.out.println();
		System.out.println("table in DELETE does not exist - false");
		System.out.println("DELETE FROM MalcheiSedom WHERE assassinated = false");
		mainClass.execute("DELETE FROM MalcheiSedom WHERE assassinated = false");
		
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");
	}

	private void selectTest() {
		System.out.println();
		System.out.println("\nTesting for strange Selects\n");
		
		testSelectNull();
		testSelectFunctionWrongType();
		testSelectNonExistant();
		
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");
	}

	private void testSelectNull() {
		System.out.println();
		System.out.println("select null, RETURNS something");
		System.out.println("SELECT reign, father FROM MalcheiYisroel WHERE father = null");
		mainClass.execute("SELECT reign, father FROM MalcheiYisroel WHERE father = null");
	}

	private void testSelectFunctionWrongType() {
		System.out.println();
		System.out.println("column in function does not exist - false");
		System.out.println("SELECT MAX(assassinated) FROM MalcheiYisroel");
		mainClass.execute("SELECT MAX(assassinated) FROM MalcheiYisroel");
		
		System.out.println("SELECT MIN(assassinated) FROM MalcheiYisroel");
		mainClass.execute("SELECT MIN(assassinated) FROM MalcheiYisroel");
		
		System.out.println("SELECT SUM(firstName) FROM MalcheiYisroel");
		mainClass.execute("SELECT SUM(firstName) FROM MalcheiYisroel");
		
		System.out.println("SELECT SUM(assassinated) FROM MalcheiYisroel");
		mainClass.execute("SELECT SUM(assassinated) FROM MalcheiYisroel");
		
		System.out.println("SELECT AVG(father) FROM MalcheiYisroel");
		mainClass.execute("SELECT AVG(father) FROM MalcheiYisroel");
		
		System.out.println("SELECT AVG(assassinated) FROM MalcheiYisroel");
		mainClass.execute("SELECT AVG(assassinated) FROM MalcheiYisroel");
		
		System.out.println("SELECT MAX(assassinated), MIN(assassinated), SUM(firstName), SUM(assassinated), AVG(father), AVG(assassinated) FROM MalcheiYisroel");
		mainClass.execute("SELECT MAX(assassinated), MIN(assassinated), SUM(firstName), SUM(assassinated), AVG(father), AVG(assassinated) FROM MalcheiYisroel");

	}

	private void testSelectNonExistant() {
		System.out.println();
		System.out.println("table does not exist - false");
		System.out.println("SELECT * FROM MalcheiSedom");
		mainClass.execute("SELECT * FROM MalcheiSedom");
		
		System.out.println();
		System.out.println("column in Select does not exist - false");
		System.out.println("SELECT nonExistantColumn FROM MalcheiYisroel WHERE FirstName = 50");
		mainClass.execute("SELECT nonExistantColumn FROM MalcheiYisroel WHERE FirstName = 50");
		
		System.out.println();
		System.out.println("column in WHERE condition does not exist - false");
		System.out.println("SELECT reign FROM MalcheiYisroel WHERE nonExistantColumn = true");
		mainClass.execute("SELECT reign FROM MalcheiYisroel WHERE nonExistantColumn = true");
		
		System.out.println();
		System.out.println("column in SELECT DISTINCT does not exist - false");
		System.out.println("SELECT DISTINCT nonExistantColumn FROM MalcheiYisroel");
		mainClass.execute("SELECT DISTINCT nonExistantColumn FROM MalcheiYisroel");
		
		System.out.println();
		System.out.println("column in function does not exist - false");
		System.out.println("SELECT MAX(nonExistantColumn), MIN(nonExistantColumn), SUM(nonExistantColumn), AVG(nonExistantColumn), COUNT(nonExistantColumn) FROM MalcheiYisroel");
		mainClass.execute("SELECT MAX(nonExistantColumn), MIN(nonExistantColumn), SUM(nonExistantColumn), AVG(nonExistantColumn), COUNT(nonExistantColumn) FROM MalcheiYisroel");
		
	}

	private void updateTest() {
		System.out.println();
		System.out.println("\nTesting for strange Updates\n");
		
		testUpdateOverLengthLimit();
		testUpdateNull();
		testUpdateWrongType();
		testUpdateNonExistant();
		
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");

	}

	private void testUpdateNonExistant() {
		System.out.println();
		System.out.println("table does not exist - false");
		System.out.println("UPDATE MalcheiSedom SET nonExistantColumn = 21, assassinated = false");
		mainClass.execute("UPDATE MalcheiSedom SET nonExistantColumn = 21, assassinated = false");
		
		System.out.println();
		System.out.println("column in WHERE condition does not exist - false");
		System.out.println("UPDATE MalcheiYisroel SET reign = 21, assassinated = false WHERE nonExistantColumn = 50");
		mainClass.execute("UPDATE MalcheiYisroel SET reign = 21, assassinated = false WHERE nonExistantColumn = 50");
		
		System.out.println();
		System.out.println("column in UPDATE does not exist - false");
		System.out.println("UPDATE MalcheiYisroel SET nonExistantColumn = 21, assassinated = false");
		mainClass.execute("UPDATE MalcheiYisroel SET nonExistantColumn = 21, assassinated = false");
	}

	private void testUpdateWrongType() {
		System.out.println();
		System.out.println("VARCHAR into INT - false");
		System.out.println("UPDATE MalcheiYisroel SET FirstName = 'not Good', num = 'strInInt'");
		mainClass.execute("UPDATE MalcheiYisroel SET FirstName = 'not Good', num = 'strInInt'");
		
		System.out.println();
		System.out.println("INT into VARCHAR - false");
		System.out.println("UPDATE MalcheiYisroel SET FirstName = 18");
		mainClass.execute("UPDATE MalcheiYisroel SET FirstName = 18");
		
		System.out.println();
		System.out.println("INT into BOOLEAN - false");
		System.out.println("UPDATE MalcheiYisroel SET assassinated = 18");
		mainClass.execute("UPDATE MalcheiYisroel SET assassinated = 18");
		
		System.out.println();
		System.out.println("BOOLEAN into INT - false");
		System.out.println("UPDATE MalcheiYisroel SET num = false");
		mainClass.execute("UPDATE MalcheiYisroel SET num = false");
		
		System.out.println();
		System.out.println("BOOLEAN into VARCHAR - false");
		System.out.println("UPDATE MalcheiYisroel SET FirstName = false");
		mainClass.execute("UPDATE MalcheiYisroel SET FirstName = false");
		
		System.out.println();
		System.out.println("VARCHAR into BOOLEAN - false");
		System.out.println("UPDATE MalcheiYisroel SET FirstName = 'whatever', assassinated = 'strInBool'");
		mainClass.execute("UPDATE MalcheiYisroel SET FirstName = 'whatever', assassinated = 'strInBool'");
		
		System.out.println();
		System.out.println("DECIMAL into INT - false");
		System.out.println("UPDATE MalcheiYisroel SET num = 12.45");
		mainClass.execute("UPDATE MalcheiYisroel SET num = 12.45");
		
		System.out.println();
		System.out.println("DECIMAL into BOOLEAN - false");
		System.out.println("UPDATE MalcheiYisroel SET assassinated = 23.3");
		mainClass.execute("UPDATE MalcheiYisroel SET assassinated = 23.3");
		
		System.out.println();
		System.out.println("BOOLEAN into DECIMAL - false");
		System.out.println("UPDATE MalcheiYisroel SET reign = false");
		mainClass.execute("UPDATE MalcheiYisroel SET reign = false");
		
		System.out.println();
		System.out.println("DECIMAL into VARCHAR - false");
		System.out.println("UPDATE MalcheiYisroel SET FirstName = 18.3");
		mainClass.execute("UPDATE MalcheiYisroel SET FirstName = 18.3");
		
		System.out.println();
		System.out.println("VARCHAR into DECIMAL - false");
		System.out.println("UPDATE MalcheiYisroel SET reign = 'strInDec'");
		mainClass.execute("UPDATE MalcheiYisroel SET reign = 'strInDec'");
	}

	private void testUpdateNull() {
		// TODO Auto-generated method stub
		System.out.println();
		System.out.println("null into INT - true");
		System.out.println("UPDATE MalcheiYisroel SET num = null");
		mainClass.execute("UPDATE MalcheiYisroel SET num = null");
		
		System.out.println();
		System.out.println("null into BOOLEAN - true");
		System.out.println("UPDATE MalcheiYisroel SET assassinated = null");
		mainClass.execute("UPDATE MalcheiYisroel SET assassinated = null");
		
		System.out.println();
		System.out.println("null into DECIMAL - true");
		System.out.println("UPDATE MalcheiYisroel SET reign = null");
		mainClass.execute("UPDATE MalcheiYisroel SET reign = null");
		
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");
		
		System.out.println();
		System.out.println("null into VARCHAR - true");
		System.out.println("UPDATE MalcheiYisroel SET father = null");
		mainClass.execute("UPDATE MalcheiYisroel SET father = null");
		
		System.out.println();
		System.out.println("null into VARCHAR NOT NULL - false");
		System.out.println("UPDATE MalcheiYisroel SET FirstName = null");
		mainClass.execute("UPDATE MalcheiYisroel SET FirstName = null");
	}

	private void testUpdateOverLengthLimit() {
		System.out.println();
		System.out.println("Over length limit for VARCHAR");
		System.out.println("UPDATE MalcheiYisroel SET FirstName = 'testLengthOverLimit'");
		mainClass.execute("UPDATE MalcheiYisroel SET FirstName = 'testLengthOverLimit'");
		
		System.out.println();
		System.out.println("Over length limit for DECIMAL");
		System.out.println("UPDATE MalcheiYisroel SET reign = 2.345");
		mainClass.execute("UPDATE MalcheiYisroel SET reign = 2.345");
		System.out.println("UPDATE MalcheiYisroel SET reign = 234.5");
		mainClass.execute("UPDATE MalcheiYisroel SET reign = 234.5");
		System.out.println("UPDATE MalcheiYisroel SET reign = 122.345");
		mainClass.execute("UPDATE MalcheiYisroel SET reign = 122.345");
		
		System.out.println();
		System.out.println("Update INT - true");
		System.out.println("UPDATE MalcheiYisroel SET num = 2022");
		mainClass.execute("UPDATE MalcheiYisroel SET num = 2022");
		
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");
	}
	

	private void insertTest() {
		System.out.println();
		System.out.println("\nTesting for strange Inserts\n");
		
		testInsertOverLengthLimit();
		testInsertNull();
		testInsertWrongType();
		testInsertNonExistant();
		
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");

	}

	private void testInsertNonExistant() {
		System.out.println();
		System.out.println("table does not exist - false");
		System.out.println("INSERT INTO MalcheiSedom (FirstName, num) VALUES ('resultSet = false', 18)");
		mainClass.execute("INSERT INTO MalcheiSedom (FirstName, num) VALUES ('resultSet = false', 18)");
		
		System.out.println();
		System.out.println("column does not exist - false");
		System.out.println("INSERT INTO MalcheiYisroel (nonExistantColumn, num) VALUES ('asdf', 20)");
		mainClass.execute("INSERT INTO MalcheiYisroel (nonExistantColumn, num) VALUES ('asdf', 20)");	
		
	}

	private void testInsertWrongType() {
		System.out.println();
		System.out.println("VARCHAR into INT - false");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('not Good', 'stringInIntSlot')");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('not Good', 'stringInIntSlot')");
		
		System.out.println();
		System.out.println("INT into VARCHAR - false");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, num) VALUES (18, 20)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, num) VALUES (18, 20)");
		
		System.out.println();
		System.out.println("INT into BOOLEAN - false");
		System.out.println("INSERT INTO MalcheiYisroel (assassinated, father) VALUES (18, 'whatever')");
		mainClass.execute("INSERT INTO MalcheiYisroel (assassinated, father) VALUES (18, 'whatever')");
		
		System.out.println();
		System.out.println("BOOLEAN into INT - false");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('whatever', false)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('whatever', false)");
		
		System.out.println();
		System.out.println("BOOLEAN into VARCHAR - false");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, num) VALUES (false, 18)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, num) VALUES (false, 18)");
		
		System.out.println();
		System.out.println("VARCHAR into BOOLEAN - false");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, assassinated) VALUES ('whatever', 'strInBool')");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, assassinated) VALUES ('whatever', 'strInBool')");
		
		System.out.println();
		System.out.println("DECIMAL into INT - false");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('whatever', 12.45)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('whatever', 12.45)");
		
		System.out.println();
		System.out.println("DECIMAL into BOOLEAN - false");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, assassinated) VALUES ('whatever', 23.3)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, assassinated) VALUES ('whatever', 23.3)");
		
		System.out.println();
		System.out.println("BOOLEAN into DECIMAL - false");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, reign) VALUES ('whatever', false)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, reign) VALUES ('whatever',false)");
		
		System.out.println();
		System.out.println("DECIMAL into VARCHAR - false");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, num) VALUES (18.3, 20)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, num) VALUES (18.3, 20)");
		
		System.out.println();
		System.out.println("VARCHAR into DECIMAL - false");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, reign) VALUES ('whatever', 'strInDec')");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, reign) VALUES ('whatever', 'strInDec')");
	}

	private void testInsertNull() {
		System.out.println();
		System.out.println("null insertion into VARCHAR - true and inserts it");
		System.out.println("INSERT INTO MalcheiYisroel (father, num, firstName) VALUES (null, 321, 'goodStr')");
		mainClass.execute("INSERT INTO MalcheiYisroel (father, num, firstName) VALUES (null, 321, 'goodStr')");
		
		System.out.println();
		System.out.println("null insertion into VARCHAR NOT NULL - false, but doesn't do it");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, num) VALUES (null, 102)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, num) VALUES (null, 102)");
		
		// "Select * from table",
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");
		
		System.out.println();
		System.out.println("null insertion into DECIMAL - true and inserts it");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, reign, num) VALUES ('goodDec', null, 345)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, reign, num) VALUES ('goodDec', null, 345)");
		
		// "Select * from table",
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");
		
		System.out.println();
		System.out.println("null insertion into INT - true");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('goodInt', null)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('goodInt', null)");
		
		// "Select * from table",
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");
		
		System.out.println();
		System.out.println("null insertion into BOOLEAN - true");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, assassinated, num) VALUES ('goodBoolean', null, 567)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, assassinated, num) VALUES ('goodBoo', null, 567)");
		
		// "Select * from table",
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");
		
	}

	private void testInsertOverLengthLimit() {
		System.out.println();
		System.out.println("Over length limit for VARCHAR - false");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('testLengthOverLimit', 20)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('testLengthOverLimit', 20)");
		
		System.out.println();
		System.out.println("Over length limit for DECIMAL - false");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, reign) VALUES ('good', 2.345)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, reign) VALUES ('good', 2.345)");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, reign) VALUES ('good', 234.5)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, reign) VALUES ('good', 234.5)");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, reign) VALUES ('good', 122.345)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, reign) VALUES ('good', 122.345)");
		
		System.out.println();
		System.out.println("Over length limit for VARCHAR - false");
		System.out.println("INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('testLengthOverLimit', 2022)");
		mainClass.execute("INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('testLengthOverLimit', 2022)");
		
		
	}

	private void clear() {
		// delete where reign < 3.5
		System.out.println("DELETE FROM MalcheiYisroel WHERE reign < 10");
		mainClass.execute("DELETE FROM MalcheiYisroel WHERE reign < 10");
		System.out.println("DELETE FROM MalcheiYehuda WHERE reign < 10");
		mainClass.execute("DELETE FROM MalcheiYehuda WHERE reign < 10");
		
		System.out.println();
		
		// "Select Distinct count (reign) from table",
		System.out.println("SELECT COUNT (DISTINCT reign) FROM MalcheiYisroel");
		mainClass.execute("SELECT COUNT (DISTINCT reign) FROM MalcheiYisroel");
		System.out.println("SELECT COUNT (DISTINCT reign) FROM MalcheiYehuda");
		mainClass.execute("SELECT COUNT (DISTINCT reign) FROM MalcheiYehuda");
		
		// delete from table
		System.out.println("DELETE FROM MalcheiYisroel WHERE reign < 20 AND assassinated = true");
		mainClass.execute("DELETE FROM MalcheiYisroel WHERE reign < 20 AND assassinated = true");
		System.out.println("DELETE FROM MalcheiYehuda WHERE reign < 20 AND hadBamos = true");
		mainClass.execute("DELETE FROM MalcheiYehuda WHERE reign < 20 AND hadBamos = true");
		
		// "Select * from table",
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");
		System.out.println("SELECT * FROM MalcheiYehuda");
		mainClass.execute("SELECT * FROM MalcheiYehuda");
		
		// delete table
		System.out.println("DELETE FROM MalcheiYisroel");
		mainClass.execute("DELETE FROM MalcheiYisroel");
		System.out.println("DELETE FROM MalcheiYehuda");
		mainClass.execute("DELETE FROM MalcheiYehuda");
		
		// "Select * from table",
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");
		System.out.println("SELECT * FROM MalcheiYehuda");
		mainClass.execute("SELECT * FROM MalcheiYehuda");
		
	}

	private void select() {
		
		System.out.println();
		
		// select FirstName, reign, num Order By reign, num
		System.out.println("SELECT FirstName, reign, num FROM MalcheiYisroel ORDER BY reign ASC, num DESC");
		mainClass.execute("SELECT FirstName, reign, num FROM MalcheiYisroel ORDER BY reign ASC, num DESC");
		System.out.println("SELECT FirstName, reign, num FROM MalcheiYehuda ORDER BY reign ASC, num DESC");
		mainClass.execute("SELECT FirstName, reign, num FROM MalcheiYehuda ORDER BY reign ASC, num DESC");
		
		System.out.println();
		
		// select * ORDER BY (artitrary) FROM table
		System.out.println("SELECT * FROM MalcheiYisroel ORDER BY assassinated DESC");
		mainClass.execute("SELECT * FROM MalcheiYisroel ORDER BY assassinated DESC");
		System.out.println("SELECT * FROM MalcheiYehuda ORDER BY hadBamos ASC");
		mainClass.execute("SELECT * FROM MalcheiYehuda ORDER BY hadBamos ASC");
		
		System.out.println();
		
		// select FirstName ORDER BY FirstName FROM table
		System.out.println("SELECT FirstName FROM MalcheiYisroel ORDER BY FirstName DESC");
		mainClass.execute("SELECT FirstName FROM MalcheiYisroel ORDER BY FirstName DESC");
		System.out.println("SELECT FirstName FROM MalcheiYehuda ORDER BY FirstName ASC");
		mainClass.execute("SELECT FirstName FROM MalcheiYehuda ORDER BY FirstName ASC");
		
		System.out.println();
		
		// SELECT SUM(reign), AVG(reign) FROM each table,
		System.out.println("SELECT SUM(reign), AVG(reign), COUNT(reign), SUM (DISTINCT reign), MAX(reign), MIN(reign) FROM MalcheiYisroel");
		mainClass.execute("SELECT SUM(reign), AVG(reign), COUNT(reign), SUM (DISTINCT reign), MAX(reign), MIN(reign) FROM MalcheiYisroel");
		System.out.println("SELECT SUM(reign), AVG(reign), COUNT(reign), SUM (DISTINCT reign), MAX(reign), MIN(reign) FROM MalcheiYehuda");
		mainClass.execute("SELECT SUM(reign), AVG(reign), COUNT(reign), SUM (DISTINCT reign), MAX(reign), MIN(reign) FROM MalcheiYehuda");
		
		System.out.println();
		
		// SELECT DISTINCT of each type of function
		System.out.println("SELECT SUM(DISTINCT reign), AVG(DISTINCT reign), COUNT(DISTINCT reign), SUM (reign), MAX(DISTINCT reign), MIN(DISTINCT reign) FROM MalcheiYisroel");
		mainClass.execute("SELECT SUM(DISTINCT reign), AVG(DISTINCT reign), COUNT(DISTINCT reign), SUM (reign), MAX(DISTINCT reign), MIN(DISTINCT reign) FROM MalcheiYisroel");
		System.out.println("SELECT SUM(DISTINCT reign), AVG(DISTINCT reign), COUNT(DISTINCT reign), SUM (reign), MAX(DISTINCT reign), MIN(DISTINCT reign) FROM MalcheiYehuda");
		mainClass.execute("SELECT SUM(DISTINCT reign), AVG(DISTINCT reign), COUNT(DISTINCT reign), SUM (reign), MAX(DISTINCT reign), MIN(DISTINCT reign) FROM MalcheiYehuda");

		System.out.println();
		
		// "SELECT Distinct FirstName, reign FROM table WHERE reign > 20",
		System.out.println("SELECT Distinct FirstName, reign FROM MalcheiYisroel WHERE reign > 20");
		mainClass.execute("SELECT Distinct FirstName, reign FROM MalcheiYisroel WHERE reign > 20");
		System.out.println("SELECT Distinct FirstName, reign FROM MalcheiYehuda WHERE reign > 20");
		mainClass.execute("SELECT Distinct FirstName, reign FROM MalcheiYehuda WHERE reign > 20");
		
		System.out.println();
		
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");
		System.out.println("SELECT * FROM MalcheiYehuda");
		mainClass.execute("SELECT * FROM MalcheiYehuda");
		
		System.out.println();
		
		// "SELECT Distinct FirstName, reign, num FROM table WHERE reign > 20",
		System.out.println("SELECT Distinct num, FirstName, reign FROM MalcheiYisroel WHERE reign > 20 OR num < 15");
		mainClass.execute("SELECT Distinct num, FirstName, reign FROM MalcheiYisroel WHERE reign > 20 OR num < 15");
		System.out.println("SELECT Distinct num, FirstName, reign FROM MalcheiYehuda WHERE reign > 20 AND num > 10");
		mainClass.execute("SELECT Distinct num, FirstName, reign FROM MalcheiYehuda WHERE reign > 20 AND num > 10");
		
		System.out.println();

		// SELECT MAX (FirstName) MIN (FirstName) FROM table
		System.out.println("SELECT MAX (FirstName), MIN (FirstName) FROM MalcheiYisroel");
		mainClass.execute("SELECT MAX (FirstName), MIN (FirstName) FROM MalcheiYisroel");
		System.out.println("SELECT MAX (FirstName), MIN (FirstName) FROM MalcheiYehuda");
		mainClass.execute("SELECT MAX (FirstName), MIN (FirstName) FROM MalcheiYehuda");
		
		System.out.println();
		
		// Select AVG (DISTINCT num) FROM table
		System.out.println("SELECT AVG (Distinct num) FROM MalcheiYisroel");
		mainClass.execute("SELECT AVG (Distinct num) FROM MalcheiYisroel");
		System.out.println("SELECT AVG (Distinct num) FROM MalcheiYehuda");
		mainClass.execute("SELECT AVG (Distinct num) FROM MalcheiYehuda");
		
		System.out.println();
		
		// SELECT SUM (num) FROM table
		System.out.println("SELECT SUM (num) FROM MalcheiYisroel");
		mainClass.execute("SELECT SUM (num) FROM MalcheiYisroel");
		System.out.println("SELECT SUM (num) FROM MalcheiYehuda");
		mainClass.execute("SELECT SUM (num) FROM MalcheiYehuda");
		
		System.out.println();
		
		System.out.println("Select DISTINCT firstName, reign FROM MalcheiYisroel Order BY reign ASC");
		mainClass.execute("Select DISTINCT firstName, reign FROM MalcheiYisroel Order BY reign ASC");
	
		System.out.println();
		
		System.out.println("Select DISTINCT firstName FROM MalcheiYisroel Order BY reign ASC");
		mainClass.execute("Select DISTINCT firstName FROM MalcheiYisroel Order BY reign ASC");

		System.out.println();
		
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");
		
	}

	private void setUpTables() {
		// first create two tables: 
		createTable();
		
		System.out.println();
					
		// insert the kings' first names and num
		insertNameNumYehuda();
		System.out.println();
		insertNameNumYisroel();
		
		System.out.println();
		
		// select * ORDER BY num
		System.out.println("SELECT * FROM MalcheiYehuda ORDER BY num ASC");
		mainClass.execute("SELECT * FROM MalcheiYehuda ORDER BY num ASC");
		System.out.println("SELECT * FROM MalcheiYisroel ORDER BY num DESC");
		mainClass.execute("SELECT * FROM MalcheiYisroel ORDER BY num DESC");
		
		System.out.println();
		
		// create index
		System.out.println("CREATE INDEX reign_INDEX on MalcheiYehuda(reign)");
		mainClass.execute("CREATE INDEX reign_INDEX on MalcheiYehuda(reign)");
		System.out.println("CREATE INDEX reign_INDEX on MalcheiYisroel(reign)");
		mainClass.execute("CREATE INDEX reign_INDEX on MalcheiYisroel(reign)");
		
		System.out.println();

		// update each table to put in father
		updateFatherYehuda();
		System.out.println();
		updateFatherYisroel();
		
		System.out.println();

		// select COUNT (father) FROM table
		System.out.println("SELECT COUNT (father) FROM MalcheiYisroel");
		mainClass.execute("SELECT COUNT (father) FROM MalcheiYisroel");
		System.out.println("SELECT COUNT (father) FROM MalcheiYehuda");
		mainClass.execute("SELECT COUNT (father) FROM MalcheiYehuda");
		
		System.out.println();
		
		// select DISTINCT COUNT (father) FROM table
		System.out.println("SELECT DISTINCT COUNT (father) FROM MalcheiYisroel");
		mainClass.execute("SELECT DISTINCT COUNT (father) FROM MalcheiYisroel");
		System.out.println("SELECT DISTINCT COUNT (father) FROM MalcheiYehuda");
		mainClass.execute("SELECT DISTINCT COUNT (father) FROM MalcheiYehuda");
		
		System.out.println();
		
		// update each table: reign, (arbitrary)
		updateReignBamosYehuda();
		System.out.println();
		updateReignAssassinatedYisroel();
				
		System.out.println();
		
		// select FirstName, (arbitrary) where (arbitrary) = true
		System.out.println("SELECT FirstName, assassinated FROM MalcheiYisroel WHERE assassinated = true");
		mainClass.execute("SELECT FirstName, assassinated FROM MalcheiYisroel WHERE assassinated = true");
		System.out.println("SELECT FirstName, hadBamos FROM MalcheiYehuda WHERE hadBamos = true");
		mainClass.execute("SELECT FirstName, hadBamos FROM MalcheiYehuda WHERE hadBamos = true");
		
		System.out.println();
		
		// select FirstName, (arbitrary) where (arbitrary) = null
		System.out.println("SELECT FirstName, assassinated FROM MalcheiYisroel WHERE assassinated = null");
		mainClass.execute("SELECT FirstName, assassinated FROM MalcheiYisroel WHERE assassinated = null");
		System.out.println("SELECT FirstName, hadBamos FROM MalcheiYehuda WHERE hadBamos = null");
		mainClass.execute("SELECT FirstName, hadBamos FROM MalcheiYehuda WHERE hadBamos = null");
		
		System.out.println();
		
		// select all rows from the tables
		System.out.println("SELECT * FROM MalcheiYisroel");
		mainClass.execute("SELECT * FROM MalcheiYisroel");
		System.out.println("SELECT * FROM MalcheiYehuda");
		mainClass.execute("SELECT * FROM MalcheiYehuda");
		
		System.out.println();
		
	}

	private void updateReignAssassinatedYisroel() {
		String[] insertInput = new String[] {
				"UPDATE MalcheiYisroel SET reign = 21, assassinated = false", // setting entire column of the table to a given value
				"UPDATE MalcheiYisroel SET reign = 2, assassinated = true WHERE father = 'Yeravam' OR FirstName = 'Nadav'",
				"UPDATE MalcheiYisroel SET reign = 23 WHERE father = 'Achiya' AND num = 3",
				"UPDATE MalcheiYisroel SET reign = 1, assassinated = true WHERE father = 'Basha' AND  FirstName = 'Eileh' AND num = 4",
				"UPDATE MalcheiYisroel SET reign = .01, assassinated = null WHERE father = null AND (FirstName = 'Omri' OR FirstName = 'Zimri')",
				"UPDATE MalcheiYisroel SET reign = 11, assassinated = true WHERE father = null AND (FirstName = 'Omri')",
				"UPDATE MalcheiYisroel SET reign = 20, assassinated = true WHERE father = 'Omri' AND FirstName = 'Achav' OR num = 8", // purposely set achaziya's father to omri, but will change it after
				"UPDATE MalcheiYisroel SET reign = 3, assassinated = false WHERE father = 'Achav' OR num > 7", // both Achazia and Yehorams' father was Achav
				"UPDATE MalcheiYisroel SET reign = 12, assassinated = true WHERE FirstName = 'Yehoram'",
				"UPDATE MalcheiYisroel SET reign = 28, assassinated = false WHERE father = 'Nimshi' AND FirstName = 'Yayhu'",
				"UPDATE MalcheiYisroel SET reign = 15, assassinated = false WHERE father = 'Yayhu' AND num = 11",
				"UPDATE MalcheiYisroel SET reign = 16, assassinated = false WHERE father = 'Yehoachaz' AND FirstName = 'Yoash'",
				"UPDATE MalcheiYisroel SET reign = 39, assassinated = false WHERE father = 'Yoash' AND  FirstName = 'Yeravam' AND num = 13",
				"UPDATE MalcheiYisroel SET reign = 0.5, assassinated = true WHERE father = 'Yeravam' AND FirstName = 'Zecharia' AND assassinated = true",
				"UPDATE MalcheiYisroel SET reign = 2, assassinated = false WHERE father = 'Gadi' AND FirstName = 'Menachem'",
				"UPDATE MalcheiYisroel SET reign = .08, assassinated = true WHERE father = 'Yavesh' AND num = 15",
				"UPDATE MalcheiYisroel SET reign = 3, assassinated = true WHERE father = 'Menachem' AND FirstName = 'Pekachia'",
				"UPDATE MalcheiYisroel SET reign = 21, assassinated = true WHERE father = 'Ramaliya' OR FirstName = 'Pekach'",
				"UPDATE MalcheiYisroel SET reign = 18 WHERE father = 'Eilla' AND num = 19",
				
		};
		for (int index = 0; index < insertInput.length; index++) {
			if (insertInput[index].equals("UPDATE MalcheiYisroel SET reign = 21, assassinated = false")) {
				System.out.println();
			}
			System.out.println(insertInput[index]);
			mainClass.execute(insertInput[index]);
		}
	}

	private void updateReignBamosYehuda() {
		String[] insertInput = new String[] {
				"UPDATE MalcheiYehuda SET reign = 17, hadBamos = true", // setting entire column of the table to a given value
				"UPDATE MalcheiYehuda SET reign = 3 WHERE father = 'Rechavam'",
				"UPDATE MalcheiYehuda SET reign = 41 WHERE father = 'Aviah' AND num = 3",
				"UPDATE MalcheiYehuda SET reign = 23 WHERE father = 'Assa'",
				"UPDATE MalcheiYehuda SET reign = 8 WHERE father = 'Yehoshafat'",
				"UPDATE MalcheiYehuda SET reign = 2 WHERE father = 'Yehoram'",
				"UPDATE MalcheiYehuda SET reign = 5 WHERE father = 'Bas Achav'", 
				"UPDATE MalcheiYehuda SET reign = 39 WHERE FirstName = 'Yehoash'",
				"UPDATE MalcheiYehuda SET reign = 15 WHERE father = 'Yehoash'",
				"UPDATE MalcheiYehuda SET reign = 52 WHERE FirstName = 'Uzzia'",
				"UPDATE MalcheiYehuda SET reign = 16 WHERE father = 'Uzzia'",
				"UPDATE MalcheiYehuda SET reign = 16 WHERE father = 'Yosam'",
				"UPDATE MalcheiYehuda SET reign = 29, hadBamos = false WHERE FirstName = 'Chizkiyahu'",
				"UPDATE MalcheiYehuda SET reign = 55 WHERE father = 'Chizkiyahu'",
				"UPDATE MalcheiYehuda SET reign = 3 WHERE father = 'Menashe'",
				"UPDATE MalcheiYehuda SET reign = 31 WHERE FirstName = 'Yoshiyahu'",
				"UPDATE MalcheiYehuda SET reign = .25 WHERE father = 'Yoshiyahu'", // update three kings and then in the next line change one of them
				"UPDATE MalcheiYehuda SET reign = 11 WHERE father = 'Yoshiyahu' AND FirstName = 'Yehoyakim' OR num = 19", //update two kings
				"UPDATE MalcheiYehuda SET reign = 0.25 WHERE father = 'Yehoyakim'",
				"UPDATE MalcheiYehuda SET reign = 11 WHERE father = 'Yoshiyahu' AND num = 19", // Tzidkiyahu hamelech
				
		};
		for (int index = 0; index < insertInput.length; index++) {
			if (insertInput[index].equals("UPDATE MalcheiYehuda SET reign = 11 WHERE father = 'Yoshiyahu' AND num = 19")) {
				System.out.println();
			}
			System.out.println(insertInput[index]);
			mainClass.execute(insertInput[index]);
		}
	}

	private void updateFatherYisroel() {
		// update each table to put in father
		String[] insertInput = new String[] {
				"UPDATE MalcheiYisroel SET father = 'Navat'", // setting entire column of the table to a given value
				"UPDATE MalcheiYisroel SET father = 'Yeravam' WHERE FirstName = 'Nadav'",
				"UPDATE MalcheiYisroel SET father = 'Achiya' WHERE num = 3",
				"UPDATE MalcheiYisroel SET father = 'Basha' WHERE  FirstName = 'Eileh' AND num = 4",
				"UPDATE MalcheiYisroel SET father = null WHERE FirstName = 'Omri' OR FirstName = 'Zimri'",
				"UPDATE MalcheiYisroel SET father = 'Omri' WHERE FirstName = 'Achav' OR num = 8", // purposely set achaziya's father to omri, but will change it after
				"UPDATE MalcheiYisroel SET father = 'Achav' WHERE num > 7", // both Achazia and Yehorams' father was Achav
				"UPDATE MalcheiYisroel SET father = 'Nimshi' WHERE FirstName = 'Yayhu'",
				"UPDATE MalcheiYisroel SET father =  'Yayhu' WHERE num = 11",
				"UPDATE MalcheiYisroel SET father =  'Yehoachaz' WHERE FirstName = 'Yoash'",
				"UPDATE MalcheiYisroel SET father =  'Yoash' WHERE FirstName = 'Yeravam' AND num = 13",
				"UPDATE MalcheiYisroel SET father =  'Yeravam' WHERE FirstName = 'Zecharia'",
				"UPDATE MalcheiYisroel SET father =  'Gadi' WHERE FirstName = 'Menachem'",
				"UPDATE MalcheiYisroel SET father =  'Yavesh' WHERE num = 15",
				"UPDATE MalcheiYisroel SET father =  'Menachem' WHERE FirstName = 'Pekachia'",
				"UPDATE MalcheiYisroel SET father =  'Ramaliya' WHERE FirstName = 'Pekach'",
				"UPDATE MalcheiYisroel SET father =  'Eilla' WHERE num = 19",
				
		};
		for (int index = 0; index < insertInput.length; index++) {
			System.out.println(insertInput[index]);
			mainClass.execute(insertInput[index]);
		}
	}

	private void updateFatherYehuda() {
		// update each table to put in father
		String[] insertInput = new String[] {
				"UPDATE MalcheiYehuda SET father = 'Shlomo'",
				"UPDATE MalcheiYehuda SET father = 'Rechavam' WHERE num = 2",
				"UPDATE MalcheiYehuda SET father = 'Aviah' WHERE FirstName = 'Assa'",
				"UPDATE MalcheiYehuda SET father = 'Assa' WHERE FirstName = 'Yehoshafat' AND num = 4",
				"UPDATE MalcheiYehuda SET father = 'Yehoshafat' WHERE FirstName = 'Yehoram'",
				"UPDATE MalcheiYehuda SET father =  'Yehoram' WHERE FirstName = 'Achazia'",
				"UPDATE MalcheiYehuda SET father =  'Bas Achav' WHERE FirstName = 'Ataliah' AND num = 7",
				"UPDATE MalcheiYehuda SET father =  'Achaziyahu' WHERE FirstName = 'Yehoash' OR num = 8",
				"UPDATE MalcheiYehuda SET father =  'Yehoash' WHERE FirstName = 'Amatzia'",
				"UPDATE MalcheiYehuda SET father =  'Amatzia' WHERE FirstName = 'Uzzia'",
				"UPDATE MalcheiYehuda SET father =  'Uzzia' WHERE FirstName = 'Yosam'",
				"UPDATE MalcheiYehuda SET father =  'Yosam' WHERE FirstName = 'Achaz'",
				"UPDATE MalcheiYehuda SET father =  'Achaz' WHERE FirstName = 'Chizkiyahu'",
				"UPDATE MalcheiYehuda SET father =  'Chizkiyahu' WHERE FirstName = 'Menashe'",
				"UPDATE MalcheiYehuda SET father =  'Menashe' WHERE num = 15",
				"UPDATE MalcheiYehuda SET father =  'Amon' WHERE FirstName = 'Yoshiyahu'",
				"UPDATE MalcheiYehuda SET father =  'Yoshiyahu' WHERE num >= 17", // ('Yehoachaz', 17)",
				"UPDATE MalcheiYehuda SET father =  'Yehoyakim' WHERE num = 19",
				
		};
		for (int index = 0; index < insertInput.length; index++) {
			System.out.println(insertInput[index]);
			mainClass.execute(insertInput[index]);
		}
	}

	private void insertNameNumYisroel() {
		// insert the kings' first names and num
		String[] insertInput = new String[] {
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Yeravam', 1)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Nadav', 2)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Basha', 3)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Eileh', 4)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Zimri', 5)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Omri', 6)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Achav', 7)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Achaziya', 8)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Yehoram', 9)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Yayhu', 10)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Yehoachaz', 11)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Yoash', 12)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Yeravam', 13)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Zecharia', 14)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Menachem', 16)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Shalom', 15)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Pekachia', 17)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Pekach', 18)",
				"INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Hoshea', 19)",

				
		};
		for (int index = 0; index < insertInput.length; index++) {
			if (insertInput[index].equals("INSERT INTO MalcheiYisroel (FirstName, num) VALUES ('Yeravam', 1)")) {
				System.out.println();
			}
			System.out.println(insertInput[index]);
			mainClass.execute(insertInput[index]);
		}
	}

	private void insertNameNumYehuda() {
		// insert the kings' first names and num
		String[] insertInput = new String[] {
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Rechavam', 1)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Aviah', 2)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Assa', 3)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Yehoshafat', 4)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Yehoram', 5)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Achazia', 6)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Ataliah', 7)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Yehoash', 8)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Amatzia', 9)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Uzzia', 10)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Yosam', 11)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Achaz', 12)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Chizkiyahu', 13)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Menashe', 14)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Amon', 15)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Yoshiyahu', 16)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Yehoachaz', 17)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Yehoyakim', 18)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Yehoyachin', 19)",
				"INSERT INTO MalcheiYehuda (FirstName, num) VALUES ('Tzidkiyahu', 20)",
				
		};
		for (int index = 0; index < insertInput.length; index++) {
			System.out.println(insertInput[index]);
			mainClass.execute(insertInput[index]);
		}
	}

	private void createTable () {
		// FirstName VARCHAR (10) NOT NULL
			// father VARCHAR (10) 
			// reign decimal(2,2) DEFAULT 0.00
			// (arbitrary) BOOLEAN DEFAULT true
			// num int UNIQUE
			// PRIMARYKEY (FirstName)
		// first table will be Malchei yehuda
		System.out.println("CREATE TABLE MalcheiYehuda (FirstName varchar(10) NOT NULL, father varchar(10), reign decimal (2,2) DEFAULT 0.0, hadBamos BOOLEAN DEFAULT true, num int UNIQUE, PRIMARY KEY (FirstName))");
		mainClass.execute("CREATE TABLE MalcheiYehuda (FirstName varchar (10) NOT NULL, father varchar (10), reign decimal (2,2) DEFAULT 0.0, hadBamos BOOLEAN DEFAULT true, num int UNIQUE, PRIMARY KEY (FirstName))");
		// second table will be Malchei yisrael
		System.out.println("CREATE TABLE MalcheiYisroel (FirstName VARCHAR (10) NOT NULL, father VARCHAR (10), reign decimal (2,2) DEFAULT 0.0, assassinated BOOLEAN DEFAULT true, num int UNIQUE, PRIMARY KEY (FirstName))");
		mainClass.execute("CREATE TABLE MalcheiYisroel (FirstName VARCHAR (10) NOT NULL, father VARCHAR (10), reign decimal (2,2) DEFAULT 0.0, assassinated BOOLEAN DEFAULT true, num int UNIQUE, PRIMARY KEY (FirstName))");

	}

}
