package chatgptserver.controller;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.po.UserPO;
import chatgptserver.service.TongYiService;
import chatgptserver.service.UserService;
import chatgptserver.utils.JwtUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    @GetMapping("/chat/tongYi/question")
    public JsonResult tongYiQuestion(HttpServletRequest httpServletRequest,
                                 @Param("chatCode") String chatCode,
                                 @Param("content") String content) {
        String token = httpServletRequest.getHeader("token");
        log.info("WenXinYiYanController tongYiQuestion token:[{}], chatCode:[{}] content:[{}]", token, chatCode, content);
        String result = tongYiService.tyQuestion(token, chatCode, content);

        return JsonResult.success(result);
    }

    @ApiOperation("通义千问：图片理解")
    @PostMapping("/chat/tongYi/image/understand")
    public JsonResult tongYiImageUnderstand(HttpServletRequest httpServletRequest,
                                            @RequestParam(value = "image", required = false) MultipartFile image,
                                            @RequestParam("content") String content,
                                            @RequestParam("chatCode") String chatCode) {
        String token = httpServletRequest.getHeader("token");
        log.info("WenXinYiYanController tongYiImageUnderstand token:[{}], image:[{}] content:[{}], token:[{}], chatCode:[{}]", token, image, content, token, chatCode);
        String response = tongYiService.tyImageUnderstand(image, content, token, chatCode);

        return JsonResult.success(response);
    }

    /**
     * todo：模型申请ing，待审核通过再进行接口测试
     */
    @ApiOperation("通义千问：图片生成")
    @GetMapping("/chat/tongYi/image/create")
    public JsonResult tongYiImageCreate(HttpServletRequest httpServletRequest,
                                        @Param("content") String content,
                                        @Param("chatCode") String chatCode) {
        String token = httpServletRequest.getHeader("token");
        log.info("WenXinYiYanController tongYiImageUnderstand token:[{}]", token);
        UserPO tokenUser = jwtUtils.getUserFromToken(token);
        log.info("WenXinYiYanController tongYiImageCreate content:[{}], tokenUser:[{}], chatCode:[{}]", content, tokenUser, chatCode);
        String response = tongYiService.tyImageCreate(tokenUser.getUserCode(), chatCode, content);

        return JsonResult.success();
    }

}
