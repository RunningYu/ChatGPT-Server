package chatgptserver.bean.dto.tongYiQianWen;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/28
 */

@Data
public class ImageCreateParameters {

    private String size;

    @ApiModelProperty(value = "本次请求生成的图片数量，目前支持1~4张，默认为1。", required = false)
    private Integer n;


    private Integer seed;
}
