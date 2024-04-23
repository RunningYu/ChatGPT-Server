package chatgptserver.bean.ao.ppt;

import chatgptserver.bean.dto.XunFeiXingHuo.pptCreate.PptOutlineResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PptCreateRequestAO {

    @ApiModelProperty("大纲")
    private PptOutlineResponse outline;

    @ApiModelProperty("颜色主题")
    private String colorTheme;

    @ApiModelProperty("问题")
    private String content;

    @ApiModelProperty("ppt生成监听的sid")
    private String sid;

    @ApiModelProperty("true：重新生成")
    private Boolean isRebuild;

    @ApiModelProperty("停止请求标识（时间戳）")
    private String cid;

    private String userCode;
}
