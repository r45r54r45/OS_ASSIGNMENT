package _system;


public class BuddySystem {
	public static int allocation_id=0; //start from 0
	private int[] hardware;
	private int[] LRUstatus;
	private int[] block_info;
	private int frame_size;
	private static BuddySystem instance;
	private BuddySystem(){
		hardware=new int[Kernel.physical_momory_size/Kernel.page_size];
		LRUstatus=new int[Kernel.physical_momory_size/Kernel.page_size];
		block_info=new int[Kernel.physical_momory_size/Kernel.page_size];
		frame_size=Kernel.page_size;
	}
	public static BuddySystem getInstance(){
		if ( instance == null )
			instance = new BuddySystem();
		return instance;
	}
	
	public int allocate(int pid, int reqSize){ 
		//return allocate id
		//compute space 
		
		
		//register to hardware
		
		
		//generate new allocation id
		int  new_alloc_id=allocation_id++;
		return new_alloc_id;
	}
	private void merge(){ //merge separated partitions
		
	}
	public void allocate(int pid, int reqSize,int old_alloc_id){  //reallocate
		
		
	}
	
	public void release(int alloc_id){
		
	}
	
	public void access(int alloc_id){
		
	}
	
	//function for disable frame 
	
	
}
