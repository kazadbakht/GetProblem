import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import abs.api.Actor;
import abs.api.Configuration;
import abs.api.Context;
import abs.api.LocalContext;
import abs.api.QueueInbox;

public class TestGetMain {

	static class Callee implements Actor {
		public Integer callee() {
			// System.out.println("Foo");
			return (Integer) 10;
		}
	}

	private static final ExecutorService EX = Executors.newWorkStealingPool();
	private static final Configuration config = Configuration.newConfiguration()
			.withInbox(new QueueInbox(EX)).build();
	private static final Context context = new LocalContext(config);

	public static void main(String[] args) {
		int n = 5000_000;
		if (args.length > 0) {
			n = Integer.parseInt(args[0]);
		}
		Callee testP = new Callee();
		Actor testPA = context.newActor("Printer", testP);
		final Callee p = (Callee) context.notary().get(testPA);
		Callable message = () -> p.callee();

		mainSequentialStyle(n, testP, message);

		EX.shutdownNow();
	}

	private static void mainSequentialStyle(int n, Callee testP, Callable message) {
		AtomicInteger count = new AtomicInteger(0);
		for (int i = 0; i < n; ++i) {
			Future<Integer> f = testP.send(testP, message);
			try {
				f.get();
				count.incrementAndGet();
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
		System.out.println(count);
	}

	private static void mainJava8Style(int n, Callee testP, Callable message) {
		// Send all messages at once
		// Collect the futures
		List<Future<Integer>> futures = IntStream.range(0, n).mapToObj(i -> message).map(m -> {
			Future<Integer> f = testP.send(testP, m);
			return f;
		}).collect(Collectors.toList());

		// Success factor?
		// If we can confirm that all messages were processed,
		// if not, we get a -1 which reduces counting.
		int success = futures.parallelStream().map(f -> {
			try {
				// System.out.println("Waiting on " + f);
				return f.get();
			} catch (Exception e) {
				e.printStackTrace(System.err);
				return -1;
			}
		}).mapToInt(x -> x).sum();
		System.out.println(success);
	}
}
