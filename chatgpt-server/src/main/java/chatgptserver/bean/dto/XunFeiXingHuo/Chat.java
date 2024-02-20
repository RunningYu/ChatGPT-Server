package chatgptserver.bean.dto.XunFeiXingHuo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/12 9:28
 */
@Data
public class Chat {

    @ApiModelProperty(value = "需要使用的领域，模型：image", required = true)
    private String domain = "image";

    @ApiModelProperty(value = "核采样阈值,向上调整可以增加结果的随机程度，取值范围 (0，1] ，默认值0.5", required = false)
    private double temperature = 0.5;

    @ApiModelProperty(value = "从k个中随机选择一个(非等概率)，最小值1，最大值6，默认值4", required = false)
    private Integer top_k;

    @ApiModelProperty(value = "回答的tokens的最大长度 ，最小值是1, 最大值是8192，默认值2048", required = false)
    private Integer max_tokens = 4096;

    @ApiModelProperty(value = "内容审核的严格程度，strict表示严格审核策略；moderate表示中等审核策略；default表示默认的审核程度", required = false)
    private String auditing;

    @ApiModelProperty(value = "宽")
    private Integer width = 1024;

    @ApiModelProperty(value = "高")
    private Integer height = 1024;

}