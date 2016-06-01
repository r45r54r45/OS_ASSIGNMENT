package _system;


public class BuddySystem {
	private int[] hardware;
	private int[] LRUstatus;
	private int[] block_info;
	private int frame_size;
	private static BuddySystem instance;
	private BuddySystem(){
		hardware=new int[Kernel.physical_momory_size/Kernel.page_size];
		LRUstatus=new int[Kernel.physical_momory_size/Kernel.page_size];
		block_info=new int[Kernel.physical_momory_size/Kernel.page_size];
		//if separated, give abs(parent value) ++ and if merge, put my value-- into 2 target partition
		//if used, give minus sign
		//so that when checking whether to merge or not, 
		frame_size=Kernel.page_size;
	}
	public static BuddySystem getInstance(){
		if ( instance == null )
			instance = new BuddySystem();
		return instance;
	}
	
	public void allocate(int pid, int reqSize, int alloc_id){ 
		//return allocate id
		//compute space 
		
		
		//register to hardware
		
		
		//generate new allocation id
	}
	private void merge(){ //merge separated partitions
		
	}
	
	public void release(int pid, int alloc_id){
		
	}
	
	public void access(int pid, int alloc_id){
		
	}
	
	//function for disable frame 
	
	
}
