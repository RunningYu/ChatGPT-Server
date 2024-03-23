package chatgptserver.service;



/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:32
 */
public interface WenXinService {

    String getMessageFromWenXin(String userCode, String chatCode, String message);


}
