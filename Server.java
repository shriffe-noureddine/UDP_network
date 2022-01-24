import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

public class Server
{
	public static void main(String[] args) throws IOException, InterruptedException
	{
	    Scanner User = new Scanner(System.in);
		ArrayList<Integer> Ports = new ArrayList<Integer>();
		int users= 1; 
		int count = 1;
		DatagramSocket DataSocket = new DatagramSocket(6666);
		DataSocket.setSoTimeout(0);
		byte window_to_send[] = new byte[65535];
		byte[] messageToRecieve = new byte[65535];
		InetAddress IPE = InetAddress.getLocalHost();
		DatagramPacket DataPacketReceive = null;
		System.out.println("\nEnter No Of Process/Players : ");
		int ALLOWED = User.nextInt();
		
		System.out.println("\nUDP Server Started . . . !!!\n");		
		while (true) 
		{
			DataPacketReceive = new DatagramPacket(messageToRecieve, messageToRecieve.length);
			DataSocket.receive(DataPacketReceive);
			StringBuilder msgrec = CombineBytes(messageToRecieve);
			System.out.println("\nNew Client Connected");	
			Ports.add(DataPacketReceive.getPort());
			if (users==ALLOWED)
			{
				for (int i = 0 ; i < ALLOWED ; i++ )
				{
					DatagramPacket PacketSent = new DatagramPacket("All Users Connected".getBytes(), "All Users Connected".getBytes().length, IPE,Ports.get(i));
					DataSocket.send(PacketSent);
					System.out.println("\nAll Users Connected");	
				}
				break;
			}
			users=users+1;
		}
		messageToRecieve = new byte[65535];
		while (true) 
		{ 
			try
			{	String command = ""; 
			DataPacketReceive = new DatagramPacket(messageToRecieve, messageToRecieve.length);
			DataSocket.receive(DataPacketReceive);
			StringBuilder msgrec = CombineBytes(messageToRecieve);
				command = msgrec.toString();

				System.out.println("Command Recieved : "+ command); 

				if(command.equals("PUT"))
				{ 
					DataPacketReceive = new DatagramPacket(messageToRecieve, messageToRecieve.length);
					DataSocket.receive(DataPacketReceive);
					String filename = CombineBytes(messageToRecieve).toString();
					
					System.out.println("Filename  : "+ filename);
					
				/*	DataPacketReceive = new DatagramPacket(messageToRecieve, messageToRecieve.length);
					DataSocket.receive(DataPacketReceive);
					String filedata = CombineBytes(messageToRecieve).toString();
*/
					
					String file = "server_"+filename;
					PrintWriter write_file = new PrintWriter(file); 

					int i=0;
					while(true)
					{
						messageToRecieve = new byte[65535];
						
						// Seq
						DataPacketReceive = new DatagramPacket(messageToRecieve, messageToRecieve.length);
						DataSocket.receive(DataPacketReceive);
						String Seq = CombineBytes(messageToRecieve).toString();
						
						if (Seq.equals("Done"))
						{
							break;
						}
						messageToRecieve = new byte[65535];

						// Len
						DataPacketReceive = new DatagramPacket(messageToRecieve, messageToRecieve.length);
						DataSocket.receive(DataPacketReceive);
						String len = CombineBytes(messageToRecieve).toString();

						messageToRecieve = new byte[65535];
						
						// DataLine
						DataPacketReceive = new DatagramPacket(messageToRecieve, messageToRecieve.length);
						DataSocket.receive(DataPacketReceive);
						String data = CombineBytes(messageToRecieve).toString();

						if (Integer.parseInt(len)==data.length() && Seq.equals(Integer.toString(i)))
						{
							window_to_send = new byte[65535];
							data = data+"\n";
							write_file.println(data); 
					        String ACK = "----PACKET ACK Recieved for SEQ "+Seq+" LEN : "+len; 
					        window_to_send = ACK.getBytes();
							DatagramPacket PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,DataPacketReceive.getPort());
							DataSocket.send(PacketSent);
							System.out.println("ACK Status  : ----PACKET ACK SENT for SEQ "+Seq+" LEN : "+len);							
						}
						else 
						{
						String ACK = "NACK";
				        window_to_send = ACK.getBytes();
						DatagramPacket PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,DataPacketReceive.getPort());
						DataSocket.send(PacketSent);
						System.out.println("ACK Status  : "+ACK);													
						}
			        i=i+1;
					}
			        			        
			        write_file.close();
			        System.out.println("\nFile Recieved");
				}
				else if (command.equals("GET"))
				{
				messageToRecieve = new byte[65535];
				DataPacketReceive = new DatagramPacket(messageToRecieve, messageToRecieve.length);
				DataSocket.receive(DataPacketReceive);
				String filename = CombineBytes(messageToRecieve).toString();

					System.out.println("Filename  : "+ filename);
					
					try {
						String message = "";
					      File file = new File(filename);
					  	boolean exist = file.exists();
						if (exist)
						{	
					      Scanner myReader = new Scanner(file);
							window_to_send = new byte[65535];
					        window_to_send = "Found".getBytes();
							DatagramPacket PacketSentt = new DatagramPacket(window_to_send, window_to_send.length, IPE,DataPacketReceive.getPort());
							DataSocket.send(PacketSentt);

					      
					      int i=0;
					      while (myReader.hasNextLine()) {
					    	  
						    	 // if (proto==1) // GO BACK N
						    	 // if (proto==1) // GO BACK N
					    	 	messageToRecieve = new byte[65535];
								window_to_send = new byte[65535];
								
					    	  String Seq = Integer.toString(i);
					    	  
						       window_to_send = Seq.getBytes();
								DatagramPacket PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,DataPacketReceive.getPort());
								DataSocket.send(PacketSent);
								
					        String data = myReader.nextLine();					        
					        System.out.print("\nLeght Of MessageLine is : "+data.length());
					        
					        String len = Integer.toString(data.length());
					        window_to_send = len.getBytes();
							PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,DataPacketReceive.getPort());
							DataSocket.send(PacketSent);
							
							window_to_send = data.getBytes();
							PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,DataPacketReceive.getPort());
							DataSocket.send(PacketSent);
							
							DataPacketReceive = new DatagramPacket(messageToRecieve, messageToRecieve.length);
							DataSocket.receive(DataPacketReceive);
							String ACK = CombineBytes(messageToRecieve).toString();
							System.out.println(" Data Line : "+ data);
							System.out.println(ACK);
							
							
							while (ACK.equals("NACK"))
							{
								//  Selective Repeat
							// timeout and retransmission // faulty packet recieved
					    	messageToRecieve = new byte[65535];
					    	window_to_send = new byte[65535];
								
					    	Seq = Integer.toString(i);
					    	  
						    window_to_send = Seq.getBytes();
							PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,DataPacketReceive.getPort());
							DataSocket.send(PacketSent);
								
					        data = myReader.nextLine();					        
					        System.out.print("\nLeght Of MessageLine is : "+data.length());
					        
					        len = Integer.toString(data.length());
					        window_to_send = len.getBytes();
							PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,DataPacketReceive.getPort());
							DataSocket.send(PacketSent);
							
							window_to_send = data.getBytes();
							PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,DataPacketReceive.getPort());
							DataSocket.send(PacketSent);
							
