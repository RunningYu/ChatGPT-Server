package chatgptserver.bean.dto.XunFeiXingHuo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/12 9:28
 */
@AllArgsConstructor
@Data
public class Parameter {

    @ApiModelProperty(value = "用于上传对话的参数信息", required = true)
    private Chat chat;

}