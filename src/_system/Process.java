package _system;

import _enum.OP_CODE;
import _enum.PROCESS_STATE;

public class Process {
	public int pid;
	public int program_counter=0;
	public PROCESS_STATE process_state;
	public int[] virtual_memory;
	public int time_quantum;
	public int sleep_cycle;
	public CodeBlock[] image;
	//TODO page table
	public Process(int pid){
		this.pid=pid;
		process_state=PROCESS_STATE.CREATE; //프로세스 상태를 변경 
		virtual_memory=new int[Kernel.virtual_momory_size];
		time_quantum=0;
		sleep_cycle=-1;
	}
	public void setImage(CodeBlock[] image){
		this.image=image;
	}

	public boolean isTQEnd(){
		if(time_quantum==0)return true;
		else return false;
	}
	public void operate(){
		program_counter++; //1st code is finished in program_counter 1 
		CodeBlock block=image[program_counter-1];
		
		switch (block.opcode) {
		case MEMORY_ALLOCATION:
			
			break;
		case MEMORY_ACCESS:

			break;
		case MEMORY_RELEASE:

			break;
		case SLEEP:
			this.sleep_cycle=block.data;
			this.process_state=PROCESS_STATE.WAITING; //not waiting for io, but sleeping
			break;
		case IO_WAIT:
			this.process_state=PROCESS_STATE.WAITING;
			break;
		case NONMEMORY_INSTRUCTION:
			//spend 1 cycle
			break;
		}
	}
	class CodeBlock{
		private OP_CODE opcode; //opCode, 명령어 
		private int data; //argument
		public CodeBlock(OP_CODE i, int d){
			opcode=i;
			data=d;
		}
	}
}
