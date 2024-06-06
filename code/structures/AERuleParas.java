package structures;

public class AERuleParas {
	private int min_support;
	private int delta;
	private double min_confidence;
	private int span;

	public AERuleParas(int support, int interval, double confidence, int span) {
		this.min_support = support;
		this.delta = interval;
		this.min_confidence = confidence;
		this.span = span;
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

	public int getSpan() {
		return span;
	}

	public void setSpan(int span) {
		this.span = span;
	}

}
