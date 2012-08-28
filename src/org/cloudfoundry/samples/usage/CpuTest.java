package org.cloudfoundry.samples.usage;

public class CpuTest extends Thread {
	@Override
	public void run() {
		int busy = 10000000;
		while (true) {
			busy--;
			if (busy == 0) {
				break;
			}
		}
	}
}
