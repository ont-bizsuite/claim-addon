package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ApplyClaimDto {
    @ApiModelProperty(name="ontid",value = "ontid")
    private String ontid;
    @ApiModelProperty(name="name",value = "name")
    private String name;
    @ApiModelProperty(name="age",value = "age")
    private int age;
    @ApiModelProperty(name="answer",value = "answer")
    private Boolean answer;
}
