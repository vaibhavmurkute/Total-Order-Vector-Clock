
public class VectorClock {

	public static void main(String[] args) {
		LogicalTimeKeeper totalOrder = new LogicalTimeKeeper();
		totalOrder.init();


		try {
			if(LogicalTimeKeeper.connectionThread != null){
				LogicalTimeKeeper.connectionThread.join();
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Process "+LogicalTimeKeeper.process_id+" ended!");

	}

}
