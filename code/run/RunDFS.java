package run;

import java.io.FileNotFoundException;

import algorithm.MIPERDFS;
import algorithm.MIPERPRU;


public class RunDFS {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length == 0) {
			MIPERDFS darSpan;
			try {
				String qs = "10,2";
				int delta = 3;
				int span = 5;
				int min_sup = 8000;
				double conf = 0.154;
				darSpan = new MIPERDFS("./data/alginput/real/supplement/600k.txt", min_sup, delta, conf, span, 1, 19390, qs);
				darSpan.runAlg();
				darSpan.writeRule2File("./data/experiments/rules-darspan600k" + conf + ".txt");
				darSpan.printStats("./data/experiments/performance.csv");
				System.out.println("qe : " + qs );
				System.out.println("delta : " + delta );
				System.out.println("span : " + span );
				System.out.println("min_sup : " + min_sup );
				System.out.println("conf : " + conf );
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			MIPERDFS darSpan;
			try {
				darSpan = new MIPERDFS(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]),
						Double.parseDouble(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]),
						Integer.parseInt(args[6]), args[7]);
				darSpan.runAlg();
//				barSpan.writeRule2File("./data/experiments/rules-barspan.txt");
				darSpan.printStats("./data/experiments/performance.csv");
			} catch (NumberFormatException | FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// ogs.writeRule2File(args[7]);
		}
	}
}
