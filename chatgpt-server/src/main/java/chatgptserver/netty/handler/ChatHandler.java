package chatgptserver.netty.handler;

import chatgptserver.Mapping.ConvertMapping;
import chatgptserver.bean.ao.MessageResponseAO;
import chatgptserver.bean.po.MessagesPO;
import chatgptserver.bean.po.UserPO;
import chatgptserver.dao.UserMapper;
import chatgptserver.netty.Common.Result;
import chatgptserver.netty.command.ChatMessage;
import chatgptserver.netty.enums.MessageType;
import chatgptserver.netty.im.IMserver;
import chatgptserver.service.MessageService;
import chatgptserver.service.UserService;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import static chatgptserver.netty.enums.MessageType.GROUP;
import static chatgptserver.netty.enums.MessageType.PRIVATE;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2023/5/22
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ChatHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageService messageService;

//    @Autowired
//    private ServerMapper serverMapper;

    public void execute(ChannelHandlerContext ctx, TextWebSocketFrame frame) {

        ChatMessage chat = JSON.parseObject(frame.text(), ChatMessage.class);

        // 判断是否已经有同名的人（即同一个人）已经在线
        if (!IMserver.USERS.containsKey(chat.getToken())) {
            // 加到映射表里
            IMserver.USERS.put(chat.getToken(), ctx.channel());
        }

        UserPO sendUserPO = userService.getUserByCode(chat.getSenderCode());
        UserPO targetUserPO = userService.getUserByCode(chat.getTargetCode());
        MessagesPO messagesPO = ConvertMapping.buildMessage(chat, sendUserPO, targetUserPO);
        messagesPO.setRole(1);
        log.info("ChatHandler execute messagesPO:[{}]", messagesPO);


        // 封装要发送的消息体
//        MessageResponseAO msgResponse = buildMessageResponse(messageDTO, userDTO, targetUserDTO);
//        log.info("ChatHandler execute --> msgResponse:{}", msgResponse);
//        switch (MessageType.match(chat.getType())) {
//            // 私聊消息
//            case PRIVATE: {
//                log.info("ChatHandler execute PRIVATE");
//                if (StringUtil.isNullOrEmpty(chat.getTargetToken())) {
//                    ctx.channel().writeAndFlush(Result.fail("消息发送失败，targetToken=null，发送消息前请指定接收对象targetToken"));
//                    log.info("ChatHandler execute PRIVATE --> " + "消息发送失败，targetToken=null，发送消息前请指定接收对象targetToken");
//                    return;
//                }
//                String targetToken = targetUserDTO.getToken();
//                Channel targetChannel = IMserver.USERS.get(targetToken);
//                // 如果接收方在线，就写消息
//                if (null != targetChannel) {
//                    targetChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msgResponse)));
//                    log.info("ChatHandler execute  --> PRIVATE 私聊消息: " + JSON.toJSONString(msgResponse));
//                }
//                ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msgResponse)));
//                // 记录聊天信息
//                doMessage(messageDTO);
//                break;
//            }
//
//            // 群聊消息 ChannelGroup 发送消息，会给每一个注册进 ChannelGroup 中的channel发送消息
//            case GROUP: {
//                log.info("GROUP------------------------>群聊");
//                for (Channel channel : IMserver.USERS.values()) {
//                    if (channel == ctx.channel()) {
//                        continue;
//                    }
//                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msgResponse)));
//                }
//                doMessage(messageDTO);
//                break;
//            }
//            default: {
//                ctx.channel().writeAndFlush(Result.fail("不支持消息类型"));
//                log.info("ChatHandler execute 不支持消息类型 chat:{}", JSON.toJSONString(chat));
//                break;
//            }
//        }
    }
//
//    private MessageResponseAO buildMessageResponse(MessageDTO messageDTO, UserDTO userDTO, UserDTO targetUserDTO) {
//        MessageResponseAO messageResponse = new MessageResponse();
//        messageResponse.setStudentId(userDTO.getStudentId());
//        messageResponse.setUsername(userDTO.getUsername());
//        messageResponse.setHeadPicture(userDTO.getHeadPicture());
//        messageResponse.setTargetId(targetUserDTO.getStudentId());
//        messageResponse.setTargetUsername(targetUserDTO.getUsername());
//        messageResponse.setTargetHeadPicture(targetUserDTO.getHeadPicture());
//        messageResponse.setContent(messageDTO.getContent());
//        messageResponse.setToken(userDTO.getToken());
//        messageResponse.setTargetToken(targetUserDTO.getToken());
//        return messageResponse;
//    }
//
//    private void doMessage(MessagesPO messagesPO) {
//        messageService.insertMessage(messagesPO);
//        log.info("----------------> have inserted");
//        int have = messageService.haveChatRecord(messagesPO.getStudentId(), messagesPO.getTargetStudentId());
//        log.info("------------>have : " + have);
//        if (have == 0) {
//            messageService.chatRecord(messagesPO.getStudentId(), messagesPO.getTargetStudentId());
//        } else {
//            messageService.updateChatRecord(messagesPO.getStudentId(), messagesPO.getTargetStudentId(), messagesPO.getContent());
//        }
//    }
}
