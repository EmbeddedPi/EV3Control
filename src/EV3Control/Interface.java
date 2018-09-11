package EV3Control;

public class Interface {
	public static void main(String args[]) {
		System.out.printf("testInterface");
		System.out.println();
		EV3 myEV3 = new EV3();
		//byte ops = EV3.opNop;
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
	}

}
