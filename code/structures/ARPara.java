package structures;

import java.util.ArrayList;

public class ARPara {
	private int min_support;
	private int delta;
	private double min_confidence;
	private int epsilon;
	private ArrayList<String> qs;

	public ARPara(int support, int interval, double confidence, int epsilon, ArrayList<String> qs) {
		this.min_support = support;
		this.delta = interval;
		this.min_confidence = confidence;
		this.epsilon = epsilon;
		this.qs = qs;
	}

	public int getMin_support() {
		return min_support;
	}

	public void setMin_support(int min_support) {
		this.min_support = min_support;
	}

	public int getDelta() {
		return delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}

	public double getMin_confidence() {
		return min_confidence;
	}

	public void setMin_confidence(double min_confidence) {
		this.min_confidence = min_confidence;
	}

	public int getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(int epsilon) {
		this.epsilon = epsilon;
	}

	public ArrayList<String> getQs() {
		return qs;
	}

	public void setQs(ArrayList<String> qs) {
		this.qs = qs;
	}

}
