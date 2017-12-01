package rnd;

import java.io.BufferedReader;
import java.net.*;
import java.io.FileReader;

public class Server {
	public static void main(String[] args) throws Exception
	{ 
		//initialization
		int sum=0;
        int index2=0;	
		int respCode=0;
		String reqID;
		String temp1=" ";
		String responseString=null;
		String measurementid=" ";
		String fileName = "data.txt"; //data text 
		DatagramPacket request;
		try
		{
		String[] reference={"<request>","<id>","</id>","<measurement>","</measurement>","</request>"};
		int syntax=0;
		int sumCheck1=0;
		int index=0;
		String line;
		DatagramSocket ds = new DatagramSocket(9999);  
        byte[] receiveData = new byte[100]; 
       String integCheck1=null;
       System.out.println("Receiving the request from the client...");
       request = new DatagramPacket(receiveData, receiveData.length);  
                       ds.receive(request);
                       ds.close();
                       String m1 = new String(request.getData());
                       String m21=m1.trim();
                       String m = m21.replaceAll("\\s","");
                       System.out.println("Request received fom the client is:");
           	           System.out.println(m); //displaying request received from client
           	     String[] parts12=m.split("><");
           		String part11=parts12[1];
           		String[] part123=part11.split(">");
           		String[] part3=part123[1].split("</");
           		reqID=part3[0];    //extracting the request ID from the request string
  //Integrity check
           	        	 index=m.lastIndexOf('>');
           	        	String stringChar=m.substring(0,index+1);
           	        	sumCheck1=integrityCheck(stringChar); //calling the function integrityCheck
           	        	String sum1=m.substring(index+1,m.length()); //extracting the checksum value from the string
           	        	String sum2=Integer.toString(sumCheck1);
         if(!sum1.equals(sum2)) //checking if integrity check fails
         {
           	    respCode=1;
         }
           	    else
           	       if(sum1.equals(sum2)) //if integrity check passes
           	       {   
     //checking for syntax      	    	   
           	    	   String m2 = m.replaceAll("\\d","");
           	        	int initial=0;
           	        	for(int i=0;i<reference.length;i++)
           	        	{
           	        		String stringCheck=m2.substring(initial,initial+reference[i].length());
           	        		if(reference[i].equals(stringCheck)) //if syntax is same against a reference string
           	        		{
           	        			syntax++;
           	        		}
           	        		else
           	        		{
           	        			syntax=0;
           	        		}
           	        		
           	        		initial=initial+reference[i].length();
           	        	}
           	        	if(syntax!=reference.length) //if syntax is not the same
           	        	{	
           	        		respCode=2;
           	        	     
           	        	}
           	        	else if(syntax==reference.length)// if syntax check passes
           	        	{       	        	
         	        
  // measurementID, temperature
           	 		FileReader fileReader = 
           	 	            new FileReader(fileName);
           	 		 BufferedReader bufferedReader = 
           	 	             new BufferedReader(fileReader);
           	 	         String[] textData=new String[1000];
           	 	     index2=m.indexOf('>');
     	        	String[] parts=m.split("><");
     	 		    String part1=parts[2];
     	 		    String[] part=part1.split("<");
     	 		    String[] part2=part[0].split(">");
     	 	        measurementid=part2[1]; //extracting measurement id
     	 	        
     	 	     int i=0;
     	 	     //to read the data.txt file
     	 	     while((line = bufferedReader.readLine()) != null) 
             {
                  	textData[i]=line.trim();
                  	String[] splitData=textData[i].split("\\s");
                   	if(measurementid.equals(splitData[0])) //checking against the id
                   	{
                   		respCode=0;
                   		temp1=splitData[1]; //returning the corresponding temperature value
                   		System.out.println("Temperature is: "+temp1);
                   		break;
                   	}
                   	else
                   	{
                   		i++;
                   	}
                   	respCode=3;
             }
     	 	   bufferedReader.close();
           	      }
             }  
	if(respCode==0) //if all conditions satisfied
	{
 responseString=("<response><id>"+reqID+"</id>"+"<code>"+respCode+"</code>"+"<measurement>"+measurementid+"</measurement>"+"<value>"+temp1+"</value>"+"</response>");
	}
	else //if error is encountered
	{
		 responseString=("<response><id>"+reqID+"</id>"+"<code>"+respCode+"</code>"+"</response>");
	}
    System.out.println("The response string to be sent to client is:");
	
sum=integrityCheck(responseString);
sum1=Integer.toString(sum);
String str1=responseString+sum1;
System.out.println(responseString); //response string that is to be sent to the client 
DatagramSocket ds1 = new DatagramSocket(9999); //to send the response to the client
InetAddress ip = InetAddress.getLocalHost();
byte[] b2 = str1.getBytes();
System.out.println("Sending the response to the client...");
DatagramPacket reply=new DatagramPacket(b2,b2.length,request.getAddress(),request.getPort());
ds1.send(reply);
}// end of try block
	catch(Exception e)
	{
		System.out.println("Error");
	}
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
}	
}

