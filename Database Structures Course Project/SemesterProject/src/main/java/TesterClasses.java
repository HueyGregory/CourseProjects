
public class TesterClasses {

	private BTreeDatabase btreeDatabase = new BTreeDatabase();
	private Database database = new Database();
	
	public TesterClasses() {
		btreeDatabase = new BTreeDatabase();
		database = new Database();
	}
	
	public void run() {
		// run many times
		Long[][] times1 = new Long[10][2];
		Long[][] times2 = new Long[10][2];
		Long[][] times3 = new Long[10][2];
		Long[][] times4 = new Long[10][2];
		Long[][] times5 = new Long[10][2];
		for (int i = 0; i < 1; i++) {
			database.reset();
			btreeDatabase.resetDatabase();
			long startTime = System.currentTimeMillis();
			long startTime10 = System.currentTimeMillis();
			Tester1WithoutBTree tester1WithoutBTree = new Tester1WithoutBTree();
			times1[i][0] = tester1WithoutBTree.run();
			long endTime10 = System.currentTimeMillis();
			long totalTime10 = endTime10 - startTime10;
			System.out.println("total amount of time: " + totalTime10 + " milliseconds.");
			
			database.reset();
			btreeDatabase.resetDatabase();
			
			long startTime11 = System.currentTimeMillis();
			Tester1WithBTree tester1WithBTree = new Tester1WithBTree();
			times1[i][1] = tester1WithBTree.run();
			long endTime11 = System.currentTimeMillis();
			long totalTime11 = endTime11 - startTime11;
			System.out.println("total amount of time: " + totalTime11 + " milliseconds.");
			
			database.reset();
			btreeDatabase.resetDatabase();
			
			long startTime20 = System.currentTimeMillis();
			Tester2WithBTree tester2WithBTree = new Tester2WithBTree();
			times2[i][0] = tester2WithBTree.run();
			long endTime20 = System.currentTimeMillis();
			long totalTime20 = endTime20 - startTime20;
			System.out.println("total amount of time: " + totalTime20 + " milliseconds.");
			
			database.reset();
			btreeDatabase.resetDatabase();
			
			long startTime21 = System.currentTimeMillis();
			Tester2WithoutBTree tester2WithoutBTree = new Tester2WithoutBTree();
			times2[i][1] = tester2WithoutBTree.run();
			long endTime21 = System.currentTimeMillis();
			long totalTime21 = endTime21 - startTime21;
			System.out.println("total amount of time: " + totalTime21 + " milliseconds.");
			
			database.reset();
			btreeDatabase.resetDatabase();
			
			
			long startTime30 = System.currentTimeMillis();
			Tester3WithBTree tester3WithBTree = new Tester3WithBTree();
			times3[i][0] = tester3WithBTree.run();
			long endTime30 = System.currentTimeMillis();
			long totalTime30 = endTime30 - startTime30;
			System.out.println("total amount of time: " + totalTime30 + " milliseconds.");
			
			database.reset();
			btreeDatabase.resetDatabase();
			
			long startTime31 = System.currentTimeMillis();
			Tester3WithOutBTree tester3WithOutBTree = new Tester3WithOutBTree();
			times3[i][1] = tester3WithOutBTree.run();
			long endTime31 = System.currentTimeMillis();
			long totalTime31 = endTime31 - startTime31;
			System.out.println("total amount of time: " + totalTime31 + " milliseconds.");
			
			database.reset();
			btreeDatabase.resetDatabase();
			
			long startTime40 = System.currentTimeMillis();
			Tester4WithoutBTree tester4WithOutBTree = new Tester4WithoutBTree();
			times4[i][1] = tester4WithOutBTree.run();
			long endTime40 = System.currentTimeMillis();
			long totalTime40 = endTime40 - startTime40;
			System.out.println("total amount of time: " + totalTime40 + " milliseconds.");
			
			database.reset();
			btreeDatabase.resetDatabase();
			
			long startTime50 = System.currentTimeMillis();
			Tester5WithoutBTree tester5WithOutBTree = new Tester5WithoutBTree();
			times5[i][1] = tester5WithOutBTree.run();
			long endTime50 = System.currentTimeMillis();
			long totalTime50 = endTime50 - startTime50;
			System.out.println("total amount of time: " + totalTime50 + " milliseconds.");
			
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("total amount of time: " + totalTime + " milliseconds.");
			database.reset();
			btreeDatabase.resetDatabase();
		}
		
		int numGreater1 = 0;
		for(int i = 0; i < times1.length; i++) {
			System.out.println(times1[i][0] + "|" + times1[i][1]);
			if (times1[i][0] > times1[i][1]) {
				numGreater1++;
			}
		}
		
		int numGreater2 = 0;
		for(int i = 0; i < times2.length; i++) {
			System.out.println(times2[i][0] + "|" + times2[i][1]);
			if (times2[i][0] > times2[i][1]) {
				numGreater2++;
			}
		}
		
		int numGreater3 = 0;
		for(int i = 0; i < times3.length; i++) {
			System.out.println(times3[i][0] + "|" + times3[i][1]);
			if (times3[i][0] > times3[i][1]) {
				numGreater3++;
			}
		}
		
//		int numGreater4 = 0;
//		for(int i = 0; i < times4.length; i++) {
//			System.out.println(times4[i][0] + "|" + times4[i][1]);
//			if (times4[i][0] > times4[i][1]) {
//				numGreater4++;
//			}
//		}
//		int numGreater5 = 0;
//		for(int i = 0; i < times5.length; i++) {
//			System.out.println(times5[i][0] + "|" + times5[i][1]);
//			if (times5[i][0] > times5[i][1]) {
//				numGreater5++;
//			}
//		}
		
		System.out.println("With the BTree, test #1 was faster: " + numGreater1 + " times.");
		System.out.println("With the BTree, test #2 was faster: " + numGreater2 + " times.");
		System.out.println("With the BTree, test #3 was faster: " + numGreater3 + " times.");
//		System.out.println("With the BTree, test #4 was faster: " + numGreater4 + " times.");
//		System.out.println("With the BTree, test #5 was faster: " + numGreater5 + " times.");
		
//		smallTests();
	}
	
	private static void smallTests() {
		String str1 = "1234";
		String str2 = str1.toLowerCase();
		Boolean isIt = str1.toLowerCase().contains(str2);
		
		System.out.println(isIt);
	}
}
