package engine;

import gui.ErrorDialog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JFrame;
import javax.swing.SwingWorker;

import core.Main;

public class ErrorSender extends SwingWorker<Void, Void>
{
	Exception e;
	JFrame parent;
	boolean exit;

	public ErrorSender(JFrame ancestor, Exception error, boolean quit)
	{
		e = error;
		parent = ancestor;
		exit = quit;
	}

	@Override
	protected Void doInBackground() throws Exception
	{		
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication("plab.sender","passrandom");
			}
		});

		StringWriter sw = new  StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String error = sw.toString();

		Message message = new MimeMessage(session);

		try
		{
			message.setFrom(new InternetAddress("plab.sender@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("plab.reciever@gmail.com"));
			message.setSubject("Error report");

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(error);

			Multipart multipart = new MimeMultipart(); 
			multipart.addBodyPart(messageBodyPart);

			message.setContent(multipart);
			
			Transport.send(message);
		}	

		catch (MessagingException e1)
		{
			new ErrorDialog(parent, Main.lang.getString("errorErrorReportTitle"), Main.lang.getString("errorErrorReportMessage"), exit);
		}	
		return null;
	}
}
