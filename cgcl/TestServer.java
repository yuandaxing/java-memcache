package cgcl;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import Test.TestService;

public class TestServer {

	private static Handler handler = null;
	private static TestService.Processor<Handler> p = null;
	/**
	 * @param args
	 * @throws TTransportException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws TTransportException, UnknownHostException {
		// TODO Auto-generated method stub
		handler = new Handler();
		p = new TestService.Processor<Handler>(handler);
//		InetAddress address = new InetAddress("192.168.226.212");
		InetSocketAddress soket = new InetSocketAddress("localhost", 9090);
		TServerTransport serverTransport = new TServerSocket(soket);
		 TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(p));
		server.serve();
	}

}
