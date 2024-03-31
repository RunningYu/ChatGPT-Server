package chatgptserver.controller;

import chatgptserver.Common.SseUtils;
import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.QuestionRequestAO;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ApiAuthAlgorithm;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ApiClient;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.CreateResponse;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ProgressResponse;
import chatgptserver.enums.GPTConstants;
import chatgptserver.service.XunFeiService;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static chatgptserver.Common.SseUtils.sseEmitterThreadLocal;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/22
 */

@ApiModel("讯飞星火")
@Slf4j
@RestController
public class XunFeiXingHuoController {

    @Autowired
    private XunFeiService xunFeiService;

    @GetMapping("sse/test")
    public SseEmitter sseTest() {
//        SseEmitter sseEmitter = new SseEmitter();
//
//        try {
//            for (int i = 0; i < 10; i ++) {
//                sseEmitter.send(SseEmitter.event().comment("[" + i + "]"));
//                System.out.println("[" + i + "]");
//                Thread.sleep(1000);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException();
//        }


        //设置默认的超时时间60秒，超时之后服务端主动关闭连接。
//        SseEmitter sseEmitter = new SseEmitter(60 * 1000L);
        SseEmitter sseEmitter = new SseEmitter();
        sseEmitterThreadLocal.set(sseEmitter);

        sseEmitter.onCompletion(new Runnable() {
            @Override
            public void run() {
                System.out.println("nnnnnnnnnnnnnnnnn");
                try {
                    for (int i = 0; i < 10; i ++) {
                        System.out.println("[" + i + "]");
                        sseEmitter.send(SseEmitter.event().comment("[" + i + "]"));
                        Thread.sleep(1000);
                    }
                } catch (IOException e) {
                    throw new RuntimeException();
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }
        });

        return sseEmitter;
    }

    @ApiOperation("讯飞星火：图片理解")
    @GetMapping("/chat/xf/image/understander")
    public SseEmitter xfImageUnderstand(@RequestParam("image") MultipartFile image, @RequestParam("content") String content,
                                        @RequestParam("userCode") String userCode, @RequestParam("chatCode") String chatCode) {
        log.info("ChatGptController xfPictureUnderstand image:[{}], content[{}], userCode:[{}], chatCode:[{}]", image, content, userCode, chatCode);
        //设置默认的超时时间60秒，超时之后服务端主动关闭连接。
//        SseEmitter sseEmitter = new SseEmitter(60 * 1000L);
        SseEmitter sseEmitter = new SseEmitter();
        Long threadId = Thread.currentThread().getId();
        SseUtils.sseEmittersMap.put(threadId, sseEmitter);
        SseEmitter sseEmitter1 = xunFeiService.xfImageUnderstand(threadId, image, content, userCode, chatCode);
        return sseEmitter;
    }

    @ApiOperation("讯飞星火：图片生成")
    @GetMapping("/chat/xf/image/create")
    public JsonResult xfImageCreate(@Param("content") String content, @Param("userCode") String userCode,
                                    @Param("chatCode") String chatCode) {
        log.info("ChatGptController xfImageCreate content:[{}], userCode:[{}], chatCode:[{}]", content, userCode, chatCode);
        JsonResult response = xunFeiService.xfImageCreate(content, userCode, chatCode);

        return response;
    }

    @ApiOperation("讯飞星火：文本问答")
    @PostMapping(value = "/chat/xf/question")
    public SseEmitter xfQuestion(@RequestBody QuestionRequestAO request) {
        log.info("ChatGptController xfQuestion request:[{}]", request);
        SseEmitter sseEmitter = new SseEmitter();
        Long threadId = Thread.currentThread().getId();
        SseUtils.sseEmittersMap.put(threadId, sseEmitter);
        xunFeiService.xfQuestion(threadId, request);

        return sseEmitter;
    }

    @ApiOperation("讯飞星火：PPT生成")
    @GetMapping("/chat/xf/ppt/create")
    public JsonResult xfPptCreate(@Param("content") String content, @Param("userCode") String userCode,
                                    @Param("chatCode") String chatCode) {
        log.info("ChatGptController xfPptCreate content:[{}], userCode:[{}], chatCode:[{}]", content, userCode, chatCode);
        String response = xunFeiService.xfPptCreate(content, userCode, chatCode);

        return JsonResult.success(response);
    }


}

