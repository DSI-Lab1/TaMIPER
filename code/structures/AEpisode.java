package structures;

import java.util.ArrayList;

public class AEpisode implements Comparable<Object> {
	private String episode;
	private ArrayList<Integer> spans = new ArrayList<Integer>();
	private Integer support;

	public AEpisode(String name, ArrayList<Integer> spans, Integer support) {
		this.episode = name;
		this.spans = spans;
		this.support = support;
	}

	public AEpisode(String name, ArrayList<Integer> spans) {
		this.episode = name;
		this.spans = spans;
	}

	public String getEpisode() {
		return episode;
	}

	public void setEpisode(String episode) {
		this.episode = episode;
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

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		AEpisode r = (AEpisode) arg0;
		String str = this.episode + this.spans.toString();
		return str.compareTo(r.getEpisode() + r.getSpans().toString());
	}

}
