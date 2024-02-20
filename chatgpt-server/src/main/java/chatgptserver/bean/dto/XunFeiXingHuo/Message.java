package chatgptserver.bean.dto.XunFeiXingHuo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/12 9:31
 */
@Data
@AllArgsConstructor
public class Message {

    @ApiModelProperty(value = "文本数据，受Token限制，有效内容不能超过8192Token", required = true)
    private List<Text> text;

}
