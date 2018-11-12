
public class Tester1WithBTree {
	private MainClass mainClass;
	private Database theDatabase;
	private BTreeDatabase btreeDatabase;
	
	public Tester1WithBTree() {
		theDatabase = new Database();
		btreeDatabase = new BTreeDatabase();
	}
	
	public long run() {
		mainClass = new MainClass();
		
		System.out.println("---------------------------------------------------------------------------------------------------------------");
		System.out.println("Starting Tester1WithBTree");
		System.out.println("---------------------------------------------------------------------------------------------------------------");
		
		System.out.println("\n\tCreate two tables");
		System.out.println("\n\tCREATE TABLE YCStudent (BannerID int, SSNum int UNIQUE, GPA decimal(1,2) DEFAULT 0.00, Class varchar, FirstName varchar(255), PRIMARY KEY (BannerID))");
		mainClass.execute("CREATE TABLE YCStudent (BannerID int, SSNum int UNIQUE, GPA decimal(1,2) DEFAULT 0.00, Class varchar, FirstName varchar(255), PRIMARY KEY (BannerID))");
		System.out.println("\n\tCREATE TABLE YCStudent1 (BannerID int, SSNum int UNIQUE, FirstName varchar(255) NOT NULL, GPA decimal(1,2) DEFAULT 0.00, CurrentStudent boolean DEFAULT true, PRIMARY KEY (BannerID))");
		mainClass.execute("CREATE TABLE YCStudent1 (BannerID int, SSNum int UNIQUE, FirstName varchar(255) NOT NULL, GPA decimal(1,2) DEFAULT 0.00, CurrentStudent boolean DEFAULT true, PRIMARY KEY (BannerID))");
		System.out.println("\n\tCREATE TABLE YCStudent2 (BannerID int, SSNum int UNIQUE, FirstName varchar(255), LastName varchar(255) NOT NULL, GPA decimal(1,2) DEFAULT 0.00, CurrentStudent boolean DEFAULT true, PRIMARY KEY (BannerID))");
		mainClass.execute("CREATE TABLE YCStudent2 (BannerID int, SSNum int UNIQUE, FirstName varchar(255), LastName varchar(255) NOT NULL, GPA decimal(1,2) DEFAULT 0.00, CurrentStudent boolean DEFAULT true, PRIMARY KEY (BannerID))");
		
		// insert some things into the first table and execute that if the columns are not available, that info is not inserted into the table
		System.out.println("\n\tInsert some things into the first table");
		System.out.println("\n\tINSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Ploni', 800012278, 3.5)");
		mainClass.execute("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Ploni', 800012278, 3.5)"); // true
		System.out.println("\n\t");
		mainClass.execute("INSERT INTO YCStudent (BannerID, FirstName, GPA) VALUES (800054697, 'Avraham', 3.5)"); // true
		System.out.println("\n\tINSERT INTO YCStudent (BannerID, FirstName, GPA) VALUES (800054697, false, 'Avraham')");
		mainClass.execute("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES (800012278, false, 'Avraham')"); // true, but won't have insert anything because wrong types
		System.out.println("\n\tINSERT INTO YCStudent (FirstName, LastName, GPA, Class, BannerID) VALUES ('Yitzchok','Almoni',4.0, 'Senior',80005498765) - FALSE");
		mainClass.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, Class, BannerID) VALUES ('Yitzchok','Almoni',4.0, 'Senior',80005498765)"); // false
		
		// create index
		System.out.println("CREATE INDEX FirstName_Index on YCStudent (FirstName)");
		mainClass.execute("CREATE INDEX FirstName_Index on YCStudent (FirstName)");
		System.out.println("CREATE INDEX GPA_Index on YCStudent (GPA)");
		mainClass.execute("CREATE INDEX GPA_Index on YCStudent (GPA)");
		
			long startTime = System.currentTimeMillis();
		
