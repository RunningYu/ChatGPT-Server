package chatgptserver.bean.dto.WenXin;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/26
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WenXinImageCreateRequestDTO {

    private String prompt;

    private String negative_prompt;

    private String size;

    private Integer n;

    private String sampler_index;

}

//"prompt": "cat",
//        "negative_prompt": "white",
//        "size": "1024x1024",
//        "steps": 20,
//        "n": 2,
//        "sampler_index": "DPM++ SDE Karras"
