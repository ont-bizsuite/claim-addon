package com.ontology.controller;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.service.*;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Api(tags = "TA接口")
@RestController
@RequestMapping("/api/v1/ta")
@CrossOrigin
public class TAController {
    @Autowired
    private TaService appService;

    @ApiOperation(value = "register", notes = "register", httpMethod = "POST")
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDto req) throws Exception {
        String action = "register";
        Map<String, Object> result = appService.register(action,req);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @ApiOperation(value = "register callback", notes = "register callback", httpMethod = "POST")
    @PostMapping("/register/callback")
    public Result registerCallback(@RequestBody MessageCallbackDto req) throws Exception {
        String action = "registerCallback";
        String txHash = appService.registerCallback(action,req);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHash);
    }

    @ApiOperation(value = "register result", notes = "register result", httpMethod = "GET")
    @GetMapping("/register/result/{id}")
    public Result registerResult(@PathVariable String id) throws Exception {
        String action = "registerResult";
        return appService.registerResult(action,id);
    }

    @ApiOperation(value = "login", notes = "login", httpMethod = "POST")
    @PostMapping("/login")
    public Result login() throws Exception {
        String action = "login";
        Map<String, Object> result = appService.login(action);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @ApiOperation(value = "login callback", notes = "login callback", httpMethod = "POST")
    @PostMapping("/login/callback")
    public Result loginCallback(@RequestBody MessageCallbackDto req) throws Exception {
        String action = "loginCallback";
        String txHash = appService.loginCallback(action,req);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), txHash);
    }

    @ApiOperation(value = "login result", notes = "login result", httpMethod = "GET")
    @GetMapping("/login/result/{id}")
    public Result loginResult(@PathVariable String id) {
        String action = "loginResult";
        return appService.loginResult(action,id);
    }

    @ApiOperation(value = "user apply claim", notes = "user apply claim", httpMethod = "POST")
    @PostMapping("/claim")
    public Result applyClaim(@RequestBody ApplyClaimDto req) throws Exception {
        String action = "applyClaim";
        appService.applyClaim(action,req);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ErrorInfo.SUCCESS.descEN());
    }

    @ApiOperation(value = "qrcode for user to get claim", notes = "qrcode for user to get claim", httpMethod = "GET")
    @GetMapping("/claim")
    public Result getClaimQRCode() throws Exception {
        String action = "getClaimQRCode";
        JSONObject claimQRCode = appService.getClaimQRCode(action);
        return new Result(action,ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), claimQRCode);
    }

    @ApiOperation(value = "OntAuth callback and send claim to user", notes = "OntAuth callback and send claim to user", httpMethod = "POST")
    @PostMapping("/claim/callback")
    public JSONObject claimCallback(@RequestBody InvokeDto req) throws Exception {
        String action = "claimCallback";
        return appService.claimCallback(action,req);
    }

    @ApiOperation(value = "get param", notes = "get param", httpMethod = "GET")
    @GetMapping("/invoke/params/{id}")
    public String getParams(@PathVariable String id) throws Exception {
        String action = "getParams";
        return appService.getParams(action, id);
    }
}
