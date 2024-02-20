package chatgptserver.bean.dto.WenXin;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 9:33
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WenXinRequestBodyDTO {

    /**
     * 聊天上下文信息。说明：
     * （1）messages成员不能为空，1个成员表示单轮对话，多个成员表示多轮对话
     * （2）最后一个message为当前请求的信息，前面的message为历史对话信息
     * （3）必须为奇数个成员，成员中message的role必须依次为user或function、assistant，第一个message的role不能是function
     * （4）最后一个message的content长度（即此轮对话的问题）不能超过9600个字符，且不能超过6000 tokens
     * （5）如果messages中content总长度大于9600个字符或6000 tokens，系统会依次遗忘最早的历史会话，
     *     直到content的总长度不超过9600个字符，且不超过6000 tokens
     */
    @ApiModelProperty(required = true)
    private List<WenXinReqMessagesDTO> messages;

}
