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
//    "refresh_token":"25.2b3ffbf4868de88db3fa6e50dffe9b37.315360000.2019628296.282335-46317365","expires_in":2592000,"" +
//    "session_key":"9mzdA5ghBbIqNug7Fi9oL\/Ds01RQMgjMpffBnPhKVhjZ2FoROyAx0Q8+D\/5CICT+qwbZwoJ6c1ozeFpwqt5O1e6W9pn8Cw==",
//    "access_token":"24.821d8e48185313efda975f1c6a92b0b6.2592000.1706860296.282335-46317365",
//    "scope":"public brain_all_scope ai_custom_yiyan_com ai_custom_yiyan_com_eb_instant wenxinworkshop_mgr ai_custom_yiyan_com_bloomz7b1 ai_custom_yiyan_com_emb_text ai_custom_yiyan_com_llama2_7b ai_custom_yiyan_com_llama2_13b ai_custom_yiyan_com_llama2_70b ai_custom_yiyan_com_chatglm2_6b_32k ai_custom_yiyan_com_aquilachat_7b ai_custom_yiyan_com_emb_bge_large_zh ai_custom_yiyan_com_emb_bge_large_en ai_custom_yiyan_com_qianfan_chinese_llama_2_7b ai_custom_qianfan_bloomz_7b_compressed ai_custom_yiyan_com_eb_pro ai_custom_yiyan_com_sd_xl ai_custom_yiyan_com_8k ai_custom_yiyan_com_ai_apaas ai_custom_yiyan_com_qf_chinese_llama_2_13b ai_custom_yiyan_com_sqlcoder_7b ai_custom_yiyan_com_codellama_7b_ins ai_custom_yiyan_com_xuanyuan_70b_chat ai_custom_yiyan_com_yi_34b ai_custom_yiyan_com_chatlaw ai_custom_yiyan_com_emb_tao_8k wise_adapt lebo_resource_base lightservice_public hetu_basic lightcms_map_poi kaidian_kaidian ApsMisTest_Test\u6743\u9650 vis-classify_flower lpq_\u5f00\u653e cop_helloScope ApsMis_fangdi_permission smartapp_snsapi_base smartapp_mapp_dev_manage iop_autocar oauth_tp_app smartapp_smart_game_openapi oauth_sessionkey smartapp_swanid_verify smartapp_opensource_openapi smartapp_opensource_recapi fake_face_detect_\u5f00\u653eScope vis-ocr_\u865a\u62df\u4eba\u7269\u52a9\u7406 idl-video_\u865a\u62df\u4eba\u7269\u52a9\u7406 smartapp_component smartapp_search_plugin avatar_video_test b2b_tp_openapi b2b_tp_openapi_online smartapp_gov_aladin_to_xcx",
//    "session_secret":"8075253c4f1f75735c904ded88547ee4"}

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
