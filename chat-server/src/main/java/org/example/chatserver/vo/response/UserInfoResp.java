package org.example.chatserver.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户详情")
public class UserInfoResp {

    @ApiModelProperty(value = "用户id")
    private Long id;

    @ApiModelProperty(value = "用户昵称")
    private String name;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "性别 1为男性，2为女性")
    private Integer sex;

    @ApiModelProperty(value = "剩余改名次数")
    private Integer modifyNameChance;

}
