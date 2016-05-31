package _main;

import _system.BuddySystem;
import _system.Kernel;
import _system.RR_Scheduler;
import _system.Process;

public class CPU {
	static int cycle=0;
	static RR_Scheduler rr;
	static BuddySystem bs;
	static Process current_process;
	public static void main(String[] args) {
		//initiate Kernel
		Kernel kernel=new Kernel(args[0]);
		//initiate BuddySystem
		bs=BuddySystem.getInstance();
		//initiate RR_Scheduler
		rr=RR_Scheduler.getInstance();
		//1. distribute cycle
		rr.distributeCycle(cycle);
		//2. check sleep process
		rr.checkSleepProcess();
		//3. io operation 
		//4. create operation
		kernel.doKernelOperation(cycle, rr);
		//5. if not current process, use scheduler to get one
		if(current_process==null){
			current_process=rr.select();
		}
		//6. system.txt output
		kernel.systemOutput();
		//7. do process operation
		current_process.operate();
		rr.checkTQ(current_process); //check if this process has used all of its tq
	}
}