		// insert some things into the second table and execute that if the columns are not available, that info is not inserted into the table
		System.out.println("\n\tInsert some things into the second table");
		System.out.println("\n\tINSERT INTO YCStudent1 (FirstName, LastName, BannerID) VALUES ('Ploni', 'Almoni', 800012312) - true");
		mainClass.execute("INSERT INTO YCStudent1 (FirstName, LastName, BannerID) VALUES ('Ploni', 'Almoni', 800012312)"); //true
		System.out.println("\n\tINSERT INTO YCStudent1 (BannerID, FirstName, GPA) VALUES (800054697, 'Avraham', 2.5) - true");
		mainClass.execute("INSERT INTO YCStudent1 (BannerID, FirstName, GPA) VALUES (800054697, 'Avraham', 2.5)"); // true
		System.out.println("\n\tINSERT INTO YCStudent1 (FirstName, LastName, GPA, Class, BannerID) VALUES ('Yitzchok','Almoni',4.0, 'Senior',80005498) - FALSE");
		mainClass.execute("INSERT INTO YCStudent1 (FirstName, LastName, GPA, Class, BannerID) VALUES ('Yitzchok','Almoni',4.0, 'Senior',80005498)"); //false
		System.out.println("\n\tINSERT INTO YCStudent1 (BannerID, FirstName, GPA, CurrentStudent) VALUES (800043526, 'Yehoshua', 3.9, true) - true");
		mainClass.execute("INSERT INTO YCStudent1 (BannerID, FirstName, GPA, CurrentStudent) VALUES (800043526, 'Yehoshua', 3.9, true)"); // true
		System.out.println("\n\tINSERT INTO YCStudent1 (BannerID, FirstName, GPA, CurrentStudent) VALUES (800012345, 'Yeshayahu', 3.8, false) - true");
		mainClass.execute("INSERT INTO YCStudent1 (BannerID, FirstName, GPA, CurrentStudent) VALUES (800012345, 'Yeshayahu', 3.8, false)"); // true
		System.out.println("\n\tINSERT INTO YCStudent1 (BannerID, FirstName, GPA, CurrentStudent) VALUES (800087654, 'Yirmiyahu', 3.8, true) - true");
		mainClass.execute("INSERT INTO YCStudent1 (BannerID, FirstName, GPA, CurrentStudent) VALUES (800087654, 'Yirmiyahu', 3.8, true)"); // true
		System.out.println("\n\tINSERT INTO YCStudent1 (BannerID, FirstName, GPA, CurrentStudent) VALUES (800087960, 'Yechezkel', 3.8, true) - true");
		mainClass.execute("INSERT INTO YCStudent1 (BannerID, FirstName, GPA, CurrentStudent) VALUES (800087960, 'Yechezkel', 3.8, true)"); // true
		System.out.println("\n\tINSERT INTO YCStudent1 (BannerID, FirstName, GPA, CurrentStudent) VALUES (800087960, '800087960', 3.8, true) - true");
		mainClass.execute("INSERT INTO YCStudent1 (BannerID, FirstName, GPA, CurrentStudent) VALUES (800087960, '800087960', 3.8, true)"); // true
		
		// Select Lines from first table
		System.out.println("\n\tSELECT FirstName From YCStudent");
		mainClass.execute("SELECT FirstName FROM YCStudent");
		System.out.println("\n\tSELECT FirstName, Class from YCStudent");
		mainClass.execute("SELECT FirstName, Class FROM YCStudent");
		
		// Select Lines from second table
		System.out.println("\n\tSELECT Lines from second table");
		System.out.println("\n\tSELECT FirstName From YCStudent1");
		mainClass.execute("SELECT FirstName FROM YCStudent1");
		System.out.println("SELECT DISTINCT GPA FROM YCStudent1");
		mainClass.execute("SELECT DISTINCT GPA FROM YCStudent1");
		System.out.println("SELECT DISTINCT CurrentStudent FROM YCStudent1");
		mainClass.execute("SELECT DISTINCT CurrentStudent FROM YCStudent1");
		
		// Order first table according to ASC or DESC order
		System.out.println("\n\tSort Columns in first table by FirstName ASC, Credits DESC");
		mainClass.execute("SELECT * FROM YCStudent ORDER BY FirstName ASC, Credits DESC");
		System.out.println("\n\tSort Columns in first table by FirstName DESC");
		mainClass.execute("SELECT * FROM YCStudent ORDER BY FirstName DESC");
		
