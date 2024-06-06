package structures;

import java.util.HashSet;

public class ARNoRoot extends ARNode {
	private String event;
	private HashSet<Integer> tmlist; // time list
	private Integer distance;//direct distance to the root node
	private AERule rule = null;
	private HashSet<Integer> children = null;
	private boolean dead = false;

	public ARNoRoot(String event, HashSet<Integer> timeList, Integer distance) {
		this.event = event;
		this.tmlist = timeList;
		this.distance = distance;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public HashSet<Integer> getTmlist() {
		return tmlist;
	}

	public void setTmlist(HashSet<Integer> tmlist) {
		this.tmlist = tmlist;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public AERule getRule() {
		return rule;
	}

	public void setRule(AERule rule) {
		this.rule = rule;
	}

	public HashSet<Integer> getChildren() {
		return children;
	}

	public void setChildren(HashSet<Integer> children) {
		this.children = children;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}
}
