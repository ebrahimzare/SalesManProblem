/*
 * Class -- object definition for the edge of the graph.  
 */
package tsp;

public class Edge {
	/*
	 * source city
	 */
	private String source;
	/*
	 * destination city
	 */
	private String dest;
	/*
	 * tour cost to visit destination from the source city (directly)
	 */
	private Integer cost;
	/*
	 * getter function for source city id
	 */
	public String getSource() {
		return source;
	}
	/*
	 * Setter function for source city
	 */
	public void setSource(String source) {
		this.source = source;
	}
	/*
	 * getter function for destination city id
	 */
	public String getDest() {
		return dest;
	}
	/*
	 * setter function for destination city id
	 */
	public void setDest(String dest) {
		this.dest = dest;
	}
	/*
	 * getter function for tour cost of the edge (tour cost from source to destination)
	 */
	public Integer getCost() {
		return cost;
	}
	/*
	 * setter function for tour cost from source to destination
	 */
	public void setCost(Integer cost) {
		this.cost = cost;
	}
}
