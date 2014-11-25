/*
 * Class -- object definition of the Graph vertex 
 */
package tsp;

import java.util.Vector;

public class Node {
	/*
	 * list of out going edges of this vertex
	 */
	Vector<Edge> edges;
	/*
	 * Vertex id or city name
	 */
	String id;
	/*
	 * getter function -- returns list of vertex edges 
	 */
	public Vector<Edge> getEdges() {
		return edges;
	}
	/*
	 * setter function to assign list of edges of the current vertex
	 */
	public void setEdges(Vector<Edge> edges) {
		this.edges = edges;
	}
	/*
	 * returns city
	 */
	public String getId() {
		return id;
	}
	/*
	 * set vertex name or city name
	 */
	public void setId(String id) {
		this.id = id;
	}
	/*
	 * insert a single outgoing edge of the vertex 
	 */
	public void insertEdge(Edge e){
		edges.add(e);
	}
	/*
	 * initialize the instance of the vertex
	 */
	public Node() {
		super();
		edges=new Vector<Edge>();
		// TODO Auto-generated constructor stub
	}
	/*
	 * function lowestCostPairEdges returns pair of edges with lowest cost 
	 */
	public Vector<Edge> lowestCostPairEdges(){
		//edge e1 -- lowest cost
		Edge e1=new Edge();
		//edge e2 -- lowest cost
		Edge e2=new Edge();
		//assign initial cost infinite foe e1 & e2
		e1.setCost(Integer.MAX_VALUE);
		e2.setCost(Integer.MAX_VALUE);
		//find two lowest cost edge
		for(int i=0;i<edges.size();i++){
			if(e1.getCost()>e2.getCost()){
				if(e1.getCost()>=edges.get(i).getCost()){
					//copy the first lowest cost edge to e1
					e1=edges.get(i);
				}
			}else if(e2.getCost()>e1.getCost()){
				if(e2.getCost()>=edges.get(i).getCost()){
					//copy the second lowest cost edge to e1
					e2=edges.get(i);
				}
			}else{
				if(e1.getCost()>=edges.get(i).getCost()){
					//copy the first lowest cost edge to e1
					e1=edges.get(i);
				}
			}
		}
		//accumulate the pair edges with lowest costs 
		Vector<Edge> l=new Vector<Edge>();
		l.add(e1);
		l.add(e2);
		return l;
	}
	/*
	 * function etEdgeCost -- get the cost of an edge; v is the destination vertex in the edge 
	 */
	public Integer getEdgeCost(String v){
		Integer cost=Integer.MAX_VALUE;
		for(int i=0;i<edges.size();i++){
			//get the edge cost of destination vertex v
			if(edges.get(i).getDest().compareToIgnoreCase(v)==0){
				cost=edges.get(i).getCost();
				break;
			}
		}
		return cost;
	}
	/*
	 * getLowerCostEdgeWoV -- function determines an lowest cost edge of the vertex where the destination vertex (city) is not 'v'
	 * this function requires for a partial tour where one of the in and out are fixed up. 
	 */
	public Edge getLowerCostEdgeWoV(String v){
		Integer cost=Integer.MAX_VALUE;
		Edge e=new Edge();
		for(int i=0;i<edges.size();i++){
			//if destination is 'v' do nothing
			if(edges.get(i).getDest().compareToIgnoreCase(v)==0) continue;
			//otherwise determine the lowest cost edge
			if(edges.get(i).getCost()<cost){
				cost=edges.get(i).getCost();
				e=edges.get(i);
			}
		}
		return e;
	}
}
