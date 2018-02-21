# ContentNetwork

This is a simple implementation of Content-Addressable Networks (CAN). It works using Java and RMI to communicate between nodes. Nodes scan for the bootstrapper to connect to the network. You might need to change the IP address depending on what sever you are running on. right now, this scans 192.168.1.X from 30 to 50 to find the bootstrapper.

# Setting up the network
To run this, you need to start the Sever class on each system that you would like to run on. You can start or remove servers as needed while not running with the GradingServer. you dont need to run any extra commands besides 'java Server' the rmiregistry and stubs/skeleton are generated and run in this code. Once you see 'Server up and running' you are ready to start using commands

# Network commands
Each of the servers can run basic commands to join/leave the network, insert and search for data, and view what data they store.
The commands are:
view: prints out information about the node.
insert: inserts data into the network and then prints out how that data was routed.
search: searches for data stored in the network, returns how to route to that node that stores the data. if data not found returns failure.
join: joins the network. if couldnt find a bootstrapper it becomes the bootstrapper.
leave: leaves the network and redistributes the data stored at this node. if it is the bootstrapper then gives the bootstrapping responsibilities to a neighbor node.

# GradingServer Class
The GradingServer class is very useful if you are trying to test the network (or because this was done for a class grade the network). The Grading sever makes one pass of all valid network connections to find running nodes. Once this scan has run you are only working with the nodes found in that scan. Again you probably shouldnt be using this unless you have some reason to have weird super network powers to make life easier. The GradingServer class connects to every node but is not used in anyway for routing of information or bootstrapping. In fact the network works without this class, and should only be used for testing/grading purposes.

The GradingServer has all the commands mentioned above, but you need to either input what node you want to run the command on or nothing to run that command on all nodes. To specify which node to run on input the node name in node# format. For example, view then node2.

# Future work
Some future things that might be interesting to add:
refresh on the grading server
more commands for the nodes
using lambda expressions to clean up code
