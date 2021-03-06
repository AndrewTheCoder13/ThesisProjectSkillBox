package main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {
    @Bean
    public JavaMailSender getMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setUsername("andreiciugureanu7@gmail.com");
        mailSender.setPassword("andciug2803");
        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        Properties properties = mailSender.getJavaMailProperties();
        properties.setProperty("mail.transport.protocol", mailSender.getProtocol());
        return mailSender;
    }
}
