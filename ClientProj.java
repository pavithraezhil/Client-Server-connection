import java.lang.Math.*;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

public class ClientProj
{
	public static void main(String[] args) throws IOException
	{
		//initializing variables
		boolean count=true;
		int step4=4,step5=5;

			int sum=0,sumCheck11=0,counter=0;
			System.out.println("Enter a measurement ID from the database.");
		    Scanner measureID=new Scanner(System.in); // Input measurement ID from user
  		    int measurementID1=measureID.nextInt();
  		    while(step5==5)
  		    {
		while(step4==4)
		{
		int reqID=(int)(Math.random()*1000); // Generate a random request ID for tracking communication
        int measurementID=measurementID1;
		String stringRequest=("<request><id>  "+reqID+"</id>"+"<measurement>"+measurementID+"</measurement>"+"</request>");
		String str = stringRequest.replaceAll("\\s","");
		String value=null;
		sum=integrityCheck(str); //calculate integrity check
		String sum1=Integer.toString(sum);
		String str1=str+sum1; //append integrity check to string
		System.out.println("The request to be sent to server is:");
		System.out.println(str1);
		byte[] b1 = str1.getBytes(); //convert the string to byte array

            DatagramSocket ds = new DatagramSocket();
			InetAddress ip = InetAddress.getLocalHost(); //Getting host IP address
			DatagramPacket request = new DatagramPacket(b1, b1.length, ip, 9999); 
			
			ds.send(request); //Send the request to server
	
//timeout and receiving data
			{
         	int t =0;        //Initial timeout of 1s
			ds.setSoTimeout(t); //Timeout for the receiving packet
			
			byte[] buffer=new byte[1000]; //Initializing buffer byte array
        	DatagramPacket request2= new DatagramPacket(buffer, buffer.length);
        	   
                 while(count)
                 {
            	try {
            		 count=false;
        	        ds.receive(request2); // Receive response from server   
        	        } 
            	catch (SocketTimeoutException e)
            	{
        	    ds.send(request);
        	    count=true;
        	    t=t*2;       //Doubling the timeout if the message isn't received after t
        	    counter++;
        	    if(counter>3)
        	    {
              		System.out.println("Error! Connection failure.");  //after 4 re-transmissions, connection fails and the error message is displayed
        	        System.exit(0);
        	    }
                }
            	t=1000;     
			}
                 String conver=new String(buffer);
                 System.out.println("The response string received from server is: ");
                 System.out.println(conver.trim()); //to display string response
                 conver=conver.trim();       //to remove extra characters from the string
                 int indexSum=conver.lastIndexOf('>'); 
                 String stringChar1=conver.substring(0,indexSum+1);
               String[] parts=conver.split("><");
               String part1=parts[2];
               String[] part2=part1.split("</");
               String part3=part2[0];
               String[] part4=part3.split(">");
               String part5=part4[1];
               String respCode=part5; // response code extracted from the response string
               
  	        	sumCheck11=integrityCheck(stringChar1); //integrityCheck function is called
  	        	String sum11=conver.substring(indexSum+1,conver.length()); //integrity check value extracted from string
  	        	String sum2=Integer.toString(sumCheck11);
  	        	if(sum11.equals(sum2)) //if the checksum matches
  	        	{
  	        		switch(respCode)
  	        		{
  	        		case "0": //This is the case where the response was succesfully generated and received
  	        			 String[] par=conver.split("><");	
  	       			   String par1=par[4];
  	       			   String[] par0=par1.split("</");
  	       			   String par2=par0[0];
  	       		       String[] par3=par2.split(">");
  	       		       value=par3[1];
  	       		       System.out.println("The response message:");
  	          	System.out.println("<response>");
  	          	System.out.printf("\t<id> "+reqID+"</id>");
  	          	System.out.print("\n");
  	          	System.out.println("\t<code>"+respCode+"</code>");
  	          	System.out.printf("\t<measurement> "+measurementID+" </measurement>\n");
  	          	System.out.println("\t<value>"+value+"</value>");
  	          	System.out.println("</response>");
  	          	System.out.println(sum11);
  	          	System.exit(0);
  	          	break;
  	        		
  	        	case "1": //case of integrity check error
  	        		System.out.println("Error. There is an error in integrity checking of bits. Enter '1' if you would like to send the request again. Press any other key to exit.");
  	        	    Scanner userInput=new Scanner(System.in);
	        		int input=userInput.nextInt();
	        		if(input==1)
  	        		{
  	        			step4=4; //in order to re-transmit the message
  	        		}
  	        		else
  	        			System.exit(0);
  	        	    break;
  	        	case "2": //case of wrong syntax
  	        		System.out.println("Error. Malformed request.");
  	        	          step4=0;
  	        		      break;
  	        	case "3": //case of invalid measurement ID
  	        		System.out.println("Error. The measurement ID is invalid.");
  	        	         step4=0;
  	        	default: step4=0;
  	        		     break;
  	        	}	
  	        	step5=0;	
  	        	}
  	        	else
  	        	{
  	        		step5=5; //in order to send request again because of failed checksum of response at client
  	        	}
			}
			
			}// end of while checking for step4
		}// end of while checking for step5
	}   		
	
public static int integrityCheck(String str1)
{
	int C=7919;
	int D=65536;
	int S=0;
	int index=0;
	String str=str1;
	int[]ascii=new int[str.length()];
	int asciword[]=new int[str.length()/2+str.length()%2]; //final array of 16 bit words
	int k=0;
	for(int i=0;i<str.length();i++)
	{
		ascii[i]=(int)str.charAt(i); //convert to ascii values
	}
	if(str.length()%2==0) //if length of the string is even
	for(int j=0;j<ascii.length-1;j++)
	{
		asciword[k]=(ascii[j]<<8)|(ascii[j+1]); //shift by 8 bits and add the adjacent byte bitwise
		j++;
		k++;
	}
	else if(str.length()%2!=0) //if the length of string is odd
	{
		for(int j=0;j<ascii.length-2;j++)
		{
			asciword[k]=(ascii[j]<<8)|(ascii[j+1]);
			j++;
			k++;
		}
	asciword[k]=(ascii[ascii.length-1]<<8)|0; //the last byte is considered as 0 and added
	}
		
	for(int m=0;m<asciword.length;m++) //algorithm for calculating checksum
	{
		index=S^(asciword[m]);
		S=(C*index)%D;
	}
return S;
} //end of main
}// end of class

