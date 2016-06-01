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
	static int finished_process; //may occur error or problem if last operation is sleep or wait
	public static void main(String[] args) {
		//initiate Kernel
		Kernel kernel=new Kernel(args[0]);
		//initiate BuddySystem
		bs=BuddySystem.getInstance();
		//initiate RR_Scheduler
		rr=RR_Scheduler.getInstance();
		while(cycle<20){//finished_process!=kernel.max_process_count
			//1. distribute cycle
			rr.distributeCycle(cycle);
			//2. check sleep process
			rr.checkSleepProcess();
			//3. io operation 
			//4. create operation
			kernel.doKernelOperation(cycle, rr);
			//5. if not current process, use scheduler to get one
			if(current_process==null){
				current_process=rr.scheduleProcess(cycle);
			}
			//6. system.txt output
			kernel.systemOutput();
			//7. do process operation
			if(current_process!=null){
				if(!current_process.operate()){
					//if false, increase kernel's end process count
					System.out.println("process ended in CPU");
					finished_process++;
				}
				if(!rr.checkTQ(current_process)){
					System.out.println("[[CPU]] process tq finish: "+current_process.pid+", CPU cycle: "+cycle);
					current_process=null;
				}
				//check if this process has used all of its tq
			}
			cycle++;
		}

	}
}
