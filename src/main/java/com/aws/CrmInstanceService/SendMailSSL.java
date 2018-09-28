package com.aws.CrmInstanceService;

import com.aws.CrmInstanceService.bean.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class SendMailSSL {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private ConfigProperties configProperties;

    public boolean sendEmail(String toEmail,String body) {
        boolean mailStatus = false;

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(configProperties.getFrommailid(),configProperties.getPassword());
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(configProperties.getFrommailid()));
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(toEmail));
            message.setSubject("Welcome to CRM !! Fulfillment request");
            message.setContent(body,"text/html");

            Transport.send(message);
            mailStatus = true;
            logger.info("Email sent");

        } catch (MessagingException e) {
            mailStatus = false;
            logger.error("Error in Sendin email - " + e );
        }
        catch(Exception e){
            mailStatus = false;
            logger.error("Error in Sendin email - " + e );
        }
        finally {
        }
        return mailStatus;
    }
}
