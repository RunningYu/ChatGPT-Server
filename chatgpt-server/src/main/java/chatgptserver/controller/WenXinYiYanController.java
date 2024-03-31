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
    @GetMapping("/chat/wenXin/question")
    public JsonResult wenXinChat(@Param("userCode") String userCode,
                                 @Param("chatCode") String chatCode,
                                 @Param("content") String content) {
        log.info("WenXinYiYanController wenXinChat userCode:[{}] chatCode:[{}] content:[{}]", userCode, chatCode, content);
        String result = wenXinService.getMessageFromWenXin(userCode, chatCode, content);

        return JsonResult.success(result);
    }

    /**
     * todo: 开源接口欠费，未测试完
     */
    @ApiOperation("文心一言：图片生成")
    @GetMapping("/chat/wenXin/image/create")
    public JsonResult wenXinImageCreate(@Param("userCode") String userCode, @Param("chatCode") String chatCode, @Param("content") String content) {
        log.info("WenXinYiYanController wenXinImageCreate userCode:[{}] chatCode:[{}] content:[{}]", userCode, chatCode, content);
        String result = wenXinService.wxImageCreate(userCode, chatCode, content);

        return JsonResult.success(result);
    }



}
