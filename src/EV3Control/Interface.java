package EV3Control;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import org.usb4java.LibUsb;

public class Interface {
	public static void main(String args[]) throws InterruptedException {
		System.out.printf("Starting test interface");
		System.out.println();		
		
		//Basic transmission tests
		//basicTest();
	
		//Sync mode transmission tests
		//testSync();
		
		//Naming brick
		//EV3 myEV3 = new EV3();
		//myEV3.setBrickName("MySteve");
	
		//Playing sound
		//testSound();

		//Test code for repeating sounds
		//testSoundRepeat();
		
		//Test code for multiple tones
		//testTones();
		//beepBeep();
		//beepBeep2();
		
		//Test code for LEDs
		//disco();
		
		//Test code for drawing
		//testDraw();
		
		//Test LVX method
		//byte[] array = EV3.LVX(15);
		//System.out.printf("%02X|", array[0]);
		
		//Test code for timer
		//testTimer1();
		//testTimer2();
		
		//Test code to run programme
		//testRun();
		
		//Test code for simulating button presses
		//testButton();
		
		//Test code for running  motors
		testMotor();
	}
	
	@SuppressWarnings("unused")
	private static void basicTest () {
		EV3 myEV3 = new EV3();
		ByteBuffer ops = ByteBuffer.allocateDirect(1);
		ops.put(EV3.opNop);
		myEV3.verbosity = false;		
		System.out.printf("Interface made verbosity false");
		System.out.println();
		myEV3.main(ops);
		myEV3.verbosity = true;
		System.out.printf("Interface made verbosity true");
		System.out.println();
		//EV3.connectUsb();
		myEV3.main(ops);
		myEV3.global = 6;		
		System.out.printf("Interface made global = 6");
		System.out.println();
		myEV3.main(ops);
		myEV3.local = 16;		
		System.out.printf("Interface made local = 16");
		System.out.println();
		myEV3.main(ops);
		System.out.println();
	}
	
	@SuppressWarnings("unused")
	private static void testSync() {
		EV3 myEV3 = new EV3();
		ByteBuffer ops = ByteBuffer.allocateDirect(1);
		ops.put(EV3.opNop);
		myEV3.sync_mode = EV3.SYNC;
		System.out.printf("sync_mode set to SYNC");
		System.out.println();
		myEV3.main(ops);
		System.out.println();
		System.out.println();
			
		myEV3.sync_mode = EV3.ASYNC;
		myEV3.global = 0;
		System.out.printf("sync_mode set to ASYNC, global memory set to 0");
		System.out.println();
		myEV3.main(ops);
		System.out.println();
		System.out.println();
		
		myEV3.global = 6;
		System.out.printf("sync_mode set to ASYNC, global memory set to 6");
		System.out.println();
		EV3.connectUsb();
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
		myEV3.main(ops);
		System.out.println();
		System.out.println();
		myEV3.global = 6;
		System.out.printf("sync_mode set to STD, global memory set to 6");
		System.out.println();
		myEV3.main(ops);
	}
	
	@SuppressWarnings("unused")
	private static void testSound() {	
	EV3 myEV3 = new EV3();
	myEV3.sync_mode = EV3.ASYNC;
	String soundName = "./ui/DownloadSucces";
	int soundNameLength = soundName.length();
	byte[] myLCXName = EV3.LCX(50);
	ByteBuffer ops = ByteBuffer.allocateDirect(soundNameLength + 4 + myLCXName.length);
	ops.put(EV3.opSound);
	ops.put(EV3.PLAY);
	for (int i=0; i < myLCXName.length; i++) {
		ops.put(myLCXName[i]);
		System.out.print("LCX byte [" + i + "] is  " + myLCXName[i]);
		System.out.println();
	}
	byte[] myLCSName = EV3.LCS("./ui/DownloadSucces");
	for (int i=0; i < myLCSName.length; i++) {
		ops.put(myLCSName[i]);
	}
	myEV3.main(ops);
	}
	
