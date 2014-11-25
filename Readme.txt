The program contains 'naive' and 'branch and bound' algorithms for TSP problem. 

Maximum Execution time : 1min

1. Full execution command and option
java tsp.TestTspBnB --testspace <range>  --start <value1> --end <value2> --it <no_of_iteration> --debug

--testspace <range> : create 10 test files each from 5 to range with 5 interval.

--start  <value1> : start test with number of nodes (must be multiple of 5)

--end <value2> : end of test with number of nodes (must be multiple of 5)

--it <no_of_iteration> : default iteration is 1; if you want more than 1 iteration choose the value between 1 to 10; Maximum limit is 10

Examples:
2. To execute -- if we have test for 5 to 15 nodes graph; (default) 

java tsp.TestTspBnB

3. To generate test space or test files; It will generates 10 files for each graph with nodes 5 to range_value skip 5 
java tsp.TestTspBnB --testspace <range_value> (e.g. 15)

4. To execute with a set of test data (start with 5 nodes to 15 nodes with skip 5)
java tsp.TestTspBnB --start 5 --end 15

5. Example:

java tsp.TestTspBnB --testspace 300  --start 100 --end 200 --it 3

(a) Create 300 x 10 test files for 5 to 300 node graph.

(b) Start the execution from 100 node graph and end with 200 node graph

(c) Iterate the process 3 times.  
