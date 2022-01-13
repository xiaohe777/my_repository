package com.yjxxt.aop;

import com.yjxxt.annotation.RequiredPermission;
import com.yjxxt.exceptions.NoAuthException;
import com.yjxxt.exceptions.NoLoginException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.List;

@Aspect
@Component
public class PermissionProxy {


    @Autowired
    private HttpSession session;


    @Around( value = "@annotation(com.yjxxt.annotation.RequiredPermission)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        //判断是否登录
        List<String> permissions = (List<String>) session.getAttribute("permissions");
        if(permissions == null || permissions.size() == 0) {
            throw new NoLoginException("未登录");
        }
        //判断是否拥有目标资源的权限码
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        RequiredPermission requiredPermission = signature.getMethod().getDeclaredAnnotation(RequiredPermission.class);
        if(!(permissions.contains(requiredPermission.code()))) {
            throw new NoAuthException("无权限");
        }
        Object result = pjp.proceed();
        return result;
    }
}
