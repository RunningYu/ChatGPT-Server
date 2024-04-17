package chatgptserver.enums.mq;

import io.swagger.annotations.ApiModel;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/17
 */
@ApiModel(description = "定义 message 交换机的常量")
public class MessageMqConstants {

    /**
     * 交换机
     */
    public final static String MESSAGE_EXCHANGE = "message.topic";

    /**
     * 监听新增和修改的队列
     */
    public final static String MESSAGE_INSERT_QUEUE = "message.insert.queue";

    /**
     * 监听删除的队列
     */
    public final static String MESSAGE_DELETE_QUEUE = "message.delete.queue";

    /**
     * 新增或修改的RoutingKey
     */
    public final static String MESSAGE_INSERT_KEY = "message.insert";

    /**
     * 删除的RoutingKey
     */
    public final static String MESSAGE_DELETE_KEY = "message.delete";


}
