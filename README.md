# Total-Order of events with Vector-Clock

#### Total-Order of events in Distributed Systems using Vector-Clock algorithm.
===============================================================================
#### Usage:
- ##### Script files (Run.bat and run.sh) are used just to run all the processes simulteneously to simulate real-world distributed systems. It contains no-other code than this.
##### •	For Windows:
  - ##### Just double-click 'Run.bat' file or open Command Prompt and navigate to the directory containing Jars (Process0.jar, Process1.jar etc). Open Run.bat file.
  - > ` Run.bat `
##### •	For Linux:
  - ##### Open the Terminal and navigate to the directory containing Jars (Process0.jar, Process1.jar etc). Open run.sh file.
  - > ` ./run.sh `
  #####
  #### Description:
- ##### We use multiple processes to emulate multiple nodes in Distributed Systems and assume that all nodes are initiated to the same vector clock.
######
- ##### After completing a local operation, each process sends its updated vector clock to all other processes and then follows the Vector-Clock algorithm.
######
#####
#### Output:
###### 
![Output](Total-Order-Vector-Clock.PNG?raw=true)
######
