package com.ontology.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.entity.Claim;
import com.ontology.entity.Invoke;
import com.ontology.entity.Login;
import com.ontology.entity.Register;
import com.ontology.exception.OntIdException;
import com.ontology.mapper.ClaimMapper;
import com.ontology.mapper.InvokeMapper;
import com.ontology.mapper.LoginMapper;
import com.ontology.mapper.RegisterMapper;
import com.ontology.service.TaService;
import com.ontology.utils.*;
import io.ont.addon.claim.sdk.common.ClaimMap;
import io.ont.addon.claim.sdk.common.ClaimResult;
import io.ont.addon.claim.sdk.common.ClaimTemplate;
import io.ont.addon.claim.sdk.common.ClaimUtility;
import io.ont.addon.claim.sdk.issuer.Application;
import io.ont.addon.claim.sdk.issuer.Issuer;
import io.ont.addon.signing.sdk.SigningSdk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Executors;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class TaServiceImpl implements TaService {
    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private ClaimMapper claimMapper;
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private InvokeMapper invokeMapper;

    private SigningSdk signingSdk;

    private Issuer issuer;

    @PostConstruct
    public void init() throws Exception {
        // init signingSdk
        signingSdk = SigningSdk.getInstance(configParam.SIGNING_SERVER_URL);
        signingSdk.init("ericyansdemo.app.ont", "L49zU8WTRVqJY3V3q1pt3pRBkuH3dUWd6tCBDnScVCZUpTVeR6E3");

        // edit ClaimTemplate
        ClaimTemplate test = new ClaimTemplate();
        test.id = "Single TA";
        test.context = "Single TA";
        test.issuerNamespace = "single.app.ont";
        test.view = new URI("http://view.com");

        Map<String, ClaimTemplate.typeView> map = new HashMap<>();
        ClaimTemplate.typeView type1 = new ClaimTemplate.typeView();
        type1.type = ClaimTemplate.type.STRING;
        type1.description = "owner's name";
        map.put("name", type1);
        ClaimTemplate.typeView type2 = new ClaimTemplate.typeView();
        type2.type = ClaimTemplate.type.INT;
        type2.description = "owner's age";
        map.put("age", type2);
        ClaimTemplate.typeView type3 = new ClaimTemplate.typeView();
        type3.type = ClaimTemplate.type.STRING;
        type3.description = "owner's level";
        map.put("level", type3);
        test.properties = map;

        // add ClaimTemplate
        Application.addClaimTemplate(test);
        // init with claim issuer privateKey and gas payer privateKey
        Application.init("your app domain", "issuer private key", "payer private key");
        // set node url
        Application.setRestfulUrl(configParam.RESTFUL_URL);

        issuer = new Issuer();
    }

    @Override
    public Map<String, Object> register(String action, RegisterDto req) throws Exception {
        return commonRegister(action, req, Constant.ACTION_REGISTER);
    }

    private Map<String, Object> commonRegister(String action, RegisterDto req, String actionRegister) throws Exception {
        String userName = req.getUserName();
        String id = UUID.randomUUID().toString();
        // check duplicate
        Register register = new Register();
        register.setUserName(userName);
        register.setState(Constant.STATE_SUCCESS);
        List<Register> list = registerMapper.select(register);
        if (!CollectionUtils.isEmpty(list)) {
            throw new OntIdException(action, ErrorInfo.USER_ALREADY_EXIST.descCN(), ErrorInfo.USER_ALREADY_EXIST.descEN(), ErrorInfo.USER_ALREADY_EXIST.code());
        }

        register.setId(id);
        register.setState(null);
        register.setCreateTime(new Date());
        registerMapper.insertSelective(register);

        List<Map<String, Object>> argsList = new ArrayList<>();
        Map<String, Object> arg0 = new HashMap<>();
        arg0.put("name", "register");
        arg0.put("value", "ontid");
        argsList.add(arg0);
        String params = signingSdk.constructMessage(argsList);
        Invoke invoke = new Invoke();
        invoke.setId(id);
        invoke.setParams(params);
        invokeMapper.insertSelective(invoke);

        String signature = signingSdk.sign(params);
        Map<String, Object> qrCodeParams = signingSdk.invoke(actionRegister, id);
        qrCodeParams.put("signature", signature);

        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("qrCode", qrCodeParams);

        return result;
    }

    @Override
    public String registerCallback(String action, MessageCallbackDto req) {
        return commonRegisterCallback(action, req);
    }

    private String commonRegisterCallback(String action, MessageCallbackDto req) {
        String id = req.getId();
        Boolean verified = req.getVerified();
        String ontid = req.getOntid();
        Register register = registerMapper.selectByPrimaryKey(id);
        if (register == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        if (verified) {
            // check duplicate
            String userName = register.getUserName();
            List<Register> list = registerMapper.selectByOntidAndUserName(ontid, userName);
            if (!CollectionUtils.isEmpty(list)) {
                register.setState(Constant.REGISTER_STATE_ALREADY_EXIST);
            } else {
                register.setOntid(ontid);
                register.setState(Constant.STATE_SUCCESS);
            }
        } else {
            register.setState(Constant.STATE_FAILURE);
        }
        registerMapper.updateByPrimaryKeySelective(register);
        return null;
    }

    @Override
    public Result registerResult(String action, String id) {
        Map<String, Object> result = new HashMap<>();

        Register register = registerMapper.selectByPrimaryKey(id);
        if (register == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        Integer state = register.getState();
        if (state != null) {
            result.put("result", state.toString());
            result.put("ontid", register.getOntid());
            result.put("userName", register.getUserName());
        } else {
            result.put("result", null);
        }

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @Override
    public Map<String, Object> login(String action) throws Exception {
        String id = UUID.randomUUID().toString();
        Login login = new Login();
        login.setId(id);
        login.setCreateTime(new Date());
        loginMapper.insertSelective(login);

        List<Map<String, Object>> argsList = new ArrayList<>();
        Map<String, Object> arg0 = new HashMap<>();
        arg0.put("name", "login");
        arg0.put("value", "String:ontid");
        argsList.add(arg0);
        String params = signingSdk.constructMessage(argsList);
        Invoke invoke = new Invoke();
        invoke.setId(id);
        invoke.setParams(params);
        invokeMapper.insertSelective(invoke);

        String signature = signingSdk.sign(params);
        Map<String, Object> qrCodeParams = signingSdk.invoke(Constant.ACTION_LOGIN, id);
        qrCodeParams.put("signature", signature);
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("qrCode", qrCodeParams);
        return result;
    }

    @Override
    public String loginCallback(String action, MessageCallbackDto req) {
        String id = req.getId();
        Boolean verified = req.getVerified();
        String ontid = req.getOntid();
        Login login = loginMapper.selectByPrimaryKey(id);
        if (login == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        if (verified) {
            // 查找注册信息
            Register register = new Register();
            register.setOntid(ontid);
            register = registerMapper.selectOne(register);
            if (register == null) {
                login.setState(Constant.LOGIN_STATE_NOT_REGISTER);
            } else {
                login.setOntid(ontid);
                login.setState(Constant.STATE_SUCCESS);
                login.setUserName(register.getUserName());
            }
        } else {
            login.setState(Constant.STATE_FAILURE);
        }
        loginMapper.updateByPrimaryKey(login);
        return null;
    }

    @Override
    public Result loginResult(String action, String id) {
        Map<String, Object> result = new HashMap<>();

        Login login = loginMapper.selectByPrimaryKey(id);
        if (login == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        Integer state = login.getState();
        if (state != null) {
            result.put("result", state.toString());
            result.put("ontid", login.getOntid());
            result.put("userName", login.getUserName());
        } else {
            result.put("result", null);
        }

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @Override
    public void applyClaim(String action, ApplyClaimDto dto) throws Exception {
        String ontid = dto.getOntid();
        String name = dto.getName();
        int age = dto.getAge();
        Boolean answer = dto.getAnswer();
        Executors.newCachedThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (answer) {
                        Map<String, Object> properties = new HashMap<>();
                        properties.put("name", name);
                        properties.put("age", age);
                        properties.put("level", "master");
                        long year = 1000 * 60 * 60 * 24 * 365L;
                        Date expire = new Date(System.currentTimeMillis() + year);

                        // issue claim
                        ClaimResult result = issuer.generateClaimByTemplate("Single TA", ontid, properties, expire);
                        String claimId = result.claimId;
                        String txHash = result.txHash;
                        String claimStr = result.claimStr;

                        // save claim info
                        Claim claim = new Claim();
                        claim.setOntid(ontid);
                        claim.setClaimId(claimId);
                        claim.setClaim(claimStr);
                        claim.setTxHash(txHash);
                        claim.setCreateTime(new Date());
                        claimMapper.insertSelective(claim);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public JSONObject getClaimQRCode(String action) throws Exception {
        String id = UUID.randomUUID().toString();
        String data = issuer.constructData("Single TA", "Single Icon");
        String callbackUrl = String.format(configParam.CALLBACK_URL, "api/v1/ta/claim/callback");
        String dataUrl = String.format(configParam.CALLBACK_URL, "api/v1/ta/invoke/params/" + id);
        String signature = issuer.sign(data);
        String qrCode = issuer.qrCodeParams(id, signature, "", dataUrl, callbackUrl, false);

        Invoke invoke = new Invoke();
        invoke.setId(id);
        invoke.setParams(data);
        invokeMapper.insertSelective(invoke);

        return JSONObject.parseObject(qrCode);
    }

    @Override
    public JSONObject claimCallback(String action, InvokeDto req) throws Exception {
        String ontid = req.getOntid();
        String signedTx = req.getSignedTx();
        boolean flag = ClaimUtility.verifySignature(ontid, signedTx);
        if (flag) {
            Claim claim = new Claim();
            claim.setOntid(ontid);
            List<Claim> list = claimMapper.select(claim);
            if (!CollectionUtils.isEmpty(list)) {
                List<ClaimMap> claimList = new ArrayList<>();
                for (Claim one : list) {
                    ClaimMap map = new ClaimMap();
                    map.setClaim(one.getClaim());
                    map.setCreateTime(one.getCreateTime());
                    map.setType("register proof");
                    map.setDescription("this claim can help you register in Hello World");
                    claimList.add(map);
                }
                // set result
                String claimStr = issuer.claimResult(claimList);
                return JSONObject.parseObject(claimStr);
            }

        } else {
            throw new OntIdException(action, ErrorInfo.IDENTITY_VERIFY_FAILED.descCN(), ErrorInfo.IDENTITY_VERIFY_FAILED.descEN(), ErrorInfo.IDENTITY_VERIFY_FAILED.code());
        }

        // set result if there is no claim for user
        String noClaimResult = issuer.noClaimResult();
        return JSONObject.parseObject(noClaimResult);
    }

    @Override
    public Result claimResult(String action, String id) {
        Map<String, Object> result = new HashMap<>();

        Claim claim = claimMapper.selectByPrimaryKey(id);
        if (claim == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }


        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @Override
    public String getParams(String action, String id) {
        Invoke exist = invokeMapper.selectByPrimaryKey(id);
        if (exist == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        return exist.getParams();
    }

}
