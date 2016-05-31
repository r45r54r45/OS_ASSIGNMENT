package _system;
/*
 * compute location of hardware memory
 */
public class BuddySystem {
	public static int allocation_id=0;
	private int[] hardware;
	private int frame_size;
	private static BuddySystem instance;
	private BuddySystem(){
		hardware=new int[Kernel.physical_momory_size];
		frame_size=Kernel.page_size;
	}
	public static BuddySystem getInstance(){
		if ( instance == null )
			instance = new BuddySystem();
		return instance;
	}
	
	public int allocate(int ){ 
		
	}

	
	
}
