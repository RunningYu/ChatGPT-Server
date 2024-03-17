package chatgptserver.controller;

import chatgptserver.bean.ao.ChatAddRequestAO;
import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.UserAO;
import chatgptserver.bean.po.MessagesPO;
import chatgptserver.bean.po.UserPO;
import chatgptserver.service.MessageService;
import chatgptserver.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 9:15
 */
@Slf4j
@RestController
public class ChatGptController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @ApiOperation("文心一言：文本问答")
    @GetMapping("/chat/wenXin")
    public JsonResult wenXinChat(@Param("userCode") String userCode,
                                 @Param("chatCode") String chatCode,
                                 @Param("message") String message) {
        log.info("ChatGptController wenXinChat userCode:[{}] chatCode:[{}] message:[{}]", userCode, chatCode, message);
        String result = messageService.getMessageFromWenXin(userCode, chatCode, message);

        return JsonResult.success(result);
    }

    @PostMapping("/chat/add")
    public JsonResult wenXinAdd(@RequestBody ChatAddRequestAO request) {
        log.info("ChatGptController wenXinChat request:[{}]", request);
        Map<String, String> response = messageService.wenXinAdd(request);

        return JsonResult.success();
    }

    @ApiOperation("讯飞星火：图片理解")
    @GetMapping("/chat/xf/image/understander")
    public JsonResult xfImageUnderstand(@Param("image") String image) {
        log.info("ChatGptController xfPictureUnderstand image:[{}]", image);
        JsonResult response = messageService.xfImageUnderstand(image);

        return JsonResult.success();
    }
}
