package cgcl;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import Test.TestService;
import Test.TestStruct;

public class TestClient {

	public static final int nTest = 50000;
	public static final int ratio = 10;
	public static final int nthread = 10;
	public static void main(String[] args) throws TException{
		TaskRunner[] tRunners = new TaskRunner[nthread];
		for(int i = 0; i < nthread; i++){
			tRunners[i] = new TaskRunner(ratio, 20, 50, 100000, "localhost");
		}
		long  start = System.currentTimeMillis();
		for(TaskRunner taskRunner : tRunners){
			new Thread(taskRunner).start();
		}
		
		for(TaskRunner taskRunner : tRunners){
			while(!taskRunner.finish)
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		long end = System.currentTimeMillis();
		for(int i = 0; i < nthread; i++){
			System.out.println("thread id :" + i + " tps:" + tRunners[i].tps);
		}
		
		double trans = 1.0 *nthread * (ratio + 1) * nTest;
		double time = end - start;
		System.out.println("time cost is " + (end - start));
		System.out.println("the totoal tps is about " + (trans * 1000 /  time));
		
	}
	
	public static  class TaskRunner implements Runnable{

		public int rwratio = 0;
		public int keylen;
		public int vallen;
		public int ninsert;
		public String serveraddr = null;
		
		public double tps = 0;
		public long time = 0;
		public boolean finish = false;
		
		public TaskRunner(int rwratio, int keylen, int vallen, int ninsert, String addr ){
			this.rwratio = rwratio;
			this.keylen = keylen;
			this.vallen = vallen;
			this.ninsert = ninsert;
			this.serveraddr = addr;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			TTransport transport;
			transport = new TSocket(serveraddr, 9090);
			try {
				transport.open();
			} catch (TTransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TProtocol protocol = new  TBinaryProtocol(transport);
			TestService.Client tcClient = new TestService.Client(protocol);
			HashSet<String> hsHashSet = new HashSet<String>();
			ArrayList<String> vList = new ArrayList<String>();
			for(int i = 0; i < ninsert; i++){
				hsHashSet.add(RandomGenerator.getString(keylen));
				vList.add(RandomGenerator.getString(vallen));
			}
			
			
			int count = 0;
			long start = System.currentTimeMillis();
			for(String string : hsHashSet){
				try {
					tcClient.putVal(new TestStruct(string, vList.get(count++)));
				} catch (TException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			for(int i = 0; i < rwratio; i++){
				for(String string : hsHashSet){
					String str = null;
					try {
						str = tcClient.getVal(string);
					} catch (TException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					assert str != null;
				}
			}
			this.time= System.currentTimeMillis() - start;
			this.tps = 1.0 *(rwratio + 1) * ninsert * 1000 / time ; 
//			System.out.println("insert and read each " + nTest+" times, and we cost" + d +" ms");
		//	System.out.println("the tps is "+tps);
			finish = true;
		}
		
	}
}
