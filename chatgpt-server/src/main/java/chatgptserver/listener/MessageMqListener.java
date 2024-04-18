package chatgptserver.listener;

import chatgptserver.bean.ao.MessagesAO;
import chatgptserver.enums.mq.MessageMqConstants;
import chatgptserver.service.MessageService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/17
 */
@ApiModel(description = "Mq监听器")
@Component
public class MessageMqListener {

    @Autowired
    private MessageService messageService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ApiOperation(" 异步处理&MQ监听整合, 监听 新增 or 修改 的业务")
    @Async
    @RabbitListener(queues = MessageMqConstants.MESSAGE_INSERT_QUEUE)
    public void listenMessageInsertOrUpdate1(String message) {
        if (StringUtils.isNotBlank( message )){
//            MessagesAO messagesAO = JSON.parseObject(message, MessagesAO.class);
//            messageService.recordHistory(messagesAO.getUserCode(), messagesAO.getChatCode(), messagesAO.getQuestion(), messagesAO.getReplication());
            System.out.println("-----------【队列1】 MESSAGE_INSERT_QUEUE 同步新增 or 修改了--->" + LocalTime.now());
            logger.info("Token时效队列 监听1（String） {} 中消息： {}", MessageMqConstants.MESSAGE_INSERT_QUEUE, message);
        }
    }

    @ApiOperation(" 监听 新增 or 修改 的业务")
    @Async
    @RabbitListener(queues = MessageMqConstants.MESSAGE_INSERT_QUEUE)
    public void listenMessageInsertOrUpdate2(String message) {
        if (StringUtils.isNotBlank( message )){
//            MessagesAO messagesAO = JSON.parseObject(message, MessagesAO.class);
//            messageService.recordHistory(messagesAO.getUserCode(), messagesAO.getChatCode(), messagesAO.getQuestion(), messagesAO.getReplication());
            System.out.println("-----------【队列2】 MESSAGE_INSERT_QUEUE 同步新增 or 修改了--->" + LocalTime.now());
            logger.info("Token时效队列 监听2（String） {} 中消息： {}", MessageMqConstants.MESSAGE_INSERT_QUEUE, message);
        }
    }


    @ApiOperation("监听 删除 的业务")
    @RabbitListener(queues = MessageMqConstants.MESSAGE_DELETE_QUEUE)
    @Async
    public void listenMessageDeleteOrUpdate1(String message){
        if (StringUtils.isNotBlank( message )){
//            messageService.deleteUserOfIndexByUserId(message);
            System.out.println("-----------【队列1】 MESSAGE_DELETE_QUEUE --->" + LocalTime.now());
            logger.info("Token时效队列 监听1（String） {} 中消息： {}", MessageMqConstants.MESSAGE_DELETE_QUEUE, message);
        }
    }

    @ApiOperation("监听 删除 的业务")
    @RabbitListener(queues = MessageMqConstants.MESSAGE_DELETE_QUEUE)
    @Async
    public void listenMessageDeleteOrUpdate2(String message){
        if (StringUtils.isNotBlank( message )){
//            messageService.deleteUserOfIndexByUserId(message);
            System.out.println("-----------【队列2】 MESSAGE_DELETE_QUEUE --->" + LocalTime.now());
            logger.info("Token时效队列 监听2（String） {} 中消息： {}", MessageMqConstants.MESSAGE_DELETE_QUEUE, message);
        }
    }

}
