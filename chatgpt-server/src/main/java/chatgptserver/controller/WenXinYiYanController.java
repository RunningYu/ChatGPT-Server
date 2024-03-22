package chatgptserver.controller;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.service.UserService;
import chatgptserver.service.WenXinService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 9:15
 */
@Slf4j
@RestController
public class WenXinYiYanController {

    @Autowired
    private WenXinService wenXinService;

    @Autowired
    private UserService userService;

    @ApiOperation("文心一言：文本问答")
    @GetMapping("/chat/wenXin")
    public JsonResult wenXinChat(@Param("userCode") String userCode,
                                 @Param("chatCode") String chatCode,
                                 @Param("message") String message) {
        log.info("ChatGptController wenXinChat userCode:[{}] chatCode:[{}] message:[{}]", userCode, chatCode, message);
        String result = wenXinService.getMessageFromWenXin(userCode, chatCode, message);

        return JsonResult.success(result);
    }



}
