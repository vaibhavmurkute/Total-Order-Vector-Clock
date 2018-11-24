import java.io.Serializable;
import java.util.Comparator;

/*
 * @author: Vaibhav Murkute
 * Project: Vector Clock for Total-Order Multicast 
 * date: 11/15/2018
 */

public class ProcessEvent implements Serializable{
	private static final long serialVersionUID = 1001626620L;
	private String process_id;
	private int pid;
	private int[] time_vector;
	private String event_id;
//	private boolean ack = false;
	
	public String getProcess_id() {
		return process_id;
	}
	
	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public void setProcess_id(String process_id) {
		this.process_id = process_id;
	}
	
	public int[] getTime_vector() {
		return time_vector;
	}

	public void setTime_vector(int[] time_vector) {
		this.time_vector = time_vector;
	}

	public String getEvent_id() {
		return event_id;
	}
	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}
	
}
