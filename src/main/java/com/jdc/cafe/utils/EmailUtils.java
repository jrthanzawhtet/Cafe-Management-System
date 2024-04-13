package com.jdc.cafe.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailUtils {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> list){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("thanzawhtet1514@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if(list != null && list.size() > 0){
            message.setCc(getCCArray(list));
            emailSender.send(message);
        }

        message.setCc(getCCArray(list));
    }

    private String[] getCCArray(List<String> cclist){
        String[] cc =  new String[cclist.size()];
        for(int i=0;i<cclist.size();i++)    {
            cc[i] = cclist.get(i);
        }
        return cc;
    }

    public void forgotEmail(String to,String subject, String password) throws Exception{
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
    }
}
