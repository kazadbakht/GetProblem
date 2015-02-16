import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import abs.api.Actor;
import abs.api.Configuration;
import abs.api.Context;
import abs.api.DispatchInbox;
import abs.api.LocalContext;

class Callee implements Actor {
	public int callee() {
		//System.out.println("Foo");
		return (Integer) 10;
	}
}

public class TestGetMain {
	private static final Configuration config = Configuration
			.newConfiguration()
			.withInbox(new DispatchInbox(Executors.newWorkStealingPool()))
			.build();
	private static final Context context = new LocalContext(config);

	public static void main(String[] args) {
		int n = Integer.parseInt(args[0]);
		Callee testP = new Callee();
		Actor testPA = context.newActor("Printer", testP);
//		testP.printout();
    final Callee p = (Callee) context.notary().get(testPA);
//    System.out.println(p);
    Callable message = () -> p.callee();
    for (int i = 0; i < n; i++)
    {
    	Future<Integer> r = testP.send(testP, message);
    	int a = 0;

    	try {
    		a = r.get();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    //System.out.println(a);
	}
}