		// insert some things into the first table and execute that if the columns are not available, that info is not inserted into the table
		System.out.println("\n\tInsert some things into the first table");
		System.out.println("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Moshe', 800012332, 3.0) - true");
		mainClass.execute("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Moshe', 800012332, 3.0)"); // true
		System.out.println("INSERT INTO YCStudent (BannerID, FirstName, GPA) VALUES (800054697, 'Aharon', 2.5) - true");
		mainClass.execute("INSERT INTO YCStudent (BannerID, FirstName, GPA) VALUES (800054697, 'Aharon', 2.5)"); // true
		System.out.println("INSERT INTO YCStudent (FirstName, LastName, GPA, Class, BannerID) VALUES ('Dovid','Hamelech',4.0, 'Senior',80005498765) - false");
		mainClass.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, Class, BannerID) VALUES ('Dovid','Hamelech',4.0, 'Senior',80005498765)"); // false
		System.out.println("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Shlomo', 800013245, 4.0) - true");
		mainClass.execute("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Shlomo', 800013245, 4.0)"); // true
		System.out.println("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Efrayim', 800013247, 3.5) - true");
		mainClass.execute("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Efrayim', 800013247, 3.5)"); // true
		System.out.println("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Menashe', 800013242, 3.0) - true");
		mainClass.execute("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Menashe', 800013242, 3.0)"); // true
		System.out.println("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Yosef', 800013248, 2.5) - true");
		mainClass.execute("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Yosef', 800013248, 2.5)"); // true
		System.out.println("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Shimon', 800013249, 3.5) - true");
		mainClass.execute("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Shimon', 800013249, 3.5)"); // true
		System.out.println("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Shimon', 800013248, 3.0) - true");
		mainClass.execute("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Shimon', 800013248, 3.0)"); // true
		System.out.println("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Shimon', 800013250, 4.0) - true");
		mainClass.execute("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Shimon', 800013250, 4.0)"); // true
		System.out.println("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Yehuda', 800013245, 2.5) - true");
		mainClass.execute("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Yehuda', 800013245, 2.5)"); // true
		System.out.println("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Binyamin', 800013243, 3.5) - true");
		mainClass.execute("INSERT INTO YCStudent (FirstName, BannerID, GPA) VALUES ('Binyamin', 800013243, 3.5)"); // true
		System.out.println("INSERT INTO YCStudent (FirstName, BannerID) VALUES ('Mordechai', 800013248) - true");
		mainClass.execute("INSERT INTO YCStudent (FirstName, BannerID) VALUES ('Mordechai', 800013248)"); // true
		
		// sort
		System.out.println("\n\tSort Columns in first table for a second time");
		System.out.println("\n\tSort * based on GPA DESC, FirstName ASC");
		mainClass.execute("SELECT * FROM YCStudent ORDER BY GPA DESC, FirstName ASC");
		System.out.println("\n\tSort * based on FirstName ASC, GPA DESC");
		mainClass.execute("SELECT * FROM YCStudent ORDER BY FirstName ASC, GPA DESC");
		System.out.println("\n\tSort * based on FirstName ASC");
		mainClass.execute("SELECT * FROM YCStudent ORDER BY FirstName DESC");
		System.out.println("\n\tSort FirstName based on BannerID DESC");
		mainClass.execute("SELECT FirstName FROM YCStudent ORDER BY BannerID DESC");
		System.out.println("\n\tSort * based on FirstName ASC, BannerID DESC");
		mainClass.execute("SELECT * FROM YCStudent ORDER BY FirstName ASC, BannerID DESC");
		System.out.println("\n\tSort * based on BannerID DESC, FirstName ASC");
		mainClass.execute("SELECT * FROM YCStudent ORDER BY BannerID DESC, FirstName ASC");
		System.out.println("\n\tSort GPA, FirstName based on BannerID DESC, FirstName ASC");
		mainClass.execute("SELECT GPA, FirstName FROM YCStudent ORDER BY BannerID DESC, FirstName ASC");
		
		// Update lines in first table
		System.out.println("\n\tUpdate lines in the first table");
		//mainClass.execute("UPDATE YCStudent SET FirstName='Yaakov', Class='Super Senior'"); // true
		System.out.println("UPDATE YCStudent SET GPA=3.0, Class='Super Senior' WHERE BannerID=800012345 - true");
		mainClass.execute("UPDATE YCStudent SET GPA=3.0, Class='Super Senior' WHERE BannerID=800012345"); // true
		System.out.println("UPDATE YCStudent SET GPA = 3.5 WHERE GPA=3.0 - true");
		mainClass.execute("UPDATE YCStudent SET GPA = 3.5 WHERE GPA=3.0"); // true
		System.out.println("UPDATE YCStudent SET GPA = 3.5 WHERE Credits = 60 - false");
		mainClass.execute("UPDATE YCStudent SET GPA = 3.5 WHERE Credits = 60"); // false
		
		// functions
		System.out.println("\n\tFunctions");
		System.out.println("\n\tAverage GPA");
		mainClass.execute("SELECT AVG(GPA) FROM YCStudent");
		System.out.println("\n\tsum of GPA");
		mainClass.execute("SELECT SUM(GPA) FROM YCStudent");
		System.out.println("\n\tcount GPA");
		mainClass.execute("SELECT COUNT(GPA) FROM YCStudent");
		System.out.println("\n\tcount distinct GPA");
		mainClass.execute("SELECT COUNT(DISTINCT GPA) FROM YCStudent");
		System.out.println("\n\tmax GPA");
		mainClass.execute("SELECT MAX(GPA) FROM YCStudent");
		System.out.println("\n\tmax FirstName");
		mainClass.execute("SELECT MAX(FirstName) FROM YCStudent");
		System.out.println("\n\tmax Class");
		mainClass.execute("SELECT MAX(Class) FROM YCStudent");
		System.out.println("\n\tmin GPA");
		mainClass.execute("SELECT MIN(GPA) FROM YCStudent");
		System.out.println("\n\tmin FirstName");
		mainClass.execute("SELECT MIN(FirstName) FROM YCStudent");
		System.out.println("\n\tmin Class");
		mainClass.execute("SELECT MIN(Class) FROM YCStudent");
		System.out.println("\n\tcount GPA and count Class");
		mainClass.execute("SELECT COUNT(GPA), COUNT(Class) FROM YCStudent");
		System.out.println("\n\tcount FirstName, count Class, count GPA, avg GPA, min GPA, max BannerID");
		mainClass.execute("SELECT COUNT(FirstName), COUNT(Class), COUNT(GPA), AVG(GPA), MIN(GPA), MAX(BannerID) FROM YCStudent");
		
		// Distinct
		System.out.println("\n\tselect the distinct GPAs");
		mainClass.execute("SELECT DISTINCT GPA FROM YCStudent");
		System.out.println("\n\tselect the distinct FirstName, GPAs");
		mainClass.execute("SELECT DISTINCT FirstName, GPA FROM YCStudent");
		//mainClass.execute("SELECT DISTINCT column1, column2 FROM table1 WHERE column3='some value' AND (column4='some value OR column4='some other value'");
		System.out.println("\n\tselect the distinct FirstNames");
		mainClass.execute("SELECT DISTINCT FirstName FROM YCStudent");
		
		// Update lines in second table
		System.out.println("\n\tUpdate lines in the second table");
		System.out.println("UPDATE YCStudent1 SET GPA=3.0,LastName='Ben Avraham' WHERE BannerID=80005498 - false");
		mainClass.execute("UPDATE YCStudent1 SET GPA=3.0,LastName='Ben Avraham' WHERE BannerID=80005498"); // false
		System.out.println("UPDATE YCStudent1 SET GPA=3.0,Class='Super Senior' - false");
		mainClass.execute("UPDATE YCStudent1 SET GPA=3.0,Class='Super Senior'"); // false
		
		// delete a line
		System.out.println("\n\tDelete lines from each of the tables");
		System.out.println("DELETE FROM YCStudent WHERE FirstName = 'Ploni' - true");
		mainClass.execute("DELETE FROM YCStudent WHERE FirstName = 'Ploni'"); // true
		System.out.println("DELETE FROM YCStudent1 WHERE FirstName = 'Avraham' OR GPA < 3.0 - true");
		mainClass.execute("DELETE FROM YCStudent1 WHERE FirstName = 'Avraham' OR GPA < 3.0");  //true
		System.out.println("DELETE FROM YCStudent WHERE FirstName = 'Avraham' OR GPA < 3.0 - true");
		mainClass.execute("DELETE FROM YCStudent WHERE FirstName = 'Avraham' OR GPA < 3.0"); //true
		System.out.println("DELETE FROM YCStudent - true");
		mainClass.execute("DELETE FROM YCStudent"); //true
		System.out.println("DELETE FROM YCStudent WHERE LastName = 'Sam' OR jlo < 1.0 - false");
		mainClass.execute("DELETE FROM YCStudent WHERE LastName = 'Sam' OR jlo < 1.0"); //false
		
		System.out.println("---------------------------------------------------------------------------------------------------------------------------------");
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total time: " + totalTime);
		System.out.println("---------------------------------------------------------------------------------------------------------------------------------");

		return totalTime;
	}
}
