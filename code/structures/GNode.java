package structures;

import java.util.HashSet;
import java.util.Set;

public class GNode {
	private String name;
	private Integer Te;
	private boolean dead;
	//private ArrayList<GNode> edgeList = new ArrayList<GNode>();
	private Set<String> lmoSet = new HashSet<String>();
	private boolean[] children = null;
	
	public GNode(String name, int Te, int childSize){
		this.name = name;
		this.Te = Te;
		this.children = new boolean[childSize];
		for(int i = 0; i < childSize; i++){
			this.children[i] = false;
		}
		this.dead = false;
	}
	
	public boolean contains(int offset){
		if(this.children[offset]==true){
			return true;
		}else{
			return false;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTe() {
		return Te;
	}

	public void setTe(Integer te) {
		Te = te;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

//	public ArrayList<GNode> getEdgeList() {
//		return edgeList;
//	}
//
//	public void setEdgeList(ArrayList<GNode> edgeList) {
//		this.edgeList = edgeList;
//	}

	public Set<String> getLmoSet() {
		return lmoSet;
	}

	public void setLmoSet(Set<String> lmoSet) {
		this.lmoSet = lmoSet;
	}

	
	public boolean[] getChildren() {
		return children;
	}

	public void setChildren(boolean[] children) {
		this.children = children;
	}

	
}
