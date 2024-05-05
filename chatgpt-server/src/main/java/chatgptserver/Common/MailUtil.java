package chatgptserver.Common;

import chatgptserver.enums.QQEmailConstants;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;
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
            int code = (int)(10 * (Math.random()) - 1);
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

    /**
     * 发送文本邮件
     * @param content : 内容
     */
    public static void sendSimpleMail(String subject, String email, String content) throws GeneralSecurityException, MessagingException {
        Properties prop=new Properties();
        //设置QQ邮件服务器
        prop.setProperty("mail.host","smtp.qq.com");
        //邮件发送协议
        prop.setProperty("mail.transport.protocol","smtp");
        //需要验证用户密码
        prop.setProperty("mail.smtp.auth","true");

        //设置SSL加密，QQ邮箱才有
        MailSSLSocketFactory sf=new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        prop.put("mail.smtp.ssl.enable","true");
        prop.put("mail.smtp.ssl.socketFactory",sf);

        //使用javaMail发送邮件的6个步骤
        //1.创建定义整个应用程序所需要的环境信息的session对象

        //QQ邮箱才有，其他邮箱就不用
        Session session=Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                //发件人邮件用户名、授权码
                return new PasswordAuthentication(QQEmailConstants.QQ_EMAIL_SENDER_EMAIL,QQEmailConstants.QQ_EMAIL_AUTHORIZATION_CODE);
            }
        });

        //开启session的debug模式，这样可以查看到程序发送Email的运行状态
        session.setDebug(true);

        //2.通过session得到transport对象
        Transport ts=session.getTransport();

        //3.使用邮箱的用户名和授权码连上邮件服务器
        ts.connect("smtp.qq.com", QQEmailConstants.QQ_EMAIL_SENDER_EMAIL, QQEmailConstants.QQ_EMAIL_AUTHORIZATION_CODE);

        //4.创建邮件：写文件
        //注意需要传递session
        MimeMessage message=new MimeMessage(session);
        //指明邮件的发件人
        message.setFrom(new InternetAddress(QQEmailConstants.QQ_EMAIL_SENDER_EMAIL));
        //指明邮件的收件人
        message.setRecipient(Message.RecipientType.TO,new InternetAddress(email));
        content = "ChatGPT集成平台注册验证码：" + content;
        //邮件标题
        message.setSubject(subject);
        //邮件的文本内容
        message.setContent(content,"text/html;charset=UTF-8");

        //5.发送邮件
        ts.sendMessage(message,message.getAllRecipients());

        //6.关闭连接
        ts.close();
    }

    /**
     * 验证邮箱是否有效
     */
    public static void emailConfirm(String subject, String email, String content) throws GeneralSecurityException, MessagingException {
        Properties prop=new Properties();
        //设置QQ邮件服务器
        prop.setProperty("mail.host","smtp.qq.com");
        //邮件发送协议
        prop.setProperty("mail.transport.protocol","smtp");
        //需要验证用户密码
        prop.setProperty("mail.smtp.auth","true");

        //设置SSL加密，QQ邮箱才有
        MailSSLSocketFactory sf=new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        prop.put("mail.smtp.ssl.enable","true");
        prop.put("mail.smtp.ssl.socketFactory",sf);

        //使用javaMail发送邮件的6个步骤
        //1.创建定义整个应用程序所需要的环境信息的session对象

        //QQ邮箱才有，其他邮箱就不用
        Session session=Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                //发件人邮件用户名、授权码
                return new PasswordAuthentication(QQEmailConstants.QQ_EMAIL_SENDER_EMAIL,QQEmailConstants.QQ_EMAIL_AUTHORIZATION_CODE);
            }
        });

        //开启session的debug模式，这样可以查看到程序发送Email的运行状态
        session.setDebug(true);

        //2.通过session得到transport对象
        Transport ts=session.getTransport();

        //3.使用邮箱的用户名和授权码连上邮件服务器
        ts.connect("smtp.qq.com", QQEmailConstants.QQ_EMAIL_SENDER_EMAIL, QQEmailConstants.QQ_EMAIL_AUTHORIZATION_CODE);

        //4.创建邮件：写文件
        //注意需要传递session
        MimeMessage message=new MimeMessage(session);
        //指明邮件的发件人
        message.setFrom(new InternetAddress(QQEmailConstants.QQ_EMAIL_SENDER_EMAIL));
        //指明邮件的收件人
        message.setRecipient(Message.RecipientType.TO,new InternetAddress(email));
        //邮件标题
        message.setSubject(subject);
        //邮件的文本内容
        message.setContent(content,"text/html;charset=UTF-8");

        //5.发送邮件
        ts.sendMessage(message,message.getAllRecipients());

        //6.关闭连接
        ts.close();
    }

}
