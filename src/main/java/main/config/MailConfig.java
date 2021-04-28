package main.config;

import org.flywaydb.core.internal.configuration.ConfigUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Configuration
@Component
public class MailConfig {

    @Value("${blog.mailbot.password}")
    private String mailPassword;
    @Value("${blog.mailbot.username}")
    private String username;

    @Bean
    public JavaMailSender getMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.yandex.ru");
        mailSender.setUsername(username);
        mailSender.setPassword(mailPassword);
        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        Properties properties = mailSender.getJavaMailProperties();
        properties.setProperty("mail.transport.protocol", mailSender.getProtocol());
        return mailSender;
    }
}
