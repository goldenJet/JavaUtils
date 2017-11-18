package com.jet.utils.email_msg;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

&

/**
 * Created by jet.chen on 5/17/2017.
 */
@Service
@Log4j
public class EmailHandler {

    @Autowired
    SMTPConfigRepository sMTPConfigRepository;

    public Message generateMessage(String subjectStr, String emailContent, SMTPConfig smtpConfig) throws IOException {
        if (smtpConfig == null) {
            smtpConfig = sMTPConfigRepository.findOne((long) 1);
        }
        MailSendInfo mailInfo = new MailSendInfo();

        String mailServerHost = smtpConfig.getSmtpAddress();
        String mailServerPort = String.valueOf(smtpConfig.getSmtpPort());
        String mailUserName = smtpConfig.getUserName();
        String mailPassword = smtpConfig.getPassword();
        String mailFromAddress = smtpConfig.getEmailAddress();
        mailInfo.setMailServerHost(mailServerHost);
        mailInfo.setMailServerPort(mailServerPort);
        mailInfo.setValidate(true);
        mailInfo.setUserName(mailUserName);
        mailInfo.setPassword(mailPassword);
        mailInfo.setFromAddress(mailFromAddress);

        mailInfo.setSubject(subjectStr);
        mailInfo.setContent(emailContent);
        return generateMessage(mailInfo);
    }

    public Message generateMessage(MailSendInfo mailInfo) {
        // Determine whether identity authentication is required
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        // If you need to create an identity authentication, password validator
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // According to the mail session attribute and password verifier to
        // construct a mail session
        Session sendMailSession = Session.getInstance(pro, authenticator);
        try {
            // Create an e-mail message from session
            Message mailMessage = new MimeMessage(sendMailSession);
            // Create a message sender address
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // Set the message to the sender
            mailMessage.setFrom(from);
            // Create the recipient address of the message and set it to the
            // message
            //Address to = new InternetAddress(mailInfo.getToAddress());
            // Message.RecipientType.TO Property indicates the type of the
            // recipient TO
            //mailMessage.setRecipient(Message.RecipientType.TO, to);
            // Set the theme of the mail message
            mailMessage.setSubject(mailInfo.getSubject());
            // Set the time for sending messages
            mailMessage.setSentDate(new Date());
            // MiniMultipartClass is a container class that contains an object
            // of the MimeBodyPart type
            Multipart mainPart = new MimeMultipart();
            // Create a HTML that contains the content of MimeBodyPart
            BodyPart html = new MimeBodyPart();
            // Set HTML content
            html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
            mainPart.addBodyPart(html);
            // Set the MiniMultipart object to mail content
            mailMessage.setContent(mainPart);
            return mailMessage;
        } catch (MessagingException ex) {
            ex.printStackTrace();
            log.error("Send emai error", ex);
        }
        return null;
    }

    public void sendHtmlEmail(Message mailMessage, String toAddress) {
        try {
            Address to = new InternetAddress(toAddress);
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            Transport.send(mailMessage);
        } catch (MessagingException ex) {
            ex.printStackTrace();
            log.error("Send email error", ex);
        }
    }


    public void sendHtmlEmail(Message mailMessage, List<String> toAddressList, List<String> ccAddressList, List<String> bccAddressList) {
        try {
            if (toAddressList != null) {
                mailMessage.setRecipients(Message.RecipientType.TO, parseStringListToAddressArray(toAddressList));
            }
            if (ccAddressList != null) {
                mailMessage.setRecipients(Message.RecipientType.CC, parseStringListToAddressArray(ccAddressList));
            }
            if (bccAddressList != null) {
                mailMessage.setRecipients(Message.RecipientType.BCC, parseStringListToAddressArray(bccAddressList));
            }
            Transport.send(mailMessage);
        } catch (MessagingException ex) {
            ex.printStackTrace();
            log.error("Send email error", ex);
        }
    }

    private Address[] parseStringListToAddressArray(List<String> emailAddressList) {
        List<Address> addressList = new ArrayList<>();
        for (String emailAddress : emailAddressList) {
            try {
                addressList.add(new InternetAddress(emailAddress));
            } catch (Exception e) {

            }
        }
        return addressList.toArray(new Address[addressList.size()]);
    }
}
