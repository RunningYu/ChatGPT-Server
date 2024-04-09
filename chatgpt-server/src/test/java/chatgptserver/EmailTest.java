package chatgptserver;

import chatgptserver.Common.MailUtil;
import chatgptserver.bean.dto.Mail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/9
 */
@SpringBootTest
public class EmailTest {

    @Autowired
    private MailUtil mailUtil;


    //接收人
    private static final String recipient = "947219346@qq.com";

    /**
     * 发送文本邮件
     */
    @Test
    public void sendSimpleMail() {
        Mail mail = new Mail();
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        mail.setRecipient(recipient);
        mail.setSubject("修改邮箱");
        mail.setContent("亲爱的用户：您好！\n" +
                "\n" + "    您收到这封电子邮件是因为您 (也可能是某人冒充您的名义) 申请了修改邮箱。假如这不是您本人所申请, 请不用理会这封电子邮件, 但是如果您持续收到这类的信件骚扰, 请您尽快联络管理员。\n" +
                "\n" +
                "   请使用以下验证码完成后续修改邮箱流程\n" + "\n  " +
                code + "\n\n" +"  注意：请您收到邮件的十分钟内（"+
//                DateFormatUtils.format(new Date().getTime() + 10 * 60 * 1000, "yyyy-MM-dd HH:mm:ss")+
                "）前使用，否则验证码将会失效。"
        );
//        mailUtil.sendSimpleMail(mail);
    }
}
