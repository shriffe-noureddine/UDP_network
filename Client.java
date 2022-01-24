import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client
{
	public static void main(String args[]) throws IOException
	{
	    Scanner User = new Scanner(System.in);
		boolean exit = false;

		int count = 1 ;
		DatagramSocket DataSocket = new DatagramSocket();
		DataSocket.setSoTimeout(0);
		// creating InetAddress object to get an IP out of it
		InetAddress IPE = InetAddress.getLocalHost();
		byte window_to_send[] = new byte[65535];
		// byte arrays to get help in sending message and recieving acknowledgments
		byte[] MessageToRecieve = new byte[65535];
		//DP to recieve acknowledgments
		DatagramPacket DataPacketReceive = null;
		System.out.println("\nUDP Client Started . . . !!!\n");

		System.out.println("\nEnter Probability : ");
		double prob = User.nextDouble();
		
		System.out.println("\n1 - GO BACK N\n2 - Selective Repeat");
		int proto = User.nextInt();
		
		DatagramPacket PacketSent = new DatagramPacket("-----".getBytes(), "-----".getBytes().length, IPE,6666);
		DataSocket.send(PacketSent);
		
		DataPacketReceive = new DatagramPacket(MessageToRecieve, MessageToRecieve.length);
		DataSocket.receive(DataPacketReceive);
		String Repl = CombineBytes(MessageToRecieve).toString();		
		System.out.println("Status : "+Repl );
		
	while (true)	{
		
		if (exit)
		{
			while(exit)
			{
			Scanner scan = new Scanner(System.in); 		
			String asd = scan.nextLine();
			System.out.print("Your Session Is terminated . You Cannot Again . ");
			}
	
		}
		Scanner scn = new Scanner(System.in); 		
		System.out.print("\n1 - Put\n2 - Get\n3 - Quit\nEnter Your Choice : ");
		String choice = scn.nextLine();
	
		if (choice.equals("1"))
		{
			try {
				
				
				
			System.out.print("\nEnter Filename : ");
			String namefile = scn.nextLine();

			String message = "";
		      File filename = new File(namefile);
				boolean exist = filename.exists();
				if (exist)
				{	
					System.out.println("\nIn EXITS");
		      Scanner myReader = new Scanner(filename);
	
				String command = "PUT";
				window_to_send = command.getBytes();
				PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,6666);
				DataSocket.send(PacketSent);

				window_to_send = namefile.getBytes();
				PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,6666);
				DataSocket.send(PacketSent);

		      int i =0;
		      while (myReader.hasNextLine()) {
		    	  
		    	 // if (proto==1) // GO BACK N
		    	  
		    		  
		    	  
		    	  window_to_send = new byte[65535];
 		    	    //seq
		    	  	String Seq = Integer.toString(i);
					window_to_send = Seq.getBytes();
					PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,6666);
					DataSocket.send(PacketSent);

			        String data = myReader.nextLine();

					//len
		    	  	String len = Integer.toString(data.length());//Integer.toString(i);
					window_to_send = len.getBytes();
					PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,6666);
					DataSocket.send(PacketSent);

					//data

					window_to_send = data.getBytes();

					double Value = (window_to_send.length*prob);
					int value = (int) (window_to_send.length- Value);
	
					
					PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,6666);
					DataSocket.send(PacketSent);
					
					if ((i+1)%3==0)
					{
						System.out.println("Retransmitting . . . . " );
					}

					//ACK REcieving
					MessageToRecieve = new byte[65535];
					DataPacketReceive = new DatagramPacket(MessageToRecieve, MessageToRecieve.length);
					DataSocket.receive(DataPacketReceive);
					String ACK = CombineBytes(MessageToRecieve).toString();
					System.out.println("ACK Status : "+ACK );
					
		    	  
					while (ACK.equals("NACK"))
					{

				    	 // if (proto==2) 	//  Selective Repeat
						// timeout and retransmission // faulty packet recieved						
						//	Sending Messages Again Back to Server						
						// if not acknowledge from server so requesting again that packet
				    	  window_to_send = new byte[65535];
		 		    	    //seq
				    	  	Seq = Integer.toString(i);
							window_to_send = Seq.getBytes();
							PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,6666);
							DataSocket.send(PacketSent);

					        data = myReader.nextLine();

							//len
				    	  	len = Integer.toString(data.length());//Integer.toString(i);
							window_to_send = len.getBytes();
							PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,6666);
							DataSocket.send(PacketSent);

							//data

							window_to_send = data.getBytes();
							PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,6666);
							DataSocket.send(PacketSent);
							
							//ACK REcieving
							MessageToRecieve = new byte[65535];
							DataPacketReceive = new DatagramPacket(MessageToRecieve, MessageToRecieve.length);
							DataSocket.receive(DataPacketReceive);
							ACK = CombineBytes(MessageToRecieve).toString();
							
							System.out.println("ACK Status : "+ACK );						
					}
			        i=i+1;
		      }
		      myReader.close();
		      
				window_to_send = "Done".getBytes();
				PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,6666);
				DataSocket.send(PacketSent);

				System.out.println("\nFile Sent with title server_"+namefile+"\n");
			}
				else
			{
				System.out.println("\nFILE NOT AVAILABLE");
			}
			}
			catch (Exception e )
			{
				System.out.print("\nClient has no file found\n");
				window_to_send = "Client has no file".getBytes();
				PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,6666);
				DataSocket.send(PacketSent);
			}

			
		}
		else if (choice.equals("2")) 
		{
			MessageToRecieve = new byte[65535];
			window_to_send = new byte[65535];
			String command = "GET";

			System.out.print("\nEnter Filename : ");
			String namefile = scn.nextLine();

			// sending command
			window_to_send = command.getBytes();
			PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,6666);
			DataSocket.send(PacketSent);


			// filename sending
			window_to_send = namefile.getBytes();
			PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,6666);
			DataSocket.send(PacketSent);

			DataPacketReceive = new DatagramPacket(MessageToRecieve, MessageToRecieve.length);
			DataSocket.receive(DataPacketReceive);
			String message = CombineBytes(MessageToRecieve).toString();

			System.out.print("\nFound Status :  "+message+"\n");

			if (message.equals("Not_Found"))
			{
				System.out.print("\nServer has no "+namefile+" file\n");
				
			}
			else if (message.equals("Found"))
			{		

				String file = "client_"+namefile;
				PrintWriter write_file = new PrintWriter(file); 
				
				int i=0;
				String line = "";
			while (true)
				{
				MessageToRecieve = new byte[65535];
				window_to_send = new byte[65535];
				// seq
				DataPacketReceive = new DatagramPacket(MessageToRecieve, MessageToRecieve.length);
				DataSocket.receive(DataPacketReceive);
				String Seq  = CombineBytes(MessageToRecieve).toString();
				
				if (Seq.equals("Done"))
				{
					break;
				}
				
				//len
				DataPacketReceive = new DatagramPacket(MessageToRecieve, MessageToRecieve.length);
				DataSocket.receive(DataPacketReceive);
				String  len = CombineBytes(MessageToRecieve).toString();
				
				//data
				DataPacketReceive = new DatagramPacket(MessageToRecieve, MessageToRecieve.length);
				DataSocket.receive(DataPacketReceive);
				line  = CombineBytes(MessageToRecieve).toString();
				System.out.println(" Data Line : "+ line);
				
				String ACK = "----PACKET ACK Recieved for SEQ "+Seq+" LEN : "+len;
		        
		        if (line.length()==Integer.parseInt(len) && Integer.toString(i).equals(Seq))
		        {		        	
		        window_to_send = ACK.getBytes();
				PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,6666);
				DataSocket.send(PacketSent);
				System.out.println("ACK Status  : "+ "----PACKET ACK SENT for SEQ "+Seq+" LEN : "+len);
		        write_file.println(line); 				
		        }
		        else
		        {
					ACK = "NACK";
			        window_to_send = ACK.getBytes();
					PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,6666);
					DataSocket.send(PacketSent);
					System.out.println("ACK Status  : "+ACK);															        			        			        	
		        }
		        i=i+1;
			}
		        write_file.close();
		        System.out.print("\nFile Recieved\n");
			}
		}
		else if (choice.equals("3")) 
		{
			System.out.print("\nExiting Program . . . \n");
			exit = true;
		}
		else 
		{
			System.out.print("\nInvalid Command\n");
		}
	
		MessageToRecieve = new byte[65535];
		window_to_send = new byte[65535];
	}
	}
	// StringBuilder function to get bytes combination
	public static StringBuilder CombineBytes(byte[] a)
	{
		if (a == null)
			return null;
		StringBuilder String_Return = new StringBuilder();
		int i = 0;
		while (a[i] != 0)
		{
			String_Return.append((char) a[i]);
			i++;
		}
		return String_Return;
	}
}
