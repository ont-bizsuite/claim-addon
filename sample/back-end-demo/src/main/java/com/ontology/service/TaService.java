package com.ontology.service;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;

import java.util.Map;


public interface TaService {

    Map<String, Object> register (String action, RegisterDto req) throws Exception;

    String registerCallback(String action, MessageCallbackDto req) throws Exception;

    Result registerResult(String action, String id);

    Map<String, Object> login(String action) throws Exception;

    String loginCallback(String action, MessageCallbackDto req);

    Result loginResult(String action, String id);

    Result claimResult(String action, String id);

    void applyClaim(String action, ApplyClaimDto dto) throws Exception;

    JSONObject getClaimQRCode(String action) throws Exception;

    JSONObject claimCallback(String action, InvokeDto req) throws Exception;

    String getParams(String action, String id);
}
