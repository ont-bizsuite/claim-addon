package com.ontology.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Result {
    @ApiModelProperty(name="action",value = "action",required = true)
    public String action;
    @ApiModelProperty(name="error",value = "error",required = true)
    public int error;
    @ApiModelProperty(name="desc",value = "desc",required = true)
    public String desc;
    @ApiModelProperty(name="result",value = "result",required = true)
    public Object result;
    @ApiModelProperty(name="version",value = "version",required = true)
    public String version;

    public Result() {
    }

    public Result(String action, int error, String desc, Object result) {
        this.action = action;
        this.error = error;
        this.desc = desc;
        this.result = result;
        this.version = "v1";
    }

}
