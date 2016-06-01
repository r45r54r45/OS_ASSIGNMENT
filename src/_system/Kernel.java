package _system;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import _enum.KERNEL_CODE;


public class Kernel {
	public static int current_pid=0;
	public static int virtual_momory_size; //2^n
	public static int physical_momory_size; //2^n
	public static int page_size;
	public static int max_time_quantum;
	public static int max_process_count;
	public static int jugi_for_cycle;
	public static int cycle_count;
	private FileReader fr;
	private BufferedReader br;
	public Kernel.ExecuteBlock[] code;
	
	public Kernel(String args){
		try {
			fr = new FileReader(args);
			br = new BufferedReader(fr);
			String line;
			int lineNumber = -1;
			
			while ((line = br.readLine()) != null) {
				String st[] = line.split(" ");
				if (lineNumber == -1) {
					max_process_count=Integer.parseInt(st[0]);
					max_time_quantum=Integer.parseInt(st[1]);
					virtual_momory_size=Integer.parseInt(st[2]);
					physical_momory_size=Integer.parseInt(st[3]);
					page_size=Integer.parseInt(st[4]);
					jugi_for_cycle=Integer.parseInt(st[5]);
					cycle_count=Integer.parseInt(st[6]);
					code=new Kernel.ExecuteBlock[max_process_count];
					lineNumber++;
					continue;
				}
				if(st[1].compareTo("INPUT")==0){
					code[lineNumber]=this.new ExecuteBlock(Integer.parseInt(st[0]),st[2]);
				}else{
					code[lineNumber]=this.new ExecuteBlock(Integer.parseInt(st[0]),st[1]);
				}
				lineNumber++;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void doKernelOperation(int current_cycle,RR_Scheduler rr){
		System.out.println("kernel cycle: "+current_cycle);
		for(int i=0; i<code.length; i++){
			if(code[i].cycle==current_cycle){
				if(code[i].type==KERNEL_CODE.IO){
					rr.ioWaitComplete(code[i].target_pid);
				}
				if(code[i].type==KERNEL_CODE.CREATE){
					rr.initProcess(current_cycle, code[i].process);
				}
				
			}
		}
	}
	public void systemOutput(){
		//write system.txt
		
	}
	class ExecuteBlock{
		public int cycle;
		public KERNEL_CODE type;
		public String process;
		public int target_pid;
		public ExecuteBlock(int cycle, String process){
			this.cycle=cycle;
			this.process=process;
			this.type=KERNEL_CODE.CREATE;
		}
		public ExecuteBlock(int cycle, int pid){
			this.cycle=cycle;
			this.target_pid=pid;
			this.type=KERNEL_CODE.IO;
		}
	}
}
