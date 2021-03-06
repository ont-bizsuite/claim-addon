package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class MessageCallbackDto {
    @ApiModelProperty(name="id",value = "id",required = true)
    private String id;

    @ApiModelProperty(name="ontid",value = "ontid",required = true)
    private String ontid;

    @ApiModelProperty(name="verified",value = "verified",required = true)
    private Boolean verified;

    @ApiModelProperty(name="publickey",value = "publickey",required = true)
    private String publickey;

    @ApiModelProperty(name="signature",value = "signature",required = true)
    private String signature;
}
