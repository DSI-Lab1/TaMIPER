package structures;

import java.util.HashSet;

public class ARRoot extends ARNode{
	private String episode;
	private HashSet<Integer> tmlist; //time list
	private HashSet<Integer> qsList; //time list
	private HashSet<Integer> qsSpan; //time list


	public ARRoot(String episode, HashSet<Integer> timeList, HashSet<Integer> qsList, HashSet<Integer> qsSpan){
		this.episode = episode;
		this.tmlist = timeList;
		this.qsList = qsList;
		this.qsSpan = qsSpan;
	}

	public String getEpisode() {
		return episode;
	}

	public void setEpisode(String episode) {
		this.episode = episode;
	}

	public HashSet<Integer> getTmlist() {
		return tmlist;
	}


	public void setTmlist(HashSet<Integer> tmlist) {
		this.tmlist = tmlist;
	}

	public HashSet<Integer> getQsList() {
		return qsList;
	}

	public void setQsList(HashSet<Integer> qsList) {
		this.qsList = qsList;
	}

	public HashSet<Integer> getQsSpan() {
		return qsSpan;
	}

	public void setQsSpan(HashSet<Integer> qsSpan) {
		this.qsSpan = qsSpan;
	}
}
