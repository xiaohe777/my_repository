package com.yjxxt.interceptors;


import com.yjxxt.exceptions.NoLoginException;
import com.yjxxt.service.UserService;
import com.yjxxt.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 非法请求拦截
 */
public class NoLoginInterceptor extends HandlerInterceptorAdapter {


    @Autowired
    private UserService userService;


    /**
     * 判断是否为登录状态，若不是，重定向到登录界面
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie中的用户id
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);

        if(null == userId || (null == userService.selectByPrimaryKey(userId))) {
            //id为空或查不到记录,抛出未登录异常
            throw new NoLoginException();
        }
        return true;
    }
}
