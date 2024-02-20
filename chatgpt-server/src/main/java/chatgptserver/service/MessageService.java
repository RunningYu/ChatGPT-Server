package chatgptserver.service;


import chatgptserver.bean.ao.ChatAddRequestAO;
import chatgptserver.bean.ao.JsonResult;

import java.util.Map;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:32
 */
public interface MessageService {

    String getMessageFromWenXin(String userCode, String chatCode, String message);

    Map<String, String> wenXinAdd(ChatAddRequestAO request);

    JsonResult xfImageUnderstand(String image);
}
