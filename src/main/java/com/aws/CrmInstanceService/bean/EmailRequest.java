package com.aws.CrmInstanceService.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

@Component
public class EmailRequest {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private ConfigProperties configProperties;

    public boolean sendEmail(String toEmail) throws MessagingException, IOException {
        logger.info("process started to send email to - " + toEmail);
        boolean mailStatus = false;
        String subject = "hello new cmb";
        String body = "Hello Welcome here";

        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", configProperties.getSmptpport());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        // Create a Session object to represent a mail session with the specified properties.
        Session session = Session.getDefaultInstance(props);

        // Create a message with the specified information.
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(configProperties.getEmaildomain(), configProperties.getFrommailid()));

        // Set the recipients
        msg.setRecipients(Message.RecipientType.TO, toEmail);
        msg.setRecipients(Message.RecipientType.BCC, configProperties.getBccemail());

        msg.setHeader("Content-Type", "text/html; charset=utf-8");
        msg.setSubject(subject, "utf-8");
        msg.setContent(body, "text/html; charset=utf-8");

        Transport transport = session.getTransport();
        try {
            if (null != msg.getAllRecipients() &&  msg.getAllRecipients().length > 0) {
                logger.info("Inside transport");
                transport.connect(configProperties.getHost(),
                                    configProperties.getSmtpusername(),
                                        configProperties.getSmtppassword());
                transport.sendMessage(msg, msg.getAllRecipients());
                mailStatus = true;
            } else {
                mailStatus = false;
            }
        } catch (Exception e) {
            mailStatus = false;
            e.printStackTrace();
            logger.error("Error in email triggering " , e);
        } finally {
            transport.close();
        }
        logger.info("send email status  : " + mailStatus +" to  " +  toEmail);
        return mailStatus;
    }
}
