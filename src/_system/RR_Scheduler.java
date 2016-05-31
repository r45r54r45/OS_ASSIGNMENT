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
	
	FileReader fr;
	BufferedReader br;
	private int jugi;
	private int cycle;
	private RR_Scheduler() {
		queue = new LinkedList<Process>();
		jugi=Kernel.jugi_for_cycle;
		cycle=Kernel.cycle_count;
	}
	public static RR_Scheduler getInstance(){
		if ( instance == null )
			instance = new RR_Scheduler();
		return instance;
	}

	public void initProcess(int cpuCycle, String codeFile) {
		// initiate process
		Process process=new Process(Kernel.current_pid++);
		process.process_state=PROCESS_STATE.CREATE;
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
				//codeblock init
			}
			process.setImage(code);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void distributeCycle(int current_cycle){
		//스케쥴러 안에 있는 프로세스들의 사이클이 모두 0인지 확인 (그러면 바로 부여)
		Iterator<Process> it=queue.iterator();
		int checkFlag=0;
		while(it.hasNext()){
			Process seeked=it.next();
			if(seeked.isTQEnd())checkFlag++;
		}
		if(checkFlag==queue.size()){
			//모두 tq 가 0인경우
			Iterator<Process> it2=queue.iterator();
			while(it2.hasNext()){
				Process seeked=it.next();
				seeked.time_quantum=cycle;
			}
		}
		//이번이 사이클 부여 주기인지 확인 
		if(current_cycle%10==0){
			Iterator<Process> it2=queue.iterator();
			while(it2.hasNext()){
				Process seeked=it.next();
				seeked.time_quantum=cycle;
			}
		}
	}
	
	public void checkSleepProcess(){
		//check sleeping process //if sleep is ended insert that process into queue
		Iterator<Process> it=queue.iterator();
		while(it.hasNext()){
			Process seeked=it.next();
			if(seeked.sleep_cycle==1){ 
				//since sleep cycle will be -1 in this cycle, check if it is 1 
				seeked.sleep_cycle=-1;
				queue.remove(seeked); //delete it from queue
				enterLast(seeked); //put it in the queue
			}
		}
	}
	public void checkTQ(Process process){
		process.time_quantum--;
		if(process.isTQEnd())enterLast(process);
	}
	public Process select() {
		//sleeping 인 프로세스는 제외해야함.
		return null;
	}

	public void enterLast(Process process) {
		process.process_state=PROCESS_STATE.READY;
		queue.add(process);
	}
	public void ioWaitComplete(int pid) {
		Process process=findProcessByPid(pid);
		queue.remove(process);
		process.process_state=PROCESS_STATE.READY;
		queue.add(process);
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
