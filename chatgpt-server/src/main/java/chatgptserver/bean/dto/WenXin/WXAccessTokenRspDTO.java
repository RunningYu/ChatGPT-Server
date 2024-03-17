package chatgptserver.bean.dto.WenXin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/3 15:52
 */
@ApiModel("访问凭证相应体")
@Data
public class WXAccessTokenRspDTO {

    @ApiModelProperty("有效期，Access Token的有效期。说明：单位是秒，有效期30天")
    private Integer expires_in;

    @ApiModelProperty("访问凭证token")
    private String access_token;

    @ApiModelProperty("错误码。说明：响应失败时返回该字段，成功时不返回")
    private String error;

    @ApiModelProperty("错误描述信息，帮助理解和解决发生的错误。说明：响应失败时返回该字段，成功时不返回")
    private String error_description;

    private String session_key;

    private String scope;

    private String session_secret;

    private String refresh_token;
}
