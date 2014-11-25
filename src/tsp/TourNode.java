package tsp;

import java.util.ArrayList;
import java.util.List;

public class TourNode {
	//tour node id (integer values)
	private Integer id;
	//vertex sequence in the current tour (partial or complete)
	private List<String> nodeList;
	//tour cost lower bound 1/2 sum(e(v1)+e(v2))
	private Integer tourCost;
	/*
	 * getter -- tour node id 
	 */
	public Integer getId() {
		return id;
	}
	//setter -- tour node id
	public void setId(Integer id) {
		this.id = id;
	}
	/*
	 * get list or sequence of vertices in the tour
	 */
	public List<String> getNodeList() {
		return nodeList;
	}
	/*
	 * set list or sequence of vertices in the tour
	 */
	public void setNodeList(List<String> parent) {
		this.nodeList = parent;
	}
	/*
	 * get current tour lower bound cost when completed
	 */
	public Integer getTourCost() {
		return tourCost;
	}
	/*
	 * Set tour lower bound cost
	 */
	public void setTourCost(Integer tourCost) {
		this.tourCost = tourCost;
	}
	/*
	 * add a vertex of city in to the tour sequence
	 */
	public void addNextNode(String city){
		nodeList.add(city);
	}
	/*
	 * Initialize tour node instance
	 */
	public TourNode() {
		super();
		// TODO Auto-generated constructor stub
		tourCost=Integer.MAX_VALUE;
		nodeList=new ArrayList<String>();
	}
	/*
	 * get last vertex in the current tour sequence
	 */
	public String getLastNode(){
		return nodeList.get((nodeList.size()-1));
	}
	/*
	 * add vertex 'v' at the end of the current tour sequence if it does not exists
	 */
	public Boolean addLastIfNotExists(String v){
		if(!nodeList.contains(v)){
			nodeList.add(v);
			return true;
		}
		return false;
	}
	/*
	 * Convert tour sequence in to a single string
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String x=new String();
		for(int i=0;i<nodeList.size();i++){
			if(i==0){
				x=x+nodeList.get(i);
			}else{
				x=x+" "+nodeList.get(i);
			}
		}
		return x;
	}
}