							DataPacketReceive = new DatagramPacket(messageToRecieve, messageToRecieve.length);
							DataSocket.receive(DataPacketReceive);
							ACK = CombineBytes(messageToRecieve).toString();
							System.out.println(" Data Line : "+ data);
							System.out.println(ACK);
								
							}
							

					        i=i+1;					      					   
					      }
					      myReader.close();
					      
							window_to_send = "Done".getBytes();
							DatagramPacket PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,DataPacketReceive.getPort());
							DataSocket.send(PacketSent);
					      
							System.out.println("File Sent with title client_"+filename+"\n");
						}
						else
						{
							System.out.print("\nServer has no "+filename+" found\n");
							String msg = "Not_Found";
							window_to_send = msg.getBytes();
							DatagramPacket PacketSent = new DatagramPacket(window_to_send, window_to_send.length, IPE,DataPacketReceive.getPort());
							DataSocket.send(PacketSent);	
						}
						}
					
						catch (Exception f)
						{

						}
				}
				else 
				{
					System.out.println("\nClient has no file\n"); 
				}
			}
			catch (IOException e) 
			{ 
				
				e.printStackTrace(); 
			} 
			messageToRecieve = new byte[65535];
			window_to_send = new byte[65535];
		}
	}
	// function to combine bytes recieved from the client
	public static StringBuilder CombineBytes(byte[] a)
	{
		if (a == null)
			return null;
		StringBuilder MessageReturn = new StringBuilder();
		int i = 0;
		while (a[i] != 0)
		{
			MessageReturn.append((char) a[i]);
			i++;
		}
		return MessageReturn;
	}
}