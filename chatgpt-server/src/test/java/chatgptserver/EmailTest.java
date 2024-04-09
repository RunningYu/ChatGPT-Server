package chatgptserver;

import chatgptserver.Common.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


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

}
