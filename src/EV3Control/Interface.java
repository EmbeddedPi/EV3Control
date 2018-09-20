package EV3Control;

import java.nio.ByteBuffer;

import org.usb4java.LibUsb;

public class Interface {
	public static void main(String args[]) {
		System.out.printf("testInterface");
		System.out.println();
		//Initialise EV3 object
		EV3 myEV3 = new EV3();
		
		/*
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
		*/
		
		/*
		//Sync mode transmission tests
		myEV3.sync_mode = EV3.SYNC;
		System.out.printf("sync_mode set to SYNC");
		System.out.println();
		myEV3.main();
		System.out.println();
		System.out.println();
		
		
		myEV3.sync_mode = EV3.ASYNC;
		myEV3.global = 0;
		System.out.printf("sync_mode set to ASYNC, global memory set to 0");
		System.out.println();
		myEV3.main();
		System.out.println();
		System.out.println();
		
		
		myEV3.global = 6;
		System.out.printf("sync_mode set to ASYNC, global memory set to 6");
		System.out.println();
		EV3.connectUsb();
		ByteBuffer ops = ByteBuffer.allocateDirect(1);;
		ops.put(EV3.opNop);
		short counter1 = myEV3.sendDirectCmd(ops, myEV3.local, myEV3.global);
		short counter2 = myEV3.sendDirectCmd(ops, myEV3.local, myEV3.global);
		ByteBuffer reply1 = myEV3.waitForReply(myEV3.global, counter1);
		ByteBuffer reply2 = myEV3.waitForReply(myEV3.global, counter2);
		int received1 = 1019 - reply1.remaining();
		int received2 = 1019 - reply2.remaining();
		System.out.printf("received1 = " + received1);
		System.out.println();
		System.out.printf("received2 = " + received2);
		System.out.println();
		LibUsb.releaseInterface(EV3.handle, 0);
		LibUsb.close(EV3.handle);
		System.out.println();
		System.out.println();
		
		myEV3.sync_mode = EV3.STD;
		myEV3.global = 0;
		System.out.printf("sync_mode set to STD, global memory set to 0");
		System.out.println();
		myEV3.main();
		System.out.println();
		System.out.println();
		myEV3.global = 6;
		System.out.printf("sync_mode set to STD, global memory set to 6");
		System.out.println();
		myEV3.main();
		*/
		
		//Testing code for naming brick
		EV3.connectUsb();
		myEV3.sync_mode = EV3.ASYNC;
		String brickName = "myEV3";
		int brickNameLength = brickName.length();
		ByteBuffer ops2 = ByteBuffer.allocateDirect(brickNameLength + 4);
		ops2.put(EV3.opCom_Set);
		ops2.put(EV3.SET_BRICKNAME);
		byte[] myLCSName = EV3.LCS(brickName);
		for (int i=0; i < myLCSName.length; i++) {
			ops2.put(myLCSName[i]);
		}
		System.out.println();		
		System.out.print("Buffer length is " + ops2.position());
		System.out.println();	
		EV3.printHex("Test operation", ops2);
		System.out.println();
		

		System.out.printf("Attempting to send opCom_Set \n");
		short counter3 = myEV3.sendDirectCmd(ops2, myEV3.local, myEV3.global);
		ByteBuffer reply3 = myEV3.waitForReply(myEV3.global, counter3);
		int received3 = 1019 - reply3.remaining();
		System.out.printf("received3 = " + received3);
		System.out.println();
		//If use EV3 then no warnings but error upon reply message
		LibUsb.releaseInterface(myEV3.handle, 0);
		LibUsb.close(myEV3.handle);
		System.out.println();
		System.out.println();
		
	}

}
