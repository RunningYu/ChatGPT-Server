package chatgptserver.Common;

import chatgptserver.enums.QQEmailConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/1
 */

@Component
@Slf4j
public class MailUtil {

    public static Session buildSession() {
        Properties props = new Properties();
        //  开启debug调试
        props.setProperty("mail.debug", "true");
        // 发送服务器需要身份验证
        props.setProperty("mail.smtp.auth", "true");
        // 设置右键服务器的主机名
        props.setProperty("mail.host", QQEmailConstants.QQ_EMAIL_HOST);
        // 发送邮件协议名称
        props.setProperty("mail.transport.protocol", "smtp");
        // 2、根据配置创建会话对象，用于和邮件服务器交互
        Session session = Session.getInstance(props);
        //  设置debug，可以查看详细的发送log
        session.setDebug(true);

        return session;
    }

    /**
     * @param session
     * @param sendMail
     * @param receiveMail
     * @param html
     * cc:抄送、 Bcc:密送、 To:发送
     */
    public static  MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail, String html, String senderName, String subject, String content) {
        try {
            // 1、创建一封邮件对象
            MimeMessage message = new MimeMessage(session);
            // 2、From：发件人
            message.setFrom(new InternetAddress(sendMail, senderName, "UTF-8"));
            // 3、To:收件人（可以增加多个收件人：抄送或者密送）
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "张三", "UTF-8"));
            // 4、Subject:邮件主题
            message.setSubject(subject,"UTF-8");
            // 5、Content:邮件正文（可以使用Html标签）
            message.setContent(content,"text/html;charset=UTF-8");
            // 6、设置发送时间
            message.setSentDate(new Date());
            // 7、保存设置
            message.saveChanges();
            // 8、将该邮件保存在本地
            OutputStream out = new FileOutputStream("D://MyEmail" + UUID.randomUUID().toString() + ".eml");
            message.writeTo(out);
            out.flush();
            out.close();
            return message;
        } catch (Exception e) {
            throw new RuntimeException("发送邮件失败");
        }
    }

    public static String createVerifyCode() {
        String verifyCode = "";
        for (int i = 0; i < 4; i ++) {
            int code = 10 * (int) Math.random() - 1;
            verifyCode += code;
        }

        return verifyCode;
    }

    public static void sendEmailMessage(String subject, String recipientEmail, String content) {
        try {
            Session session = MailUtil.buildSession();
            //  3、创建一封邮件
            content = "ChatGPT集成平台注册验证码：" + content;
            String senderName = "gpt-developers";
            MimeMessage message = MailUtil.createMimeMessage(session, QQEmailConstants.QQ_EMAIL_SENDER_EMAIL, recipientEmail, " ", senderName, subject, content);
            //  4、根据session获取邮件传输对象
            Transport transport = session.getTransport();
            //  5、使用邮箱账号和授权码连接邮箱服务器emailAccount必须与message中的发件人邮箱一致，否则报错
            transport.connect(QQEmailConstants.QQ_EMAIL_SENDER_EMAIL, QQEmailConstants.QQ_EMAIL_AUTHORIZATION_CODE);
            //  6、发送邮件,发送所有收件人地址
            transport.sendMessage(message, message.getAllRecipients());
            //  7、关闭连接
            transport.close();
        } catch (Exception e) {
            throw new RuntimeException("邮件发送失败");
        }
    }
}
