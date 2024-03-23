package chatgptserver.controller;

import chatgptserver.Common.SseUtils;
import chatgptserver.bean.ao.JsonResult;
import chatgptserver.service.XunFeiService;
import chatgptserver.utils.XunFeiUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public SseEmitter xfImageUnderstand(@Param("image") String image, String question) {
        log.info("ChatGptController xfPictureUnderstand image:[{}], question[{}]", image, question);
        //设置默认的超时时间60秒，超时之后服务端主动关闭连接。
//        SseEmitter sseEmitter = new SseEmitter(60 * 1000L);
        SseEmitter sseEmitter = new SseEmitter();
        Long threadId = Thread.currentThread().getId();
        SseUtils.sseEmittersMap.put(threadId, sseEmitter);
        SseEmitter sseEmitter1 = xunFeiService.xfImageUnderstand(threadId, image, question);
        return sseEmitter;
    }

    @ApiOperation("讯飞星火：图片生成")
    @GetMapping("/chat/xf/image/create")
    public JsonResult xfImageCreate(@Param("content") String content) {
        log.info("ChatGptController xfImageCreate content:[{}]", content);
        JsonResult response = xunFeiService.xfImageCreate(content);

        return response;
    }


}

