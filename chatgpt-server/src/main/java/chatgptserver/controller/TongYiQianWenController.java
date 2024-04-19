package chatgptserver.controller;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.QuestionRequestAO;
import chatgptserver.bean.po.UserPO;
import chatgptserver.service.TongYiService;
import chatgptserver.service.UserService;
import chatgptserver.utils.JwtUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


/**
 * @Author：chenzhenyu
 * @Date：2024/3/26 21:04
 */
@Slf4j
@RestController
public class TongYiQianWenController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TongYiService tongYiService;

    @Autowired
    private UserService userService;

    @ApiOperation("通义千问：文本问答")
    @PostMapping("/chat/tongYi/question")
    public JsonResult tongYiQuestion(HttpServletRequest httpServletRequest,
                                     @RequestBody QuestionRequestAO request) {
        String token = httpServletRequest.getHeader("token");
        log.info("WenXinYiYanController tongYiQuestion token:[{}], request:[{}]", token, request);
        JsonResult response = tongYiService.tyQuestion(token, request.getChatCode(), request.getContent(), request.getIsRebuild());

        return response;
    }

    @ApiOperation("通义千问：图片理解")
    @PostMapping("/chat/tongYi/image/understand")
    public JsonResult tongYiImageUnderstand(HttpServletRequest httpServletRequest,
                                            @RequestParam(value = "image", required = false) MultipartFile image,
                                            @RequestParam("content") String content,
                                            @RequestParam("chatCode") String chatCode,
                                            @RequestParam(value = "isRebuild", defaultValue = "false") Boolean isRebuild) {
        String token = httpServletRequest.getHeader("token");
        log.info("WenXinYiYanController tongYiImageUnderstand token:[{}], image:[{}] content:[{}], token:[{}], chatCode:[{}], isRebuild:[{}]", token, image, content, token, chatCode, isRebuild);
        JsonResult response = tongYiService.tyImageUnderstand(image, content, token, chatCode, isRebuild);

        return response;
    }

    @ApiOperation("通义千问：图片生成")
    @PostMapping("/chat/tongYi/image/create")
    public JsonResult tongYiImageCreate(HttpServletRequest httpServletRequest,
                                        @RequestBody QuestionRequestAO request) {
        String token = httpServletRequest.getHeader("token");
        log.info("WenXinYiYanController tongYiImageUnderstand token:[{}]", token);
        String userCode = userService.getUserCodeByToken(token);
        log.info("WenXinYiYanController tongYiImageCreate userCode:[{}], request:[{}]", userCode, request);
        JsonResult response = tongYiService.tyImageCreate(userCode, request.getChatCode(), request.getContent(), request.getIsRebuild());

        return response;
    }

}
