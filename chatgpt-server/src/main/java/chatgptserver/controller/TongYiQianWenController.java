package chatgptserver.controller;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.service.TongYiService;
import chatgptserver.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



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

    @ApiOperation("通义千问：文本问答")
    @GetMapping("/chat/tongYi/question")
    public JsonResult tongYiQuestion(@Param("userCode") String userCode,
                                 @Param("chatCode") String chatCode,
                                 @Param("content") String content) {
        log.info("WenXinYiYanController wenXinChat userCode:[{}] chatCode:[{}] content:[{}]", userCode, chatCode, content);
        String result = tongYiService.tyQuestion(userCode, chatCode, content);

        return JsonResult.success(result);
    }

    @ApiOperation("通义千问：图片理解")
    @PostMapping("/chat/tongYi/image/understand")
    public JsonResult tongYiImageUnderstand(@RequestParam(value = "image", required = false) MultipartFile image,
                                            @RequestParam("content") String content,
                                            @RequestParam("userCode") String userCode,
                                            @RequestParam("chatCode") String chatCode) {
        log.info("WenXinYiYanController wenXinChat userCode:[{}] image:[{}] content:[{}], userCode:[{}], chatCode:[{}]", image, content, userCode, chatCode);
        String response = tongYiService.tyImageUnderstand(image, content, userCode, chatCode);

        return JsonResult.success(response);
    }

    /**
     * todo：模型申请ing，待审核通过再进行接口测试
     */
    @ApiOperation("通义千问：图片生成")
    @GetMapping("/chat/tongYi/image/create")
    public JsonResult tongYiImageCreate(@Param("content") String content, @Param("userCode") String userCode,
                                        @Param("chatCode") String chatCode) {
        log.info("WenXinYiYanController tongYiImageCreate content:[{}], userCode:[{}], chatCode:[{}]", content, userCode, chatCode);
        String response = tongYiService.tyImageCreate(userCode, chatCode, content);

        return JsonResult.success();
    }

}
