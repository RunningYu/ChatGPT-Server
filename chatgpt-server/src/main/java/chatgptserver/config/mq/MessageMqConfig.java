package chatgptserver.config.mq;

import chatgptserver.enums.mq.MessageMqConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/17
 */
@ApiModel(description = "message配置")
@Configuration
public class MessageMqConfig {

    @ApiOperation("实现交换机的定义")
    @Bean
    public TopicExchange messageTopicExchange() {
        return new TopicExchange(MessageMqConstants.MESSAGE_EXCHANGE, true, false);
    }

    @ApiOperation("定义新增队列")
    @Bean
    public Queue messageInsertQueue(){
        return new Queue(MessageMqConstants.MESSAGE_INSERT_QUEUE,true);
    }

    @ApiOperation("定义删除队列")
    @Bean
    public Queue messageDeleteQueue(){
        return new Queue(MessageMqConstants.MESSAGE_DELETE_QUEUE,true);
    }

    @ApiOperation("定义绑定关系")
    @Bean
    public Binding messageInsertQueueBinding(){
//                            绑定     队列         到    交换机                        用的RoutingKey
        return BindingBuilder.bind(messageInsertQueue()).to(messageTopicExchange()).with(MessageMqConstants.MESSAGE_INSERT_KEY);
    }

    @Bean
    public Binding messageDeleteQueueBinding(){
//                            绑定     队列         到    交换机                        用的RoutingKey
        return BindingBuilder.bind(messageDeleteQueue()).to(messageTopicExchange()).with(MessageMqConstants.MESSAGE_DELETE_KEY);
    }


}
