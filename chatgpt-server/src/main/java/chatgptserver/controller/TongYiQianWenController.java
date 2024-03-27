package chatgptserver.controller;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.service.TongYiService;
import chatgptserver.service.UserService;
import chatgptserver.service.WenXinService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 * @Author：chenzhenyu
 * @Date：2024/3/26 21:04
 */
@Slf4j
@RestController
public class TongYiQianWenController {

    @Autowired
    private TongYiService tongYiService;

    @Autowired
    private UserService userService;

    /**
     * todo：多轮对话未完成
     */
    @ApiOperation("通义千问：文本问答")
    @GetMapping("/chat/tongYi/question")
    public JsonResult wenXinChat(@Param("userCode") String userCode,
                                 @Param("chatCode") String chatCode,
                                 @Param("content") String content) {
        log.info("WenXinYiYanController wenXinChat userCode:[{}] chatCode:[{}] content:[{}]", userCode, chatCode, content);
        String result = tongYiService.getMessageFromWenXin(userCode, chatCode, content);

        return JsonResult.success(result);
    }

    @ApiOperation("通义千问：图片理解")
    @PostMapping("/chat/tongYi/image/understand")
    public JsonResult tongYiImageUnderstand(@RequestParam("image") MultipartFile image,
                                            @RequestParam("content") String content,
                                            @RequestParam("userCode") String userCode,
                                            @RequestParam("chatCode") String chatCode) {
        log.info("WenXinYiYanController wenXinChat userCode:[{}] image:[{}] content:[{}], userCode:[{}], chatCode:[{}]", image, content, userCode, chatCode);
        String response = tongYiService.tyImageUnderstand(image, content, userCode, chatCode);

        return JsonResult.success(response);
    }

}
