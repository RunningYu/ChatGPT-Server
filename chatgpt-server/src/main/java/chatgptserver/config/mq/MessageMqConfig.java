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
@ApiModel(description = "User配置")
@Configuration
public class MessageMqConfig {

    @ApiOperation("实现交换机的定义")
    @Bean
    public TopicExchange userTopicExchange() {
        return new TopicExchange(MessageMqConstants.MESSAGE_EXCHANGE, true, false);
    }

    @ApiOperation("定义新增队列")
    @Bean
    public Queue userInsertQueue(){
        return new Queue(MessageMqConstants.MESSAGE_INSERT_QUEUE,true);
    }
    @ApiOperation("定义删除队列")
    @Bean
    public Queue userDeleteQueue(){
        return new Queue(MessageMqConstants.MESSAGE_DELETE_QUEUE,true);
    }

    @ApiOperation("定义绑定关系")
    @Bean
    public Binding userInsertQueueBinding(){
//                            绑定     队列         到    交换机                        用的RoutingKey
        return BindingBuilder.bind(userInsertQueue()).to(userTopicExchange()).with(MessageMqConstants.MESSAGE_INSERT_KEY);
    }

    @Bean
    public Binding userDeleteQueueBinding(){
//                            绑定     队列         到    交换机                        用的RoutingKey
        return BindingBuilder.bind(userDeleteQueue()).to(userTopicExchange()).with(MessageMqConstants.MESSAGE_DELETE_KEY);
    }


}
