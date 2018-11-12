
// tests strings
public class Tester5WithoutBTree {

	private MainClass mainClass;
	private Database theDatabase;
	
	public Tester5WithoutBTree() {
		theDatabase = new Database();
	}
	
	public Long run() {
		mainClass = new MainClass();
		
		System.out.println("-----------------------------------------------------------------------------------------------------------------");
		System.out.println(" RUNNING TESTER5WITHOUTBTREE");
		System.out.println("-----------------------------------------------------------------------------------------------------------------");
		
		String[] theQueries = new String[] {
			
				"CREATE TABLE YCStudent50 (FirstName VARCHAR (5), LastName VARCHAR (7), PRIMARY KEY(LastName))",
				"INSERT INTO YCStudent50 (FirstName) VALUES (MAYBE)",
				"UPDATE YCStudent50 SET FirstName = true",
				"UPDATE YCStudent50 SET LastName = false where FirstName = true",
				"INSERT INTO YCStudent50 (FirstName) VALUES ('Chaim')",
				"INSERT INTO YCStudent50 (LastName) VALUES ('613')",
				"INSERT INTO YCStudent50 (LastName) VALUES (1442)",
				"INSERT INTO YCStudent50 (firstName) VALUES (klasdd)",
				"INSERT INTO YCStudent50 (firstName) VALUES (null)",
				"SELECT FirstName FROM YCStudent50 WHERE LastName = null",
				"INSERT INTO YCStudent50 (LastName) VALUES (qwer613qewr)",
				"INSERT INTO YCStudent50 (LastName) VALUES (null)"
				
				
		};
		
		long startTime = 0;
		long endTime = 0;
		for (int i = 0; i < theQueries.length; i++) {
			if (theQueries[i].equals("SELECT FirstName FROM YCStudent50 WHERE LastName = null")) {
				startTime = System.currentTimeMillis();
			}
			System.out.println("\n\t" + theQueries[i]);
			mainClass.execute(theQueries[i]);
			if (theQueries[i].equals("UPDATE YCStudent11 SET GPA = 3.8 WHERE FirstName = 'Efrayim' OR BannerID = 800004240")) {
				endTime = System.currentTimeMillis();
			}
		}
		return (endTime - startTime);
	}
	
}
