import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * @author: Vaibhav Murkute
 * Project: Vector Clock for Total-Order Multicast 
 * date: 11/15/2018
 */

public class LogicalTimeKeeper {
	public static final String process_id = "P3";
	public static final int pid = 3;
	private static InetAddress SERVER_HOSTADD = InetAddress.getLoopbackAddress();
	public static int[] PROCESS_PORTLIST = new int[]{4444,5555,6666,7777};
	public static String[] PROCESS_EVENTLIST = new String[]{"a", "b", "c", "d"};
	private static final int NUM_PROCESSES = PROCESS_PORTLIST.length;
	public static volatile ArrayList<ProcessEvent> event_buffer = new ArrayList<>();
	public static int[] time_vector = new int[PROCESS_PORTLIST.length];
	private static int time_matched = 0;
	private static ServerSocket server_socket;
	public static Thread connectionThread = null;
	public static Thread orderThread = null;
	

	public LogicalTimeKeeper() {
		time_vector = new int[PROCESS_PORTLIST.length];
		Arrays.fill(time_vector, 0);
	}
	
	
	public void init(){
		System.out.println("Process: "+LogicalTimeKeeper.process_id);
		// Thread 01: manageConnections
		connectionThread = (new Thread(){
			@Override
			public void run(){
				manageConnections();
				return;
			}
		});

		connectionThread.start();

		StringBuilder initialTimeString = new StringBuilder("[");
		for(int i : LogicalTimeKeeper.time_vector){
			initialTimeString.append(String.valueOf(i));
			initialTimeString.append(", ");
		}
		int lastComma = initialTimeString.lastIndexOf(",");
		initialTimeString.replace(lastComma, lastComma+1, " ]");
		System.out.println("Initial Vector-Time: "+initialTimeString.toString());
		
		/*try {
			// Added this delay to wait till all other processes are in the listening state
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		
		// Multicast event to everyone
		sendEvent();

	}

	public void manageConnections(){
		int server_port = PROCESS_PORTLIST[pid];
		try {
			server_socket = new ServerSocket(server_port, 0, SERVER_HOSTADD);
			while(true){
				Socket clientSocket = server_socket.accept();
				ObjectInputStream obj_ip = new ObjectInputStream(clientSocket.getInputStream());
				ProcessEvent event = (ProcessEvent)obj_ip.readObject();
				clientSocket.close();
				(new Thread(){
					@Override
					public void run(){
						manageTimeVector(event);
						return;
					}
				}).start();
			}
			/*if(!server_socket.isClosed()){
				server_socket.close();
			}*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void manageTimeVector(ProcessEvent event){
		try {
			event_buffer.add(event);
			enforceTimeOrder();			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void enforceTimeOrder(){
			for(int i=0; i < event_buffer.size(); i++){
				boolean delivered = false;
				ProcessEvent event = event_buffer.get(i);
				int event_pid = event.getPid();
				int[] event_timeVector = event.getTime_vector();
				
				for(int j=0; j <= event_timeVector.length; j++){
					if(j == event_timeVector.length){
						break;
					}
					
					//checking: ts(m)[i] = VCj[i]+1
					if(event_timeVector[event_pid] != (LogicalTimeKeeper.time_vector[event_pid] + 1)){
						break;
					}
					if((j != event_pid) && event_timeVector[j] > LogicalTimeKeeper.time_vector[j]){
						break;
					}
					
					// if last iteration
					if(j == (event_timeVector.length-1)){
						// Adjusting my time-vector
						int[] prevTime = new int[LogicalTimeKeeper.time_vector.length];
						prevTime = Arrays.copyOf(LogicalTimeKeeper.time_vector, prevTime.length);
						LogicalTimeKeeper.time_vector[event_pid] = Math.max(LogicalTimeKeeper.time_vector[event_pid], event_timeVector[event_pid]);
						delivered = true;
						deliverEvent(prevTime);
					}
				}
				
				if(delivered){
					event_buffer.remove(i);
				}
			}
//		}
	}

	public void sendEvent(){
		try {
			LogicalTimeKeeper.time_vector[pid] += 1;
			
			ProcessEvent myEvent = new ProcessEvent();
			myEvent.setEvent_id(PROCESS_EVENTLIST[pid]);
			myEvent.setTime_vector(LogicalTimeKeeper.time_vector);
			myEvent.setPid(pid);
			myEvent.setProcess_id(process_id);
			
			Socket socket;
			ObjectOutputStream obj_op;
			for(int port : PROCESS_PORTLIST){
				if(port == PROCESS_PORTLIST[pid]) continue;
				socket = new Socket(SERVER_HOSTADD,port);
				obj_op = new ObjectOutputStream(socket.getOutputStream());
				obj_op.writeObject(myEvent);

				obj_op.close();
				socket.close();
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public void deliverEvent(int[] prevTimeVector){
		StringBuilder prevTimeString = new StringBuilder("[");
		for(int i : prevTimeVector){
			prevTimeString.append(String.valueOf(i));
			prevTimeString.append(", ");
		}
		int lastComma = prevTimeString.lastIndexOf(",");
		prevTimeString.replace(lastComma, lastComma+1, " ]");

		StringBuilder timeString = new StringBuilder("[");
		for(int i : LogicalTimeKeeper.time_vector){
			timeString.append(String.valueOf(i));
			timeString.append(", ");
		}
		int lastComma1 = timeString.lastIndexOf(",");
		timeString.replace(lastComma, lastComma1+1, " ]");

		System.out.println("Time Adjusted: "+prevTimeString +" ---> "+timeString.toString());
		time_matched += 1;
	}

}
