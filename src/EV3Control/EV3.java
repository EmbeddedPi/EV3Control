package EV3Control;
	
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ByteOrder;

public class EV3 {
	static final short ID_VENDOR_LEGO = (short) 0x0694;
	static final short ID_PRODUCT_EV3 = (short) 0x0005;
	static final byte  EP_IN          = (byte)  0x81;
	static final byte  EP_OUT         = (byte)  0x01;

	static final byte  opNop                  	= (byte)  0x01;
	static final byte  DIRECT_COMMAND_REPLY     = (byte)  0x00;
	static final byte  DIRECT_COMMAND_NO_REPLY  = (byte)  0x80;
	static final byte  STD 						= (byte)  0x00;
	static final byte  SYNC 					= (byte)  0x01;
	static final byte  ASYNC 					= (byte)  0x02;

	static DeviceHandle handle;
	static private short counter = 41;
	Boolean verbosity = true;
	int local = 0;
	int global =0;
	byte sync_mode = SYNC;

	public static void connectUsb () {
		int result = LibUsb.init(null);
		Device device = null;
		DeviceList list = new DeviceList();
		result = LibUsb.getDeviceList(null, list);
		if (result < 0){
			throw new RuntimeException("Unable to get device list. Result=" + result);
		}
		boolean found = false;
		for (Device dev: list) {
			DeviceDescriptor descriptor = new DeviceDescriptor();
			result = LibUsb.getDeviceDescriptor(dev, descriptor);
			if (result != LibUsb.SUCCESS) {
				throw new LibUsbException("Unable to read device descriptor", result);
			}
			if (  descriptor.idVendor()  == ID_VENDOR_LEGO
			|| descriptor.idProduct() == ID_PRODUCT_EV3) {
				device = dev;
				found = true;
				break;
			}
		}
		LibUsb.freeDeviceList(list, true);
		if (! found) throw new RuntimeException("Lego EV3 device not found.");
		
		handle = new DeviceHandle();
		result = LibUsb.open(device, handle);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to open USB device", result);
		}
		boolean detach = LibUsb.kernelDriverActive(handle, 0) != 0;

		if (detach) result = LibUsb.detachKernelDriver(handle, 0);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to detach kernel driver", result);
		}

		result = LibUsb.claimInterface(handle, 0);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to claim interface", result);
		}
	}

	/* Original combined sendDirectCmd method
	public static ByteBuffer sendDirectCmd (ByteBuffer operations,
	int local_mem, int global_mem, Boolean verb) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(operations.position() + 7);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putShort((short) (operations.position() + 5));   // length
		buffer.putShort((short) 42);                            // counter
		buffer.put(DIRECT_COMMAND_REPLY);                       // type
		buffer.putShort((short) (local_mem*1024 + global_mem)); // header
		for (int i=0; i < operations.position(); i++) {         // operations
			buffer.put(operations.get(i));
		}

		IntBuffer transferred = IntBuffer.allocate(1);
		int result = LibUsb.bulkTransfer(handle, EP_OUT, buffer, transferred, 100); 
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to write data", transferred.get(0));
		}
		if (verb) {
			printHex("Sent", buffer);
		} else {
			System.out.print("Suppressing sent message");
			System.out.println();
		}
		
		//TODO Split off here for wait_for_reply method
		buffer = ByteBuffer.allocateDirect(1024);
		transferred = IntBuffer.allocate(1);
		result = LibUsb.bulkTransfer(handle, EP_IN, buffer, transferred, 100);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to read data", result);
		}
		buffer.position(global_mem + 5);
		if (verb) {
			printHex("Recv", buffer);
		} else {
			System.out.print("Suppressing recv message");
			System.out.println();
		}
		return buffer;
	}
	*/
	
	public short sendDirectCmd (ByteBuffer operations,int local_mem, int global_mem) {
		counter++;
		ByteBuffer buffer = ByteBuffer.allocateDirect(operations.position() + 7);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putShort((short) (operations.position() + 5));   // length
		buffer.putShort((short) counter);                       // counter
		if (sync_mode == SYNC || global_mem > 0) {
			buffer.put(DIRECT_COMMAND_REPLY);  					// type
		} else {
			buffer.put(DIRECT_COMMAND_NO_REPLY); 				// type
		}
		buffer.putShort((short) (local_mem*1024 + global_mem)); // header
		for (int i=0; i < operations.position(); i++) {         // operations
			buffer.put(operations.get(i));	
			IntBuffer transferred = IntBuffer.allocate(1);
			int result = LibUsb.bulkTransfer(handle, EP_OUT, buffer, transferred, 100); 
			if (result != LibUsb.SUCCESS) {
				throw new LibUsbException("Unable to write data", transferred.get(0));
			}
			if (verbosity) {
				printHex("Sent", buffer);
			} else {
				System.out.print("Suppressing sent message");
				System.out.println();
			}
		}
		return counter;
	}			
	
	public ByteBuffer waitForReply (int global_mem, short counter) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
		IntBuffer transferred = IntBuffer.allocate(1);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int result = LibUsb.bulkTransfer(handle, EP_IN, buffer, transferred, 100);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to read data", result);
		}
		buffer.position(global_mem + 5);
		if (verbosity) {
			printHex("Recv", buffer);
		} else {
			System.out.print("Suppressing recv message");
			System.out.println();
		}
	return buffer;
	}

	public static void printHex(String desc, ByteBuffer buffer) {
		System.out.print(desc + " 0x|");
		for (int i= 0; i < buffer.position() - 1; i++) {
			System.out.printf("%02X:", buffer.get(i));
		}
		System.out.printf("%02X|", buffer.get(buffer.position() - 1));
		System.out.println();
	}

	//TODO Remove this after breaking returned reply down into properties
	//@SuppressWarnings("unused")
	public void main () {
		try {
			connectUsb();

			ByteBuffer operations = ByteBuffer.allocateDirect(1);
			operations.put(opNop);

			//Original call for combined sendDirectCmd method
			//ByteBuffer reply = sendDirectCmd(operations, local, global, verbosity);
			//Sends operation and returns counter for referencing
			short msg_count = sendDirectCmd(operations, local, global);
			//System.out.printf("Sent counter is " + msg_count);
			//System.out.println();
			//Gets reply based on message message count albeit not yet filtered as such
			if (sync_mode == SYNC || (sync_mode == STD && global > 0)) {
				ByteBuffer reply = waitForReply(global, msg_count);
				int returnedCounter =  reply.get(2);
				//System.out.printf("Returned counter is " + returnedCounter + "\n");
				while (returnedCounter != msg_count) {
					System.out.print("Reply rejected as returned counter is  " + returnedCounter + " and msg_count is " + msg_count + "\n");
					ByteBuffer unusedReply = waitForReply(global, msg_count);
					returnedCounter =  unusedReply.get(2);					
				}
				int received = 1019 - reply.remaining();
				System.out.printf("Received " + received + " integers in reply.");
				System.out.println();
			} else {
				System.out.printf("Not waiting for reply as either ASYNC or STD with global = 0");
				System.out.println();
			}
			//TODO Do stuff with the reply
			LibUsb.releaseInterface(handle, 0);
			LibUsb.close(handle);
		} catch (Exception e) {
	     e.printStackTrace(System.err);
		}
	}
}