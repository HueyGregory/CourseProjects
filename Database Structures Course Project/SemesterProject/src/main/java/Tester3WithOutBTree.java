
public class Tester3WithOutBTree {
	private MainClass mainClass;
	private BTreeDatabase btreeDatabase;
	private Database theDatabase;
	
	public Tester3WithOutBTree() {
		btreeDatabase = new BTreeDatabase();
		theDatabase = new Database();
	}
	
	public Long run() {
		mainClass = new MainClass();
		System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.println("\n\nRunning Tester3WithOutBTree\n\n");
		System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		String[] theQueries = new String[] {
			// Create the tables
				"CREATE TABLE YCStudent10 (BannerID int, SSNum int UNIQUE, GPA decimal(1,2) DEFAULT 0.00, Class varchar, FirstName varchar(10) NOT NULL, PRIMARY KEY (BannerID))",
				"CREATE TABLE YCStudent11 (BannerID int UNIQUE, Age int, FirstName varchar(255), LastName varchar(255) NOT NULL, GPA decimal(1,2) DEFAULT 0.00, CurrentStudent boolean DEFAULT true, PRIMARY KEY (BannerID))",
				"CREATE TABLE YCStudent12 (BannerID int UNIQUE, Age int, FirstName varchar(255), GPA decimal(1,2) DEFAULT 0.00, CurrentStudent boolean DEFAULT true, PRIMARY KEY (BannerID))",
			// insert first 10 doros until Noach into the first table
				"INSERT INTO YCStudent10 (BannerID, FirstName, GPA) VALUES (800000010, 'Adam', 3.5)",
				"INSERT INTO YCStudent10 (BannerID, FirstName, GPA) VALUES (800000011, 'Chavah', 3.3)",
				"INSERT INTO YCStudent10 (BannerID, FirstName, GPA) VALUES (800000020, 'Kayin', 3.0)",
				"INSERT INTO YCStudent10 (BannerID, FirstName, GPA) VALUES (800000021, 'Hevel', 3.5)",
				"INSERT INTO YCStudent10 (BannerID, FirstName, GPA) VALUES (800000020, 'Sheis', 3.7)",
				"INSERT INTO YCStudent10 (BannerID, FirstName, GPA) VALUES (800000030, 'Enosh', 3.2)",
				"INSERT INTO YCStudent10 (BannerID, FirstName, GPA) VALUES (800000040, 'Keinan', 3.0)",
				"INSERT INTO YCStudent10 (BannerID, FirstName, GPA) VALUES (800000050, 'Mehalalel', 3.2)",
				"INSERT INTO YCStudent10 (BannerID, FirstName, GPA) VALUES (800000060, 'Yered', 3.3)",
				"INSERT INTO YCStudent10 (BannerID, FirstName, GPA) VALUES (800000070, 'Chanoch', 3.8)",
				"INSERT INTO YCStudent10 (BannerID, FirstName, GPA) VALUES (800000080, 'Mesushelach', 3.7)", // the name is too long, so it should not be inserted
				"INSERT INTO YCStudent10 (BannerID, FirstName, GPA) VALUES (800000090, 'Lemech', 3.5)",
				
			// insert second 10 doros until Avraham into the first table	
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA, CurrentStudent) VALUES (800000100, 'Noach', 'Ben Lemech', 3.8, true)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA, CurrentStudent) VALUES (800000101, 'Naama', 'Bas Lemech', 3.8, true)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA, CurrentStudent) VALUES (800000210, 'Sheim', 'Ben Noach', 3.8, true)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA, CurrentStudent) VALUES (800000220, 'Cham', 'Ben Noach', 3.4, false)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA, CurrentStudent) VALUES (800000230, 'Yefes', 'Ben Noach', 3.6, false)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA, CurrentStudent) VALUES (800000300, 'Arpachshad', 'Ben Sheim', 3.6, true)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA, CurrentStudent) VALUES (800000400, 'Shelach', 'Ben Arpachshad', 3.6, true)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA, CurrentStudent) VALUES (800000500, 'Aiver', 'Ben Shelach', 3.5, true)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA, CurrentStudent) VALUES (800000600, 'Peleg', 'Ben Aiver', 3.5, true)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA, CurrentStudent) VALUES (800000700, 'Reu', 'Ben Peleg', 3.4, true)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA, CurrentStudent) VALUES (800000800, 'Serug', 'Ben Reu', 3.4, true)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA, CurrentStudent) VALUES (800000900, 'Nachor', 'Ben Serug', 3.4, true)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA, CurrentStudent) VALUES (800001000, 'Terach', 'Ben Terach', 3.6, true)",
				
				"UPDATE YCStudent11 SET Age = 950 WHERE FirstName = 'Noach'",
				"UPDATE YCStudent11 SET Age = 600 WHERE FirstName = 'Sheim'",
				"UPDATE YCStudent11 SET Age = 438 WHERE FirstName = 'Arpachshad'",
				"UPDATE YCStudent11 SET Age = 433 WHERE FirstName = 'Shelach'",
				"UPDATE YCStudent11 SET Age = 464 WHERE FirstName = 'Aiver'",
				"UPDATE YCStudent11 SET Age = 232 WHERE FirstName = 'Peleg'",
				"UPDATE YCStudent11 SET Age = 239 WHERE FirstName = 'Reu'",
				"UPDATE YCStudent11 SET Age = 230 WHERE FirstName = 'Serug'",
				"UPDATE YCStudent11 SET Age = 148 WHERE FirstName = 'Nachor'",
				"UPDATE YCStudent11 SET Age = 205 WHERE FirstName = 'Terach'",
				"UPDATE YCStudent11 SET GPA = 3.5 WHERE GPA = 3.6",
				
				"DELETE FROM YCStudent11 WHERE FirstName = 'Sheim'",
				
				"SELECT * FROM YCStudent11 ORDER BY GPA ASC, Credits DESC",
				"SELECT * FROM YCStudent11 ORDER BY GPA ASC",
				"SELECT FirstName FROM YCStudent11",
				"SELECT Distinct LastName FROM YCStudent11",
				"SELECT Distinct GPA FROM YCStudent11",
				"SELECT * FROM YCStudent11 ORDER BY LastName ASC, FirstName DESC",
				"SELECT COUNT(FirstName), COUNT(CurrentStudent), COUNT(GPA), AVG(GPA), MIN(GPA), MAX(BannerID) FROM YCStudent11",
				"SELECT SUM(Age), AVG(Age) FROM YCStudent11",
				"SELECT SUM(Credits), AVG(FirstName) FROM YCStudent11",
				
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800001000, 'Avraham', 'Avinu', 4.0)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800001001, 'Sarah', 'Emeinu', 4.0)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800002000, 'Yitzchok', 'Avinu', 4.0)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800002001, 'Rivka', 'Emeinu', 4.0)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800003000, 'Yaakov', 'Avinu', 4.0)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800003001, 'Leah', 'Emeinu', 4.0)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800003002, 'Rochel', 'Emeinu', 4.0)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800003003, 'Bilhaah', 'Emeinu', 4.0)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800003004, 'Zilpah', 'Emeinu', 4.0)",

				"UPDATE YCStudent10 SET GPA=3.0, Class='Super Senior' WHERE BannerID=800000010",
				"UPDATE YCStudent10 SET GPA = 3.5 WHERE GPA=3.0",
				
				"UPDATE YCStudent11 SET CurrentStudent = true WHERE CurrentStudent = false", // messes up Yefes got to look into
				"UPDATE YCStudent11 SET CurrentStudent = false WHERE LastName = 'Ben Noach'",
				"UPDATE YCStudent11 SET GPA = 3.5 WHERE GPA=0.00",
				
				"DELETE FROM YCStudent10 WHERE FirstName = 'Sheis' OR GPA < 3.7",
				"DELETE FROM YCStudent10",
				
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800004110, 'Reuvein', 'Ben Yaakov', 3.8)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800004120, 'Shimon', 'Ben Yaakov', 3.6)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800004130, 'Levi', 'Ben Yaakov', 3.7)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800004140, 'Yehuda', 'Ben Yaakov', 3.9)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800004150, 'Yissachar', 'Ben Yaakov', 3.8)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800004160, 'Zevulun', 'Ben Yaakov', 3.8)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800004310, 'Dan', 'Ben Yaakov', 3.8)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800004320, 'Naftali', 'Ben Yaakov', 3.8)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800004410, 'Gad', 'Ben Yaakov', 3.8)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800004420, 'Asher', 'Ben Yaakov', 3.8)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800004210, 'Yosef', 'Ben Yaakov', 3.9)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800004220, 'Binyamin', 'Ben Yaakov', 3.9)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800004170, 'Dina', 'Bas Yaakov', 3.7)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName, GPA) VALUES (800004370, 'Osnas', 'Bas Dina', 3.9)",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName) VALUES (800042110, 'Menashe', 'Ben Yosef')",
				"INSERT INTO YCStudent11 (BannerID, FirstName, LastName) VALUES (800042120, 'Efrayim', 'Ben Yosef')",
				
				"SELECT * FROM YCStudent11 ORDER BY GPA ASC, LastName DESC",
				"SELECT * FROM YCStudent11",
				"SELECT * FROM YCStudent10",
				"SELECT FirstName, LastName FROM YCStudent11 WHERE GPA > 3.6",
				
				"UPDATE YCStudent11 SET BannerID = 800004211 WHERE FirstName = 'Osnas' AND LastName = 'Bas Dina'",
				"UPDATE YCStudent11 SET BannerID = 800004230, GPA = 3.7 WHERE LastName = 'Ben Yosef'",
				"UPDATE YCStudent11 SET GPA = 3.8 WHERE FirstName = 'Efrayim' OR Credits = 60",
				"UPDATE YCStudent11 SET GPA = 3.0 WHERE FirstName = 'Yaakov' AND LastName = 'Avinu' AND BannerID = 800001234 OR GPA = 4.0",
			//	"UPDATE YCStudent11 SET GPA = 3.0 WHERE FirstName = 'Avraham' AND LastName = 'Avinu' AND (BannerID = 800001234 OR GPA = 4.0)",
				"UPDATE YCStudent11 SET GPA = 3.0 WHERE FirstName = 'Yitzchak' AND (LastName = 'Avinu' OR BannerID = 800001234) AND GPA = 4.0",
				"UPDATE YCStudent11 SET GPA = 3.0 WHERE FirstName = 'Avraham' AND LastName = 'Avinu' AND Credits = 60 OR GPA = 4.0",
				"UPDATE YCStudent11 SET BannerID = 800004240 WHERE FirstName = 'Efrayim'",
				"UPDATE YCStudent11 SET GPA = 3.8 WHERE FirstName = 'Efrayim' OR BannerID = 800004240",
				
				
		};
		long startTime = 0;
		long endTime = 0;
		for (int i = 0; i < theQueries.length; i++) {
			if (theQueries[i].equals("UPDATE YCStudent11 SET Age = 950 WHERE FirstName = 'Noach'")) {
				startTime = System.currentTimeMillis();
			}
			//System.out.println("\n\t" + theQueries[i]);
			mainClass.execute(theQueries[i]);
			if (theQueries[i].equals("UPDATE YCStudent11 SET GPA = 3.8 WHERE FirstName = 'Efrayim' OR BannerID = 800004240")) {
				endTime = System.currentTimeMillis();
			}
		}
		return (endTime - startTime);
	}
}
