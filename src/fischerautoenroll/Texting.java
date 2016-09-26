package fischerautoenroll;

import java.util.Properties;
import java.util.Scanner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

public class Texting
{
	public static String domain, smsEmail;
	public static long number;
	
	public static void setUpSMS()
	{
		System.out.println("Which mobile carrier do you use? Enter a number:\n0: AT&T\n1: Sprint\n2:T-Mobile\n3: Verizon");
		Scanner s = new Scanner(System.in);
		boolean hasCarrier = false;
		while (hasCarrier == false)
		{
			String line = s.nextLine();
			if (line.equals("0"))
			{
				domain = "txt.att.net";
				hasCarrier = true;
			} else if (line.equals("1"))
			{
				domain = "pm.sprint.com";
				hasCarrier = true;
			} else if (line.equals("2"))
			{
				domain = "tmomail.net";
				hasCarrier = true;
			} else if (line.equals("3"))
			{
				domain = "vtext.com";
				hasCarrier = true;
			} else
			{
				System.out.println("Error: please enter a number from 0-3.");
			}
		}
		boolean hasNumber = false;
		while (hasNumber == false)
		{
			System.out.println("Please enter your phone number (no dashes, spaces, nor parentheses): ");
			String line = s.nextLine();
			try
			{
				number = Long.parseLong(line);
				hasNumber = true;
			} catch (NumberFormatException e)
			{
				System.out.println("Error: please enter a valid number.");
				e.printStackTrace();
			}
		}
		smsEmail = number + "@" + domain;
		s.close();
	}
	
	public static void sendText(String subject, String message)
	{
		Properties props = new Properties();
		props.put("mail.smtp.host", "localhost");
		Session session = Session.getInstance(props);
		MimeMessage msg = new MimeMessage(session);
		try
		{
			msg.setRecipients(Message.RecipientType.TO, number + "@" + domain);
			msg.setSubject(subject);
			msg.setText(message);
			Transport.send(msg);
		} catch (MessagingException e)
		{
			System.out.println("Error: unable to send message.");
			e.printStackTrace();
		}
	}
	
	public static void main(String...args)
	{
		setUpSMS();
		sendText("test subeject", "I wonder if this will send");
	}
}
