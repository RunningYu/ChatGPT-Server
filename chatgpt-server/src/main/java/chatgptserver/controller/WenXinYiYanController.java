package chatgptserver.controller;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.po.UserPO;
import chatgptserver.service.UserService;
import chatgptserver.service.WenXinService;
import chatgptserver.utils.JwtUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 9:15
 */
@Slf4j
@RestController
public class WenXinYiYanController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private WenXinService wenXinService;

    @Autowired
    private UserService userService;

    @ApiOperation("文心一言：文本问答")
    @GetMapping("/chat/wenXin/question")
    public JsonResult wenXinChat(HttpServletRequest httpServletRequest,
                                 @Param("chatCode") String chatCode,
                                 @Param("content") String content) {
        String token = httpServletRequest.getHeader("token");
        log.info("ChatGptController wenXinChat token:[{}]", token);
        UserPO tokenUser = jwtUtils.getUserFromToken(token);
        log.info("WenXinYiYanController wenXinChat userCode:[{}] chatCode:[{}] content:[{}]", tokenUser, chatCode, content);
        String result = wenXinService.getMessageFromWenXin(tokenUser.getUserCode(), chatCode, content);

        return JsonResult.success(result);
    }

    /**
     * todo: 开源接口欠费，未测试完
     */
    @ApiOperation("文心一言：图片生成")
    @GetMapping("/chat/wenXin/image/create")
    public JsonResult wenXinImageCreate(HttpServletRequest httpServletRequest,
                                        @Param("chatCode") String chatCode, @Param("content") String content) {
        String token = httpServletRequest.getHeader("token");
        log.info("WenXinYiYanController wenXinImageCreate token:[{}]", token);
        UserPO tokenUser = jwtUtils.getUserFromToken(token);
        log.info("WenXinYiYanController wenXinImageCreate tokenUser:[{}] chatCode:[{}] content:[{}]", tokenUser, chatCode, content);
        String result = wenXinService.wxImageCreate(tokenUser.getUserCode(), chatCode, content);

        return JsonResult.success(result);
    }

    @ApiOperation("文心一言：图片理解")
    @GetMapping("/chat/wenXin/image/understand")
    public JsonResult wenXinImageUnderstand(HttpServletRequest httpServletRequest,
                                            @RequestParam("image") MultipartFile image,
                                            @RequestParam("chatCode") String chatCode,
                                            @RequestParam("content") String content) {
        log.info("WenXinYiYanController wenXinImageUnderstand chatCode:[{}], image:[{}], content:[{}]", chatCode, image, content);
        String token = httpServletRequest.getHeader("token");
        log.info("WenXinYiYanController wenXinImageUnderstand token:[{}]", token);
        JsonResult response = wenXinService.wenXinImageUnderstand(token, chatCode, image, content);

        return response;
    }



}
