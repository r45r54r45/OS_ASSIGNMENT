package _system;
/*
 * manage process and ready queue
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import _enum.OP_CODE;
import _enum.PROCESS_STATE;
import _main.CPU;

public class RR_Scheduler {
	private static RR_Scheduler instance;
	Queue<Process> queue;
	Queue<Process> sleep_list;
	Queue<Process> io_list;
	FileReader fr;
	BufferedReader br;
	private int jugi;
	private int cycle;
	private static int current_jugi_cycle=0;
	
	private RR_Scheduler() {
		queue = new LinkedList<Process>();
		jugi=Kernel.jugi_for_cycle;
		cycle=Kernel.cycle_count;
		sleep_list=new LinkedList<Process>();
		io_list=new LinkedList<Process>();
	}
	public static RR_Scheduler getInstance(){
		if ( instance == null )
			instance = new RR_Scheduler();
		return instance;
	}

	public void initProcess(int cpuCycle, String codeFile) {
		// initiate process
		Process process=new Process(Kernel.current_pid++,codeFile);
		process.process_state=PROCESS_STATE.CREATE;
		process.time_quantum=Kernel.max_time_quantum;
		System.out.println("[[PROCESS]] init pid: "+process.pid );
		try {
			fr = new FileReader(codeFile);
			br = new BufferedReader(fr);
			String line;
			int lineNumber = -1;
			int numOpcode;
			Process.CodeBlock[] code = null;
			while ((line = br.readLine()) != null) {
				String st[] = line.split(" ");
				if (lineNumber == -1) {
					numOpcode = Integer.parseInt(st[0]);
					code=new Process.CodeBlock[numOpcode];
					lineNumber++;
					continue;
				}
				code[lineNumber]=process.new CodeBlock(OP_CODE.values()[Integer.parseInt(st[0])], Integer.parseInt(st[1]));
				lineNumber++;
				//codeblock init
			}
			process.setImage(code);
		} catch (IOException e) {
			e.printStackTrace();
		}
		queue.offer(process);
		
	}
	
	public void distributeCycle(int current_cycle){
		current_jugi_cycle++;
		//스케쥴러 안에 있는 프로세스들의 사이클이 모두 0인지 확인 (그러면 바로 부여)
		Iterator<Process> it=queue.iterator();
		int checkFlag=0;
		while(it.hasNext()){
			Process seeked=it.next();
			if(seeked.isTQEnd())checkFlag++;
		}
		if(checkFlag==queue.size()&&queue.size()!=0){
			current_jugi_cycle=0;
			//모두 tq 가 0인경우
			Iterator<Process> it2=queue.iterator();
			while(it2.hasNext()){
				Process seeked=it2.next();
				seeked.time_resource+=cycle;
			}
		}
		//이번이 사이클 부여 주기인지 확인 
		if(current_jugi_cycle==jugi){
			current_jugi_cycle=0;
			Iterator<Process> it3=queue.iterator();
			while(it3.hasNext()){
				Process seeked=it3.next();
				seeked.time_resource+=cycle;
			}
		}
		
		
	}
	public void sleepInit(int pid, int sleepingTime){
		//init sleeping procedure of process
		Process process=findProcessByPid(pid);
		queue.remove(process);
		process.sleep_cycle=sleepingTime;
		process.process_state=PROCESS_STATE.SLEEPING;
		sleep_list.offer(process);
	}
	public void ioInit(int pid){
		Process process=findProcessByPid(pid);
		queue.remove(process);
		process.process_state=PROCESS_STATE.WAITING;
		io_list.offer(process);
	}
	public void checkSleepProcess(){
		//check sleeping process //if sleep is ended insert that process into queue	
		Iterator<Process> sleeping=sleep_list.iterator();
		while(sleeping.hasNext()){
			Process seeked=sleeping.next();
			if(seeked.sleep_cycle==1){ //check if sleep is ended
				//since sleep cycle will be -1 in this cycle, check if it is 1 
				seeked.sleep_cycle=-1;
				sleep_list.remove(seeked); //delete it from sleeping list
				enterLast(seeked); //put it in the queue
			}
		}
	}
	public boolean checkTQ(Process process){
		process.time_quantum--;
		if(process.isTQEnd()){
			enterLast(process);
			return false; //tq over
		}else{
			return true; //tq is remaining
		}
	}
	public Process scheduleProcess(int cycle) {
		Process process=queue.poll();
		if(process!=null){
			//if there is sth came out of the queue
			if(process.time_resource>=Kernel.max_time_quantum){
				process.time_resource-=Kernel.max_time_quantum;
				process.time_quantum+=Kernel.max_time_quantum;
			}else{
				process.time_quantum=process.time_resource;
			}
			System.out.println(cycle+"   "+process.pid+"   "+process.name);
			return process;
		}
		return null;
		
	}
	public void tqFinishRequeue(Process process){
		System.out.println(cycle+"   "+process.pid+"   "+process.name);
		queue.offer(process);
	}

	public void enterLast(Process process) {
		process.process_state=PROCESS_STATE.READY;
		queue.offer(process);
	}
	public void ioWaitComplete(int pid) {
		Process process=findProcessByPid(pid);
		io_list.remove(process);
		enterLast(process);
	}
	public Process findProcessByPid(int pid){
		Iterator<Process> it=queue.iterator();
		while(it.hasNext()){
			Process seeked=it.next();
			if(seeked.pid==pid){ 
				return seeked;
			}
		}
		return null;
	}
}
