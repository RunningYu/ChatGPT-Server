package chatgptserver.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageCreateDTO {

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("聊天code")
    private String chatCode;

    @ApiModelProperty("图片描述")
    private String content;

    @ApiModelProperty("图片链接")
    private String url;

}
