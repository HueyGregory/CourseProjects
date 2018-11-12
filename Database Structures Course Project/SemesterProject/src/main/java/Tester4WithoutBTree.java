
// tests booleans
public class Tester4WithoutBTree {

	private MainClass mainClass;
	private Database theDatabase;
	
	public Tester4WithoutBTree() {
		theDatabase = new Database();
	}
	
	public Long run() {
		mainClass = new MainClass();
		
		System.out.println("-----------------------------------------------------------------------------------------------------------------");
		System.out.println(" RUNNING TESTER4WITHOUTBTREE");
		System.out.println("-----------------------------------------------------------------------------------------------------------------");
		
		String[] theQueries = new String[] {
			
				"CREATE TABLE YCStudent40 (CurrentStudent BOOLEAN, PRIMARY KEY(CurrentStudent))",
				"INSERT INTO YCStudent40 (CurrentStudent) VALUES (MAYBE)",
				"UPDATE YCStudent40 SET CurrentStudent = true",
				"UPDATE YCStudent40 SET CurrentStudent = false where CurrentStudent = true",
				"INSERT INTO YCStudent40 (CurrentStudent) VALUES (true)",
				"INSERT INTO YCStudent40 (CurrentStudent) VALUES (False)",
				"INSERT INTO YCStudent40 (CurrentStudent) VALUES (true)",
				"INSERT INTO YCStudent40 (CurrentStudent) VALUES (true)",
				"SELECT CurrentStudent FROM YCStudent40 WHERE CurrentStudent = true",
				
				
		};
		
		long startTime = 0;
		long endTime = 0;
		for (int i = 0; i < theQueries.length; i++) {
			if (theQueries[i].equals("INSERT INTO YCStudent40 (CurrentStudent) VALUES (False)")) {
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
