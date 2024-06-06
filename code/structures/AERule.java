package structures;

import java.util.ArrayList;

public class AERule {
	private String name;
	private ArrayList<Integer> spans = new ArrayList<Integer>();
	private Integer support;
	private Double confidence;

	public AERule(String name, ArrayList<Integer> spans, Integer support, Double confidence) {
		this.name = name;
		this.spans = spans;
		this.support = support;
		this.confidence = confidence;
	}

	public AERule(String name, ArrayList<Integer> spans){
		this.name = name;
		this.spans = spans;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Integer> getSpans() {
		return spans;
	}

	public void setSpans(ArrayList<Integer> spans) {
		this.spans = spans;
	}

	public Integer getSupport() {
		return support;
	}

	public void setSupport(Integer support) {
		this.support = support;
	}

	public Double getConfidence() {
		return confidence;
	}

	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}

	// @Override
	// public int compareTo(Object arg0) {
	// // TODO Auto-generated method stub
	// AERule r = (AERule) arg0;
	// String str = this.name+this.spans.toString();
	// return str.compareTo(r.getName()+r.getSpans().toString());
	// }

}
