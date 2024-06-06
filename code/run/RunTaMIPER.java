package run;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import algorithm.TaMIPER;

public class RunTaMIPER {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length == 0) {
			TaMIPER oarSpan;
			try {
				String qs = "10,2";
				int delta = 3;
				int span = 5;
				int min_sup = 8000;
				double conf = 0.154;
				oarSpan = new TaMIPER("./data/alginput/real/kosarak_mining.dat", min_sup, delta, conf, span, 1, 19390, qs);
				oarSpan.runAlg();
				oarSpan.rankRuleByConfidence();
				oarSpan.writeRule2File("./data/experiments/supplement/1000k_0.2" + conf + ".csv");
				oarSpan.printStats("./data/experiments/supplement/performance.csv");

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
			TaMIPER oarSpan;
			try {
				oarSpan = new TaMIPER(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]),
						Double.parseDouble(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]),
						Integer.parseInt(args[6]), args[7]);
				oarSpan.runAlg();
//				oarSpan.rankRuleByConfidence();
				oarSpan.writeRule2File(args[7]);
//				oarSpan.printStats("./data/experiments/performance.csv");
			} catch (NumberFormatException | FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// ogs.writeRule2File(args[7]);
		}
	}
}
