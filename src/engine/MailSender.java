package engine;

import gui.ErrorDialog;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
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
import javax.swing.SwingWorker;

import core.Main;

public class MailSender extends SwingWorker<Void, Void>
{
	public boolean failed = false;
	
	String fileName;
	String subject;
	
	public MailSender(String title, String classSubject)
	{
		subject = classSubject;
		fileName = title;
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

		try
		{
			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress("plab.sender@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("plab.reciever@gmail.com"));
			message.setSubject(subject);

		    MimeBodyPart messageBodyPart = new MimeBodyPart();
		    messageBodyPart.setText("Nova skripta je stigla!");

		    Multipart multipart = new MimeMultipart(); 
		    multipart.addBodyPart(messageBodyPart);

		    messageBodyPart = new MimeBodyPart();
		    
		    File attachment = Save.readableSaveToFile(fileName);
		    DataSource source = new FileDataSource(attachment);
		    messageBodyPart.setDataHandler(new DataHandler(source));
		    messageBodyPart.setFileName(attachment.getName());
		    
		    multipart.addBodyPart(messageBodyPart);
		    
		    message.setContent(multipart);	
		    
		    Transport.send(message);
			attachment.delete();
		}
		
		catch (MessagingException e)
		{
			failed = true;
			new ErrorDialog(Main.masterWindow, Main.lang.getString("errorSendingDocumentTitle"), Main.lang.getString("errorSendingDocumentMessage"), false);
		}
		
		return null;
	}
}
