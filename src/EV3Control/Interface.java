package EV3Control;

public class Interface {
	public static void main(String[] args) {
		System.out.printf("testInterface");
		System.out.println();
		EV3 myEV3 = new EV3();
		myEV3.main();
		System.out.printf("I made an object without verbosity!");
		System.out.println();
		myEV3.verbosity = true;
		myEV3.main();
		System.out.printf("I made verbosity true");
		System.out.println();
		myEV3.verbosity = false;
		myEV3.main();
		System.out.printf("I made verbosity false");
		System.out.println();
	}

}
