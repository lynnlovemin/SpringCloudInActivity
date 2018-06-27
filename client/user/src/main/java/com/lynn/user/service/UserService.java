package com.lynn.user.service;

import com.lynn.common.result.Code;
import com.lynn.common.result.SingleResult;
import com.lynn.common.service.BaseService;
import com.lynn.user.mapper.UserMapper;
import com.lynn.user.model.bean.UserBean;
import com.lynn.user.model.request.LoginRequest;
import com.lynn.user.model.request.RegisterRequest;
import com.lynn.user.model.response.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
@Service
public class UserService extends BaseService{

    @Autowired
    private UserMapper userMapper;

    public SingleResult<TokenResponse> login(LoginRequest request){
        List<UserBean> userList = userMapper.selectUser(request.getMobile(),request.getPassword());
        if(null != userList && userList.size() > 0){
            String token = getToken(request.getMobile(),request.getPassword());
            TokenResponse response = new TokenResponse();
            response.setToken(token);
            return SingleResult.buildSuccess(response);
        }else {
            return SingleResult.buildFailure(Code.ERROR,"手机号或密码输入不正确！");
        }
    }

    public SingleResult<TokenResponse> register(RegisterRequest request){
        List<UserBean> userList = userMapper.selectUser(request.getMobile());
        if(null != userList && userList.size() > 0){
            String token = getToken(request.getMobile(),request.getPassword());
            TokenResponse response = new TokenResponse();
            response.setToken(token);
            return SingleResult.buildSuccess(response);
        }else {
            return SingleResult.buildFailure(Code.ERROR,"手机号或密码输入不正确！");
        }
    }
}
