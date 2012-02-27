
package cgcl;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.thrift.TException;

import Test.*;

public class Handler implements TestService.Iface{

	private ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
	@Override
	public String getVal(String key) throws TException {
		// TODO Auto-generated method stub
		String val = map.get(key);
		return val;
	}

	@Override
	public boolean putVal(TestStruct ts) throws TException {
		// TODO Auto-generated method stub
		map.put(ts.key, ts.val);
		return true;
	}
	
}
