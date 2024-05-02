package chatgptserver;

import chatgptserver.Common.MailUtil;
import chatgptserver.enums.QQEmailConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/1
 */
@Slf4j
@SpringBootTest
public class QQEmailTest {


    //接收人
    private static final String recipient = "";

    /**
     * 发送文本邮件
     */
//    @Test
//    public void sendSimpleMail() {
//        Mail mail = new Mail();
//        int code = (int) ((Math.random() * 9 + 1) * 100000);
//        mail.setRecipient(recipient);
//        mail.setSubject("字节跳动2024校园招聘录用意向书");
//        mail.setContent("\n" +
//                "亲爱的袁宜明同学\u200B\n" +
//                "\n" +
//                "\u200B\n" +
//                "\n" +
//                "恭喜你顺利通过字节跳动2024校园招聘的考核。我们非常高兴地通知你，即将成为字节跳动的最新成员！\u200B\n" +
//                "\n" +
//                "\u200B\n" +
//                "\n" +
//                "你将担任后端开发工程师-抖音一职，工作地点为北京。我们期待着能和你并肩前行，在充满潜力的字节跳动，共同开创一份有创造力的事业。\u200B\n" +
//                "\n" +
//                "\n" +
//                "我们会在10-11月陆续沟通/发放含薪资的正式Offer，还请耐心等待。\u200B\n" +
//                "\n" +
//                "\u200B\n" +
//                "\n" +
//                "【关于我们】\u200B\n" +
//                "\n" +
//                "字节跳动成立于2012年3月，公司使命为“Inspire Creativity, Enrich Life（激发创造，丰富生活）”。业务覆盖全球150个国家和地区，拥有15万名员工。\u200B\n" +
//                "\n" +
//                "\u200B\n" +
//                "\n" +
//                "字节跳动在全球推出了多款有影响力的产品，包括今日头条、抖音、西瓜视频、飞书、Lark、PICO、剪映、TikTok等。截至2021年6月，字节跳动旗下产品全球月活跃用户数超过19亿。\u200B\n" +
//                "\n" +
//                "\u200B\n" +
//                "\n" +
//                "在字节跳动，我们提供多维度的项目实战机会，让新人在创造和实战中快速成长。同时，我们鼓励用创新方式解决问题，关注和创造社会价值，承担社会责任。\u200B\n" +
//                "\n" +
//                "\u200B\n" +
//                "\n" +
//                "同时，我们诚挚地邀请你推荐身边优秀的人才加入字节跳动。经过人选同意可将他们的简历直接发给HR，在你及推荐的同学入职后，根据内推政策有机会获得500-1000元不等的现金奖励！\u200B\n" +
//                "\n" +
//                "\u200B\n" +
//                "\n" +
//                "期待更多优秀同学的加入，和我们一起做有挑战的事，激发创造！\u200B\n" +
//                "\n" +
//                "\u200B\n" +
//                "\n" +
//                "点击【 Hello同学！欢迎了解字节跳动（校招）】介绍手册，了解字节跳动的业务发展和成长体验。\u200B\n" +
//                "\n" +
//                "\u200B\n" +
//                "\n" +
//                "特别提示：\u200B\n" +
//                "\n" +
//                "如在招聘流程中（包括但不限于简历投递、笔试、面试等）有任何虚假陈述或故意隐瞒等情形，本Offer意向书将自动失效。\u200B\n" +
//                "\n" +
//                "\u200B\n" +
//                "\n" +
//                "和优秀的人，做有挑战的事！\u200B\n" +
//                "\n" +
//                "\u200B\n" +
//                "\n" +
//                "此致\u200B\n" +
//                "\n" +
//                "\n" +
//                "刘\uD83D\uDC74管理部\n" +
//                "2023-10-27\u200B\n" +
//                "\n" +
//                "\n" +
//                "\n" +
//                "\n" +
//                "\n" +
//                "codeMan_L\n" +
//                "773890440@qq.com\n"
//        );
//        mailUtil.sendSimpleMail(mail);
//    }

    //  javaMail邮件发送

    @Test
    public void test2() {
        try {
            Session session = MailUtil.buildSession();
            //  3、创建一封邮件
            String subject = "重庆邮电大学学硕录取邮件";
            String senderName = "陈老头";
            String content = "亲爱的谭舟行同学，恭喜你被重庆邮电大学学硕录取，并获得一等奖！";
            MimeMessage message = MailUtil.createMimeMessage(session, QQEmailConstants.QQ_EMAIL_SENDER_EMAIL, recipient, " ", senderName, subject, content);
            //  4、根据session获取邮件传输对象
            Transport transport = session.getTransport();
            //  5、使用邮箱账号和授权码连接邮箱服务器emailAccount必须与message中的发件人邮箱一致，否则报错
            transport.connect(QQEmailConstants.QQ_EMAIL_SENDER_EMAIL, QQEmailConstants.QQ_EMAIL_AUTHORIZATION_CODE);
            //  6、发送邮件,发送所有收件人地址
            transport.sendMessage(message, message.getAllRecipients());
            //  7、关闭连接
            transport.close();
        } catch (Exception e) {
            throw new RuntimeException("失败");
        }
    }

}
