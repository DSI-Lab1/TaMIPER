package structures;

import java.io.Serializable;
import java.util.ArrayList;

public class AEO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -114100360309853523L;
	private String name = null;
	private int startT;
	private int endT;
	private ArrayList<Integer> spans = new ArrayList<Integer>();

	/**
	 * Constructor of actionable episode occurrence
	 * 
	 * @param episodeName
	 * @param sT
	 * @param eT
	 */
	public AEO(String episodeName, Integer sT, Integer eT) {
		this.name = episodeName;
		this.startT = sT;
		this.endT = eT;
	}
	
	public AEO(String episodeName, Integer sT, Integer eT, ArrayList<Integer> spans) {
		this.name = episodeName;
		this.startT = sT;
		this.endT = eT;
		this.spans = spans;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStartT() {
		return startT;
	}

	public void setStartT(int startT) {
		this.startT = startT;
	}

	public int getEndT() {
		return endT;
	}

	public void setEndT(int endT) {
		this.endT = endT;
	}

	public ArrayList<Integer> getSpans() {
		return spans;
	}

	public void setSpans(ArrayList<Integer> spans) {
		this.spans = spans;
	}
	// public int compareTo(Object o) {
	// // TODO Auto-generated method stub
	// String another = (String) o;
	// if (another.equals(this.name)) {
	// return 0;
	// }
	// return 1;
	// }

}
