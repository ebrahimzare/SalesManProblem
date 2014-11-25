package tsp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

public class TestTspBnB {
	private static Boolean exeIntr=false;
	private static long startTime;
	private static long endTime;
	private static Boolean Debug=false;
	//list of all tours accepted and rejected
	private static Vector<String> listOfExecution;
	//store next tour path prefix to stack
	private static Stack<TourNode> nextTour;
	//the complete graph
	private static Map<String,Node> G;
	//list of vertices
	private static Vector<String> vertices;
	//cost of best tour
	private static Integer bestTour;
	//tour sequence of vertices
	private static String bestTourSeq;
	//number of vertices in the graph
	private static Integer noOfNode;
	//number of tour generated
	private static Integer noOfTour;
	//initialize the graph from the file
	private static void initGraph(String fileName){
		try {
			//read file for graph
			Scanner fileContent=new Scanner(new java.io.File(fileName));
			//get the number of vertices
			int n=fileContent.nextInt();
			noOfNode=n;
			fileContent.nextLine();
			//get n-vertices
			for(int i=0;i<n;i++){
				//get vertices (cities)
				String city=fileContent.nextLine().trim();
				//create a vertices
				Node x=new Node();
				//store city into vertex
				x.setId(city);
				//add to graph
				G.put(city,x);
				//add to city list
				vertices.add(city);
			}
			//skip a blank line
			fileContent.nextLine();
			//now read the edges
			while(fileContent.hasNext()){
				String sCity,dCity;
				String line=fileContent.nextLine();
				//remove the double quotes to get the name of the source city
				int head = line.indexOf('"') + 1;
				int tail = line.indexOf('"', head);
				//set the source city
				sCity = line.substring(head, tail);

				//remove the double quotes to get the name of the destination city 
				head = line.indexOf('"', tail+1) + 1;
				tail = line.indexOf('"', head);
				//set the destination city
				dCity = line.substring(head, tail);
				if(sCity.compareToIgnoreCase(dCity)==0) continue;
				//get the integer at the end of the line which is the distance between two city
				Integer distance = Integer.parseInt( line.substring(tail+1).trim() );
				//create edge of the vertex sorce between source and destination cities
				Edge e=new Edge();
				//source city
				e.setSource(sCity);
				//destination city
				e.setDest(dCity);
				//cost from source to destination city
				e.setCost(distance);
				//add to vertex of source city
				G.get(sCity).insertEdge(e);
			}
			fileContent.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//display the graph
	public static void displayGraph(){
		//total vertices
		System.out.println(G.size());
		//vertices
		for(String key:G.keySet()){
			System.out.println(key);
		}
		//blank line
		System.out.println();
		//all edges of each of the vertex
		for(String key:G.keySet()){
			Node v=G.get(key);
			Vector<Edge> es=v.getEdges();
			for(int i=0;i<es.size();i++){
				System.out.println(es.get(i).getSource()+" "+es.get(i).getDest()+" "+es.get(i).getCost());
			}
		}
	}
	/*
	 * function calculateLowerBound() -- calculated lower cost bound for a complete or a partial tour
	 * tNode -- current status of the tour
	 */
	public static void calculateLowerBound(TourNode tNode){
		Integer cost=0;
		//list of the destination vertices from vertex (city) tNode
		List<String> list=tNode.getNodeList();
		//number of outgoing edges of vertex tNode 
		Integer lSize=list.size();
		Integer cost_cal=0;
		//if the tour is a complete tour
		if(lSize==vertices.size()){
			//back to the source city (vertex)
			String lastNode=list.get(0);
			//add start vertex to the end
			tNode.addNextNode(lastNode);
			//get new tour node sequence
			list=tNode.getNodeList();
			//calculated the cost of the tour (complete)
			for(int j=0;j<list.size()-1;j++){
				//get source vertex (city) from the tour sequence of vertices
				String src=list.get(j);
				//get destination vertex (city) from the tour sequence of vertices
				String dest=list.get(j+1);
				//get source vertex (city) from the graph
				Node node=G.get(src);
				//get edges of source city (vertex) from the list of edges of the source vertex
				Vector<Edge> edges=node.getEdges();
				//loop through all edges, match destination vertex 'dest' and get the cost for the tour from src to dest city 
				for(int k=0;k<edges.size();k++){
					if(edges.get(k).getDest().compareToIgnoreCase(dest)==0){
						//found the edge of source to dest city; add the cost  
						cost+=edges.get(k).getCost();
						break;
					}
				}
			}
			//calculated total cost
			cost_cal=cost;
			//add the total cost to the tour (complete) vertex
			tNode.setTourCost(cost_cal);
			/*
			 * Compare cost with previously calculated best tour cost; If current tour cost is better then store the tour 
			 * sequence and modify best tour cost; the tour is accepted; otherwise reject the tour. Our previous tour was 
			 * better.
			 */
			if(bestTour>cost_cal){
				bestTour=cost_cal;
				//store the sequence of current tour, so far it is the best tour
				bestTourSeq=tNode.toString();
				noOfTour++;
				//accept as a best tour
				if(Debug==true) listOfExecution.add("Tour "+tNode.toString()+" Accepted : "+tNode.getTourCost());
				//System.out.println("Tour "+tNode.toString()+" Accepted : "+tNode.getTourCost());
			}else if(bestTour<cost_cal){
				noOfTour++;
				//reject the tour, current tour is worst than the previous
				if(Debug==true) listOfExecution.add("Tour "+tNode.toString()+" Rejected : "+tNode.getTourCost());
				//System.out.println("Tour "+tNode.toString()+" Rejected : "+tNode.getTourCost());
			}else{
				noOfTour++;
				//current tour is as better as the previous tour; accept the tour
				if(Debug==true)  listOfExecution.add("Tour "+tNode.toString()+" Accepted : "+tNode.getTourCost());
				//System.out.println("Tour "+tNode.toString()+" Accepted : "+tNode.getTourCost());
			}
		}else{
			/*
			 * If current tour is not a complete tour; Reject, if the lower cost bound of the final tour is worst than the current best tour 
			 */
			for(int i=0;i<vertices.size();i++){
				String v=vertices.get(i);
				Integer index=list.indexOf(v);
				//If it is a start or intermediate state of the tour -- calculate the lower bound cost of the final tour
				if(index>-1){
					//if the current vertex (city) is the start city (vertex)
					if(index-1<0 && index+1>=list.size()){
						//determine lower bound cost for in and out edges (hamiltonian cycle constraint), no node should not be visited twice of more  
						Vector<Edge> e_c=G.get(v).lowestCostPairEdges();
						cost+=e_c.get(0).getCost();
						cost+=e_c.get(1).getCost();
					}else if(index-1<0 && index+1<list.size()){
						//current node is the stating node and has a out going edge to another vertex (exist next to it)
						String v1=list.get(index+1);
						cost+=G.get(v).getEdgeCost(v1);
						//add lowest cost of an incoming edge
						cost+=G.get(v).getLowerCostEdgeWoV(v1).getCost();
					}else if(index-1>=0 && index+1>=list.size()){
						//incoming edge exists in the tour. add the cost
						String v1=list.get(index-1);
						cost+=G.get(v).getEdgeCost(v1);
						//add the minimum cost of the outgoing edge 
						cost+=G.get(v).getLowerCostEdgeWoV(v1).getCost();
					}else{
						//both incoming and outgoing edge exists in the tour. Add the cost to calculate probable tour cost 
						cost+=G.get(v).getEdgeCost(list.get(index-1));
						cost+=G.get(v).getEdgeCost(list.get(index+1));
					}
				}else{
					//if the vertex v is not included in the tour. Determine minimum incoming and outgoing edge costs  
					Vector<Edge> e_c=G.get(v).lowestCostPairEdges();
					//add with total cost
					cost+=e_c.get(0).getCost();
					//add with total cost
					cost+=e_c.get(1).getCost();
				}
			}
			//lower bound cost for the current tour to be completed 
			cost_cal=(int) Math.ceil(cost/2);
			//update lower bound cost of the current tour to the end
			tNode.setTourCost(cost_cal);
		}
		//System.out.println("Cost : "+cost_cal);
	}
	public static void bestTour(){
		//initial best tour cost is infinite
		//bestTour=Integer.MAX_VALUE;
		bestTourSeq=new String();
		//get initial node from the vertices list
		String key=vertices.get(0);
		//create a tour node
		TourNode init_tn=new TourNode();
		//insert next node in the list
		init_tn.addNextNode(key);
		//calculate lower bound
		calculateLowerBound(init_tn);
		//lower bound tour cost
		listOfExecution.add("Lower bound tour cost : "+init_tn.getTourCost());
		//System.out.println("Lower bound tour cost : "+init_tn.getTourCost());
		listOfExecution.add("------------------------------------------------------");
		//System.out.println("------------------------------------------------------");
		//put it into next tour
		nextTour.push(init_tn);
		//Start tour from the initial node
		while(!nextTour.empty()){
			endTime = System.currentTimeMillis();
			if((endTime-startTime)>60000){
				exeIntr=true;
				System.out.println("Execution time out ...");
				break;
			}
			//get next node for the tour
			TourNode nextNode=nextTour.pop();
			//get last node in the tour
			String v=nextNode.getLastNode();
			//expand the tour with the last vertex and store the children for future tour evaluation 
			Node lastNode=G.get(v);
			//get edges of the last vertex or node
			Vector<Edge> l=lastNode.getEdges();
			//System.out.println("No of edges of "+v+": "+l.size());
			for(int i=0;i<l.size();i++){
				//build new tour
				TourNode nTour=new TourNode();
				//add the incomplete tour sequence from the parent tour node. 
				for(int j=0;j<nextNode.getNodeList().size();j++){
					nTour.addNextNode(nextNode.getNodeList().get(j));
				}
				//add another vertex (not added and adjacent to the last vertex) in the tour
				if(nTour.addLastIfNotExists(l.get(i).getDest())){
					//calculate lower bound of the tour cost
					calculateLowerBound(nTour);
					//System.out.println("Tour Probable Cost "+nTour.getTourCost()+" Best tour cost "+bestTour);
					//if the current tour sequence is complete or intermediate
					if(nTour.getNodeList().size()<=vertices.size()){
						/*compare lower bound tour cost with the current best tour cost. If the lower bound tour cost is less or equal, 
						 * store the tour for future evaluation  
						 */
						if(nTour.getTourCost()<=bestTour){
							//System.out.println("pushes "+nTour.toString());
							nextTour.push(nTour);
						}else{
							/*
							 * otherwise discard this tour -- will never be the optimal tour
							 */
							Integer nVr=(vertices.size()+1)-nTour.getNodeList().size();
							String star=new String();
							for(int t=0;t<nVr;t++){
								star=star+" "+"*";
							}
							noOfTour++;
							//reject the partial or complete tour
							if(Debug==true) listOfExecution.add("Tour "+nTour.toString()+star+" Rejected : "+nTour.getTourCost());
							//System.out.println("Tour "+nTour.toString()+star+" Rejected : "+nTour.getTourCost());
						}
					}
				}
			}
			//System.out.println(nextTour.size());
			//add edge from key to the next node in the Graph G
		}
	}
	/*
	 * function factorial is used to determine the amount of available tour.
	 * The amount of tour will be used to determine the percentage of saving from the naive tsp algorithm 
	 */
	private static BigInteger factorial(Integer num){
		if(num==0|num==1) return BigInteger.valueOf(1);
		BigInteger x=BigInteger.valueOf(1);
		for(int i=1;i<=num;i++){
			x=x.multiply(BigInteger.valueOf(i));
		}
		return x;

	}
	/*
	 * createTestSpace() -- function create test files in 5 interval starting from 5
	 */
	public static void createTestSpace(int range){
		//file base
		String fileBase="testBnB";
		//cities
		String[] graphNodes={"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
		//cost range
		Integer max_cost=100;
		//create testspace for tsp
		for(int m=0;m<10;m++){
			for(int n=5;n<range;n+=5){
				Vector<String> fileContent=new Vector<String>();
				//number of cities (vertices
				fileContent.add(""+n);
				//generate vertices (cities)
				//for vertex more than 26 add numbers to the end of the name (j-26)
				for(int j=0;j<n;j++){
					String nid;
					int s=0;
					int d=0;
					if(j>25){
						s=j%26;
						d=j-s;
						nid=graphNodes[s]+d;
					}else{
						nid=graphNodes[j];
					}
					fileContent.add(nid);
				}
				//blank line
				fileContent.add("");
				//generate edges with random cost; 0 indicates source and destination is same -- no cost 
				for(int k=0;k<n;k++){
					for(int l=k;l<n;l++){
						if(l==k){
							//for vertex more than 26 add numbers to the end of the name (j-26)
							String nid;
							int s=0;
							int d=0;
							if(l>25){
								s=l%26;
								d=l-s;
								nid=graphNodes[s]+d;
							}else{
								nid=graphNodes[l];
							}
							fileContent.add("\""+nid+"\""+" "+"\""+nid+"\""+" "+0);
						}else{
							Random ran = new Random();
							Integer cost = ran.nextInt(max_cost) + 1;
							//for vertex more than 26 add numbers to the end of the name (j-26)
							String nidl;
							int s=0;int d=0;
							if(l>25){
								s=l%26;	d=l-s;
								nidl=graphNodes[s]+d;
							}else{
								nidl=graphNodes[l];
							}
							//for vertex more than 26 add numbers to the end of the name (j-26)
							String nidk;
							s=0;d=0;
							if(k>25){
								s=k%26;d=k-s;
								nidk=graphNodes[s]+d;
							}else{
								nidk=graphNodes[k];
							}
							fileContent.add("\""+nidk+"\""+" "+"\""+nidl+"\""+" "+cost);
							fileContent.add("\""+nidl+"\""+" "+"\""+nidk+"\""+" "+cost);
						}
					}
				}
				try{
					//write to file -- vertices and edges with direct tour costs
					File file=new File(fileBase+String.format("%02d",m)+String.format("%03d", n)+".txt");
					BufferedWriter output = new BufferedWriter(new FileWriter(file));
					for(int i=0;i<fileContent.size();i++){
						output.write(fileContent.get(i));
						output.newLine();
					}
					output.close();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void tsp(String fileName){
		// TODO Auto-generated method stub
		//System.exit(0);
		listOfExecution=new Vector<String>();
		//initialize best tour cost to infinite
		bestTour=Integer.MAX_VALUE;
		//list of all vertices
		vertices=new Vector<String>();
		//the graph
		G=new HashMap<String,Node>();
		//initialize the graph
		initGraph(fileName);
		//Initialize tour stack
		nextTour=new Stack<TourNode>();
		//displayGraph();
		//number of complete and partial tour generated from the branch and bound method
		noOfTour=0;
		//start record the time 
		startTime = System.currentTimeMillis();
		//banch and bound determine best tour
		exeIntr=false;
		bestTour();
		//end record time
		endTime = System.currentTimeMillis();
		if(Debug==true){ 
			for(int i=0;i<listOfExecution.size();i++){
				System.out.println(listOfExecution.get(i));
			}
		}
		System.out.println(listOfExecution.get(0));
		//System.out.println(listOfExecution.get(1));
		//System.out.println("--------------------------------------------------------------");
		if(exeIntr==false){
			System.out.println("Best Tour : "+bestTourSeq+", Tour Cost : "+bestTour);
		}else{
			System.out.println("Before Timeout Tour : "+bestTourSeq+", Tour Cost : "+bestTour);
		}
		//get the naive complete tour
		BigInteger factorial=factorial(noOfNode);
		BigInteger sub=factorial.subtract(BigInteger.valueOf(noOfTour));
		//percentage of saving from the naive compare to branch and bound TSP algorithm
		BigInteger perc=sub.multiply(BigInteger.valueOf(100)).divide(factorial);
		System.out.println("Saving "+perc+"%");
		//print the execution time for the test data in branch and bound algorithm
		System.out.println("Execution Time " + (endTime - startTime) + " milliseconds");
		System.out.println("--------------------------------------------------------------");
	}
	/*
	 * Naive algorithm
	 * Generates all (permutation) complete tour gradually and determine the best tour
	 * 1. Start from root node. 
	 * 2. Expand and create partial tours save it for process
	 * 3. Pop a partial tour and expand further (add a node which is not there)
	 * 4. Now, if tour is complete calculate cost; compare with the previous cost
	 * 5. otherwise store the partial tour
	 * 6. repeat 2 to 5 while store is not empty
	 */
	public static void tspNaive(String fileName){
		//System.exit(0);
		listOfExecution=new Vector<String>();
		//initialize best tour cost to infinite
		bestTour=Integer.MAX_VALUE;
		//list of all vertices
		vertices=new Vector<String>();
		//the graph
		G=new HashMap<String,Node>();
		//initialize the graph
		initGraph(fileName);
		//Initialize tour stack
		nextTour=new Stack<TourNode>();
		//start node 
		String key=vertices.get(0);
		////create the tour node
		TourNode init_tn=new TourNode();
		//insert next node in the list
		init_tn.addNextNode(key);
		//put into tour stack
		nextTour.push(init_tn);
		//start record the time 
		startTime = System.currentTimeMillis();
		//banch and bound determine best tour
		exeIntr=false;
		while(!nextTour.empty()){
			endTime = System.currentTimeMillis();
			if((endTime-startTime)>60000){
				exeIntr=true;
				System.out.println("Execution time out ...");
				break;
			}
			TourNode tn=nextTour.pop();
			//is complete
			if(tn.getNodeList().size()==vertices.size()){
				//complete tour
				tn.addNextNode(tn.getNodeList().get(0));
				Integer cost=getNaiveTourCost(tn.getNodeList());
				if(cost<bestTour){
					bestTour=cost;
					bestTourSeq=tn.getNodeList().toString();
				}
			}else{
				for(int i=0;i<vertices.size();i++){
					if(!tn.getNodeList().contains(vertices.get(i))){
						TourNode ntour=new TourNode();
						for(int j=0;j<tn.getNodeList().size();j++){
							ntour.addNextNode(tn.getNodeList().get(j));
						}
						ntour.addLastIfNotExists(vertices.get(i));
						nextTour.push(ntour);
					}
				}
			}
		}
		//end record time
		endTime = System.currentTimeMillis();
		if(Debug==true){ 
			for(int i=0;i<listOfExecution.size();i++){
				System.out.println(listOfExecution.get(i));
			}
		}
		//System.out.println(listOfExecution.get(0));
		//System.out.println(listOfExecution.get(1));
		//System.out.println("--------------------------------------------------------------");
		if(exeIntr==false){
			System.out.println("Best Tour : "+bestTourSeq+", Tour Cost : "+bestTour);
		}else{
			System.out.println("Before Timeout Tour : "+bestTourSeq+", Tour Cost : "+bestTour);
		}
		//print the execution time for the test data in branch and bound algorithm
		System.out.println("Execution Time " + (endTime - startTime) + " milliseconds");
		System.out.println("--------------------------------------------------------------");
	}
	private static Integer getNaiveTourCost(List<String> nodeList) {
		// TODO Auto-generated method stub
		Integer cost=0;
		for(int i=0;i<nodeList.size()-1;i++){
			cost+=G.get(nodeList.get(i)).getEdgeCost(nodeList.get(i+1));
		}
		return cost;
	}
	public static void main(String[] args) {
		//test start number of cities
		int ts=5;
		//test end number of cities
		int tend=15;
		//test space range
		int range=0;
		//iteration
		int iteration=1;
		//create test space
		if(args.length>0){
			for(int t=0;t<args.length;t++){
				if(args[t].compareToIgnoreCase("--testspace")==0 && args.length>(t+1)){
					range=Integer.parseInt(args[t+1]);
					createTestSpace(range);
				}else if(args[t].compareToIgnoreCase("--testspace")==0 && args.length<=(t+1)){
					System.out.println("Usage: --testspace range-value");
					System.exit(0);
				}
				if(args[t].compareToIgnoreCase("--it")==0 && args.length>(t+1)){
					iteration=Integer.parseInt(args[t+1]);
				}else if(args[t].compareToIgnoreCase("--testspace")==0 && args.length<=(t+1)){
					System.out.println("Usage: --it <number of iteration>");
					System.exit(0);
				}
				if(args[t].compareToIgnoreCase("--start")==0 && args.length>(t+1)){
					ts=Integer.parseInt(args[t+1]);
				}else if(args[t].compareToIgnoreCase("--start")==0 && args.length<=(t+1)){
					System.out.println("Usage: --start value");
					System.exit(0);
				}
				if(args[t].compareToIgnoreCase("--end")==0 && args.length>(t+1)){
					tend=Integer.parseInt(args[t+1]);
				}else if(args[t].compareToIgnoreCase("--end")==0 && args.length<=(t+1)){
					System.out.println("Usage: --end value");
					System.exit(0);
				}
				//debug flag on will print all the accept and rejected complete/tour detail
				if(args[t].compareToIgnoreCase("--debug")==0){
					Debug=true;
				}
			}
		}
		//run the tsp on the test space
		for(int j=ts;j<=tend;j+=5){
			for(int i=0;i<iteration;i++){
                                System.out.println("Iteration "+i);
				System.out.println("Executing Naive TSP for test on : "+j+" cities, file name "+"testBnB"+String.format("%02d",i)+String.format("%03d",j)+".txt");
				String fileName="testBnB"+String.format("%02d",i)+String.format("%03d",j)+".txt";
				tspNaive(fileName);
				System.out.println("Executing Branch and Bound TSP for test on : "+j+" cities, file name "+"testBnB"+String.format("%02d",i)+String.format("%03d",j)+".txt");
				fileName="testBnB"+String.format("%02d",i)+String.format("%03d",j)+".txt";
				tsp(fileName);
			}
                        System.out.println("==========================================================================================");
		}
	}
}