	@SuppressWarnings("unused")
	private static void testSoundRepeat () {
		EV3 myEV3 = new EV3();
		myEV3.sync_mode = EV3.ASYNC;
		String soundName = "./ui/DownloadSucces";
		int soundNameLength = soundName.length();
		byte[] myLCXName = EV3.LCX(50);
		ByteBuffer ops = ByteBuffer.allocateDirect(soundNameLength + 4 + myLCXName.length);
		ops.put(EV3.opSound);
		ops.put(EV3.REPEAT);

		for (int i=0; i < myLCXName.length; i++) {
			ops.put(myLCXName[i]);
			System.out.print("LCX byte [" + i + "] is  " + myLCXName[i]);
			System.out.println();
		}
		byte[] myLCSName = EV3.LCS("./ui/DownloadSucces");
		for (int i=0; i < myLCSName.length; i++) {
			ops.put(myLCSName[i]);
		}
		myEV3.main(ops);

		ByteBuffer ops2 = ByteBuffer.allocateDirect(2);
		try {
		TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ops2.put(EV3.opSound);
		ops2.put(EV3.BREAK);
		myEV3.main(ops2);
	}
	
	@SuppressWarnings("unused")
	private static void testTones() {
		EV3 myEV3 = new EV3();
		myEV3.sync_mode = EV3.ASYNC;
		myEV3.playTone(1,262,500);
		myEV3.playTone(1,330,500);
		myEV3.playTone(1,392,500);
		myEV3.playTone(1,523,1000);
	}
	
	public static void beepBeep() {
		int tempo = 400;
		int full = tempo;
		int half = tempo /2;
		int eighth = tempo / 8;
		int threeEighth = 3* tempo / 8;

		EV3 myEV3 = new EV3();	
		EV3.connectUsb();
		myEV3.sync_mode = EV3.ASYNC;

		ByteBuffer ops = ByteBuffer.allocateDirect(116);
		
		ops.put(EV3.opSound);
		ops.put(EV3.TONE);
		ops.put(EV3.LCX(1));
		ops.put(EV3.LCX(185));
		ops.put(EV3.LCX(full));
	    ops.put(EV3.opSound_Ready);
		ops.put(EV3.opSound);
		ops.put(EV3.TONE);
		ops.put(EV3.LCX(1));
		ops.put(EV3.LCX(185));
		ops.put(EV3.LCX(full));
	    ops.put(EV3.opSound_Ready);
		ops.put(EV3.opSound);
		ops.put(EV3.TONE);
		ops.put(EV3.LCX(1));
		ops.put(EV3.LCX(165));
		ops.put(EV3.LCX(threeEighth));
	    ops.put(EV3.opSound_Ready);
		ops.put(EV3.opSound);
		ops.put(EV3.TONE);
		ops.put(EV3.LCX(1));
		ops.put(EV3.LCX(165));
		ops.put(EV3.LCX(eighth));
		ops.put(EV3.opSound_Ready);
		ops.put(EV3.opSound);
		ops.put(EV3.TONE);
		ops.put(EV3.LCX(1));
		ops.put(EV3.LCX(185));
		ops.put(EV3.LCX(half));
		ops.put(EV3.opSound_Ready);
		ops.put(EV3.opSound);
		ops.put(EV3.TONE);
		ops.put(EV3.LCX(0));
		ops.put(EV3.LCX(185));
		ops.put(EV3.LCX(threeEighth));
		ops.put(EV3.opSound_Ready);
		ops.put(EV3.opSound);
		ops.put(EV3.TONE);
		ops.put(EV3.LCX(1));
		ops.put(EV3.LCX(185));
		ops.put(EV3.LCX(eighth));
		ops.put(EV3.opSound_Ready);
		ops.put(EV3.opSound);
		ops.put(EV3.TONE);
		ops.put(EV3.LCX(1));
		ops.put(EV3.LCX(185));
		ops.put(EV3.LCX(half));
		ops.put(EV3.opSound_Ready);
		ops.put(EV3.opSound);
		ops.put(EV3.TONE);
		ops.put(EV3.LCX(0));
		ops.put(EV3.LCX(185));
		ops.put(EV3.LCX(full));
		ops.put(EV3.opSound_Ready);
		ops.put(EV3.opSound);
		ops.put(EV3.TONE);
		ops.put(EV3.LCX(1));
		ops.put(EV3.LCX(165));
		ops.put(EV3.LCX(threeEighth));
	    ops.put(EV3.opSound_Ready);
		ops.put(EV3.opSound);
		ops.put(EV3.TONE);
		ops.put(EV3.LCX(1));
		ops.put(EV3.LCX(165));
		ops.put(EV3.LCX(eighth));
		ops.put(EV3.opSound_Ready);
		ops.put(EV3.opSound);
		ops.put(EV3.TONE);
		ops.put(EV3.LCX(1));
		ops.put(EV3.LCX(220));
		ops.put(EV3.LCX(half));
		
		myEV3.sendDirectCmd(ops, myEV3.local, myEV3.global);
				
		System.out.println();
		//If use EV3 then no warnings but error upon reply message
		LibUsb.releaseInterface(EV3.handle, 0);
		LibUsb.close(EV3.handle);
		System.out.println();
		System.out.println();		
	}
	
	public static void beepBeep2 () {
		EV3 myEV3 = new EV3();	
		myEV3.sync_mode = EV3.ASYNC;
		int tempo = 400;
		int full = tempo;
		int half = tempo /2;
		int eighth = tempo / 8;
		int threeEighth = 3* tempo / 8;
		myEV3.playTone(1,185,full);
		myEV3.playTone(1,185,full);
		myEV3.playTone(1,165,threeEighth);
		myEV3.playTone(1,165,eighth);
		myEV3.playTone(1,185,half);
		myEV3.playTone(0,0,threeEighth);
		myEV3.playTone(1,185,eighth);
		myEV3.playTone(1,185,half);
		myEV3.playTone(0,0,full);
		myEV3.playTone(1,165,threeEighth);
		myEV3.playTone(1,165,eighth);
		myEV3.playTone(1,220,half);	
	}
	
	public static void disco() throws InterruptedException {
		EV3 myEV3 = new EV3();
		myEV3.sync_mode = EV3.ASYNC;
		System.out.print("Attempting to set colours");
		System.out.println();
		myEV3.setLED(EV3.LED_RED);
		Thread.sleep(1000);
		myEV3.setLED(EV3.LED_GREEN);
		Thread.sleep(1000);
		myEV3.setLED(EV3.LED_ORANGE);
		Thread.sleep(1000);
		myEV3.setLED(EV3.LED_RED_FLASH);
		Thread.sleep(1000);
		myEV3.setLED(EV3.LED_GREEN_FLASH);
		Thread.sleep(1000);
		myEV3.setLED(EV3.LED_ORANGE_FLASH);
		Thread.sleep(1000);
		myEV3.setLED(EV3.LED_RED_PULSE);
		Thread.sleep(1000);
		myEV3.setLED(EV3.LED_GREEN_PULSE);
		Thread.sleep(1000);
		myEV3.setLED(EV3.LED_ORANGE_PULSE);
		Thread.sleep(1000);
		myEV3.setLED(EV3.LED_OFF);
	}
	
	@SuppressWarnings("unused")
	private static void testDraw() throws InterruptedException {
		System.out.print("Attempting to draw things");
		System.out.println();
		EV3 myEV3 = new EV3();
		myEV3.sync_mode = EV3.ASYNC;
		ByteBuffer ops = ByteBuffer.allocateDirect(48);
		ops.put(EV3.opUI_Draw);
		ops.put(EV3.TOPLINE);
		ops.put(EV3.LCX(0));		// ENABLE
		ops.put(EV3.opUI_Draw);
		ops.put(EV3.BMPFILE);
		ops.put(EV3.LCX(1));        // COLOUR
		ops.put(EV3.LCX(0));        // X0
		ops.put(EV3.LCX(0));        // Y0
		ops.put(EV3.LCS("../apps/Motor Control/MotorCtlAD.rgf")); // NAME
		ops.put(EV3.opUI_Draw);
		ops.put(EV3.UPDATE);
		myEV3.main(ops);
		Thread.sleep(5000);
		ByteBuffer ops2 = ByteBuffer.allocateDirect(10);
	    ops2.put(EV3.opUI_Draw);
	    ops2.put(EV3.TOPLINE);
	    ops2.put(EV3.LCX(1));     // ENABLE
	    ops2.put(EV3.opUI_Draw);
	    ops2.put(EV3.FILLWINDOW);
	    ops2.put(EV3.LCX(0));     // COLOUR
	    ops2.put(EV3.LCX(0));     // Y0
	    ops2.put(EV3.LCX(0));     // Y1
	    ops2.put(EV3.opUI_Draw);
	    ops2.put(EV3.UPDATE);
	    myEV3.main(ops2);
	}
	
	@SuppressWarnings("unused")
	private static void testTimer1() {
		System.out.print("Attempting to draw things");
		System.out.println();
		EV3 myEV3 = new EV3();
		myEV3.sync_mode = EV3.ASYNC;
		myEV3.local = 4;
		ByteBuffer ops = ByteBuffer.allocateDirect(68);
		ops.put(EV3.opUI_Draw);
		ops.put(EV3.TOPLINE);
		ops.put(EV3.LCX(0));		// ENABLE
		ops.put(EV3.opUI_Draw);
		ops.put(EV3.FILLWINDOW);
		ops.put(EV3.LCX(0));        // COLOUR
		ops.put(EV3.LCX(0));        // Y0
		ops.put(EV3.LCX(0));        // Y1
		ops.put(EV3.opUI_Draw);
		ops.put(EV3.UPDATE);
		ops.put(EV3.opTimer_Wait);
		ops.put(EV3.LCX(1000));
		ops.put(EV3.LVX(0));
		ops.put(EV3.opTimer_Ready);
		ops.put(EV3.LVX(0));
	    ops.put(EV3.opUI_Draw);
	    ops.put(EV3.LINE);
	    ops.put(EV3.LCX(1));     	// COLOUR
	    ops.put(EV3.LCX(2));     	// X0
	    ops.put(EV3.LCX(125));     	// Y0
	    ops.put(EV3.LCX(88));     	// X1
	    ops.put(EV3.LCX(2));     	// Y1
	    ops.put(EV3.opUI_Draw);
	    ops.put(EV3.UPDATE);
	    ops.put(EV3.opTimer_Wait);
		ops.put(EV3.LCX(500));
		ops.put(EV3.LVX(0));
		ops.put(EV3.opTimer_Ready);
		ops.put(EV3.LVX(0));
	    ops.put(EV3.opUI_Draw);
	    ops.put(EV3.LINE);
	    ops.put(EV3.LCX(1));     	// COLOUR
	    ops.put(EV3.LCX(88));     	// X0
	    ops.put(EV3.LCX(2));     	// Y0
	    ops.put(EV3.LCX(175));     	// X1
	    ops.put(EV3.LCX(125));     	// Y1
	    ops.put(EV3.opUI_Draw);
	    ops.put(EV3.UPDATE);
	    ops.put(EV3.opTimer_Wait);
		ops.put(EV3.LCX(500));
		ops.put(EV3.LVX(0));
		ops.put(EV3.opTimer_Ready);
		ops.put(EV3.LVX(0));
		ops.put(EV3.opUI_Draw);
	    ops.put(EV3.LINE);
	    ops.put(EV3.LCX(1));     	// COLOUR
	    ops.put(EV3.LCX(175));     	// X0
	    ops.put(EV3.LCX(125));     	// Y0
	    ops.put(EV3.LCX(2));     	// X1
	    ops.put(EV3.LCX(125));     	// Y1
	    ops.put(EV3.opUI_Draw);
	    ops.put(EV3.UPDATE);
	    myEV3.main(ops);
	}
	
	@SuppressWarnings("unused")
	private static void testTimer2() throws InterruptedException {
		System.out.print("Attempting to draw things");
		System.out.println();
		EV3 myEV3 = new EV3();
		myEV3.local = 4;
		myEV3.sync_mode = EV3.ASYNC;
		ByteBuffer ops = ByteBuffer.allocateDirect(10);
		ops.put(EV3.opUI_Draw);
		ops.put(EV3.TOPLINE);
		ops.put(EV3.LCX(0));		// ENABLE
		ops.put(EV3.opUI_Draw);
		ops.put(EV3.FILLWINDOW);
		ops.put(EV3.LCX(0));        // COLOUR
		ops.put(EV3.LCX(0));        // Y0
		ops.put(EV3.LCX(0));        // Y1
		ops.put(EV3.opUI_Draw);
		ops.put(EV3.UPDATE);
		myEV3.main(ops);
		Thread.sleep(1000);
		ByteBuffer ops2 = ByteBuffer.allocateDirect(11);
		ops2.put(EV3.opUI_Draw);
	    ops2.put(EV3.LINE);
	    ops2.put(EV3.LCX(1));     	// COLOUR
	    ops2.put(EV3.LCX(2));     	// X0
	    ops2.put(EV3.LCX(125));     // Y0
	    ops2.put(EV3.LCX(88));     	// X1
	    ops2.put(EV3.LCX(2));     	// Y1
	    ops2.put(EV3.opUI_Draw);
	    ops2.put(EV3.UPDATE);
	    myEV3.main(ops2);
	    Thread.sleep(500);
	    ByteBuffer ops3 = ByteBuffer.allocateDirect(13);
		ops3.put(EV3.opUI_Draw);
	    ops3.put(EV3.LINE);
	    ops3.put(EV3.LCX(1));     	// COLOUR
	    ops3.put(EV3.LCX(88));     	// X0
	    ops3.put(EV3.LCX(2));     	// Y0
	    ops3.put(EV3.LCX(175));     // X1
	    ops3.put(EV3.LCX(125));     // Y1
	    ops3.put(EV3.opUI_Draw);
	    ops3.put(EV3.UPDATE);
	    myEV3.main(ops3);
	    Thread.sleep(500);
	    ByteBuffer ops4 = ByteBuffer.allocateDirect(13);
		ops4.put(EV3.opUI_Draw);
	    ops4.put(EV3.LINE);
	    ops4.put(EV3.LCX(1));     	// COLOUR
	    ops4.put(EV3.LCX(175));     // X0
	    ops4.put(EV3.LCX(125));     // Y0
	    ops4.put(EV3.LCX(2));     	// X1
	    ops4.put(EV3.LCX(125));     // Y1
	    ops4.put(EV3.opUI_Draw);
	    ops4.put(EV3.UPDATE);
	    myEV3.main(ops4);    
	}
	
	@SuppressWarnings("unused")
	private static void testRun() {
		System.out.print("Attempting to start program");
		System.out.println();
		EV3 myEV3 = new EV3();
		myEV3.sync_mode = EV3.ASYNC;
		myEV3.local = 8;
		ByteBuffer ops = ByteBuffer.allocateDirect(51);
		ops.put(EV3.opFile);
		ops.put(EV3.LOAD_IMAGE);
		ops.put(EV3.LCX(1));		// SLOT
		ops.put(EV3.LCS("../apps/Motor Control/Motor Control.rbf")); //NAME
		ops.put(EV3.LVX(0));		// SIZE
		ops.put(EV3.LVX(4));        // IP*
		ops.put(EV3.opProgram_Start);        // Y0
		ops.put(EV3.LCX(1));        // SLOT
		ops.put(EV3.LVX(0));		// SIZE
		ops.put(EV3.LVX(4));        // IP*
		ops.put(EV3.LCX(0));        // DEBUG	
		myEV3.main(ops);
	}	
	
	@SuppressWarnings("unused")
	private static void testButton() {
		EV3 myEV3 = new EV3();
		myEV3.sync_mode = EV3.SYNC;
		myEV3.buttonWait = true;
		myEV3.pressButton(EV3.BACK_BUTTON);
		myEV3.pressButton(EV3.RIGHT_BUTTON);
		myEV3.sync_mode = EV3.ASYNC;
		myEV3.pressButton(EV3.ENTER_BUTTON);
	}	
	
	@SuppressWarnings("unused")
	private static void testMotor() throws InterruptedException {
		EV3 myEV3 = new EV3();
		myEV3.sync_mode = EV3.ASYNC;
		ByteBuffer ops = ByteBuffer.allocateDirect(8);
		//Start all at full blast
		ops.put(EV3.opOutput_Power);
		ops.put(EV3.LCX(0));		// LAYER
		ops.put(EV3.LCX(15)); 		// NOS ALL MOTORS
		ops.put(EV3.LCX(100));		// POWER
		ops.put(EV3.opOutput_Start);        
		ops.put(EV3.LCX(0));        // LAYER
		ops.put(EV3.LCX(15)); 		// NOS ALL MOTORS
		myEV3.main(ops);
		Thread.sleep(2000);
		ByteBuffer ops2 = ByteBuffer.allocateDirect(4);
		//Stop all
		ops2.put(EV3.opOutput_Stop);
		ops2.put(EV3.LCX(0));		// LAYER
		ops2.put(EV3.LCX(15)); 		// NOS ALL MOTORS
		ops2.put(EV3.LCX(0));		// FLOAT BRAKE
		myEV3.main(ops2);
	}	
}
