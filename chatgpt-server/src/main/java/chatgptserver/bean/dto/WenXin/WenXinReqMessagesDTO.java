package chatgptserver.bean.dto.WenXin;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 9:34
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WenXinReqMessagesDTO {

    /**
     * 当前支持以下：
     * user: 表示用户
     * assistant: 表示对话助手
     * function: 表示函数
     */
    @ApiModelProperty(required = true)
    private String role;

    /**
     * 对话内容，说明：
     * （1）当前message存在function_call，且role="assistant"时可以为空，其他场景不能为空
     * （2）最后一个message对应的content不能为blank字符，包含空格、"\n"、“\r”、“\f”等
     */
    @ApiModelProperty(required = true)
    private String content;

    /**
     * message作者；当role=function时，必填，且是响应内容中function_call中的name
     */
    @ApiModelProperty(required = false)
    private String name;



}
