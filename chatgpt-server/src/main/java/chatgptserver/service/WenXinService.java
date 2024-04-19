package chatgptserver.service;


import chatgptserver.bean.ao.JsonResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:32
 */
public interface WenXinService {

    JsonResult getMessageFromWenXin(String userCode, String chatCode, String message, Boolean isRebuild);

    JsonResult wxImageCreate(String userCode, String chatCode, String content, Boolean isRebuild);

    JsonResult wenXinImageUnderstand(String token, String chatCode, MultipartFile image, String content, Boolean isRebuild);
}
