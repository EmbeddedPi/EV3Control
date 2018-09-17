package EV3Control;

import java.nio.ByteBuffer;

import org.usb4java.LibUsb;

public class Interface {
	public static void main(String args[]) {
		System.out.printf("testInterface");
		System.out.println();
		//Initialise EV3 object
		EV3 myEV3 = new EV3();
		ByteBuffer ops = ByteBuffer.allocateDirect(1);;
		ops.put(EV3.opNop);
		
		//Basic transmission tests
		myEV3.verbosity = false;		
		System.out.printf("Interface made verbosity false");
		System.out.println();
		myEV3.main();
		myEV3.verbosity = true;
		System.out.printf("Interface made verbosity true");
		System.out.println();
		//EV3.connectUsb();
		myEV3.main();
		myEV3.global = 6;		
		System.out.printf("Interface made global = 6");
		System.out.println();
		myEV3.main();
		myEV3.local = 16;		
		System.out.printf("Interface made local = 16");
		System.out.println();
		myEV3.main();
		System.out.println();
		
		//Sync mode transmission tests
		myEV3.sync_mode = EV3.SYNC;
		System.out.printf("sync_mode set to SYNC");
		System.out.println();
		myEV3.main();
		
		myEV3.sync_mode = EV3.ASYNC;
		myEV3.global = 0;
		System.out.printf("sync_mode set to ASYNC, global memory set to 0");
		System.out.println();
		myEV3.main();
		myEV3.global = 6;
		System.out.printf("sync_mode set to ASYNC, global memory set to 6");
		System.out.println();
		EV3.connectUsb();
		short counter1 = myEV3.sendDirectCmd(ops, myEV3.local, myEV3.global, myEV3.verbosity);
		short counter2 = myEV3.sendDirectCmd(ops, myEV3.local, myEV3.global, myEV3.verbosity);
		ByteBuffer reply1 = EV3.waitForReply(ops, myEV3.global, counter1, myEV3.verbosity);
		ByteBuffer reply2 = EV3.waitForReply(ops, myEV3.global, counter2, myEV3.verbosity);
		int received1 = 1019 - reply1.remaining();
		int received2 = 1019 - reply2.remaining();
		System.out.printf("received1 = " + received1);
		System.out.println();
		System.out.printf("received2 = " + received2);
		System.out.println();
		LibUsb.releaseInterface(EV3.handle, 0);
		LibUsb.close(EV3.handle);
		
		myEV3.sync_mode = EV3.STD;
		myEV3.global = 0;
		System.out.printf("sync_mode set to STD, global memory set to 0");
		System.out.println();
		myEV3.main();
		myEV3.global = 6;
		System.out.printf("Global memory set to 6");
		System.out.println();
		myEV3.main();
	}

}
