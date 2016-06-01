package _system;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import _enum.OP_CODE;
import _enum.PROCESS_STATE;

public class Process {
	public int pid;
	public int program_counter=0;
	public PROCESS_STATE process_state;
	public PageTableCell[] page_table;
	public int time_quantum;
	public int sleep_cycle;
	public CodeBlock[] image;
	public Process(int pid){
		this.pid=pid;
		process_state=PROCESS_STATE.CREATE; //프로세스 상태를 변경 
		page_table=new PageTableCell[Kernel.virtual_momory_size/Kernel.page_size];
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
		RR_Scheduler rr=RR_Scheduler.getInstance();
		switch (block.opcode) {
		case MEMORY_ALLOCATION:
			allocateOperation(block.data);
			break;
		case MEMORY_ACCESS:
			memoryAccess(block.data);
			break;
		case MEMORY_RELEASE:
			releaseOperation(block.data);
			break;
		case SLEEP:
			this.sleep_cycle=block.data;
			rr.sleepInit(this.pid, sleep_cycle);
			break;
		case IO_WAIT:
			rr.ioInit(this.pid);
			break;
		case NONMEMORY_INSTRUCTION:
			//spend 1 cycle
			break;
		}
	}
	
	private void allocateOperation(int reqPage){ //manage pageTable
		for(int i=0; i<page_table.length-reqPage+1;i++){
			boolean isEmpty=true;
			for(int j=0; j<reqPage; j++){
				if(page_table[i+j]!=null)isEmpty=false;
			}
			if(isEmpty){
				//has index i which is the start of target pages
				BuddySystem bs=BuddySystem.getInstance();
				//retrieve allocation id from buddysystem
				int new_alloc_id=bs.allocate(this.pid,reqPage);
				for(int start=i; start<i+reqPage; start++){
					page_table[start]=new PageTableCell(new_alloc_id);
				}
			}
		}
	}
	private void releaseOperation(int alloc_id){
		//release inside pagetable
		Iterator<Integer> it=findPageWithAllocId(alloc_id).iterator();
		boolean valid=isValid(alloc_id);
		while(it.hasNext()){
			int target_page_index=it.next();
			page_table[target_page_index]=null;
		}
		//release in buddySystem if it is valid
		if(valid){
			BuddySystem bs=BuddySystem.getInstance();
			bs.release(alloc_id);
		}
	}
	private void memoryAccess(int alloc_id){
		BuddySystem bs=BuddySystem.getInstance();
		if(isValid(alloc_id)){
			//valid
			bs.access(alloc_id);
		}else{
			//invalid
			generatePageFault();
			//request to buddy system
			bs.allocate(pid, findPageWithAllocId(alloc_id).size(), alloc_id); 
			//find how many pages to be reallocate and reallocate
			//LRU should be refreshed
			bs.access(alloc_id);
		}
	}
	private void generatePageFault(){
		System.out.println("page fault occur");
	}
	private boolean isValid(int alloc_id){
		//may generate error if that allocation id is not in pagetable
		if(page_table[findPageWithAllocId(alloc_id).get(0)].valid_bit==true) 
			return true;
		else return false;
	}
	private ArrayList<Integer> findPageWithAllocId(int alloc_id){
		ArrayList<Integer> result=new ArrayList<Integer>();
		for(int i=0; i<page_table.length; i++){
			if(page_table[i]!=null){
				if(page_table[i].allocation_id==alloc_id){
					result.add(i);
				}
			}
		}
		return result;
	}
	class CodeBlock{
		private OP_CODE opcode; //opCode, 명령어 
		private int data; //argument
		public CodeBlock(OP_CODE i, int d){
			opcode=i;
			data=d;
		}
	}
	class PageTableCell{
		public boolean valid_bit;
		public int allocation_id;	
		public int frame_index;
		public PageTableCell(int new_alloc_id){
			/*
			 * allocation is made to hardware by buddy so that
			 * it contains alloc_id and valid bit is set to true
			 */
			allocation_id=new_alloc_id;
			valid_bit=true;
		}
	}
}
