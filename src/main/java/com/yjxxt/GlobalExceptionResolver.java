package com.yjxxt;

import com.alibaba.fastjson.JSON;
import com.yjxxt.base.ResultInfo;
import com.yjxxt.exceptions.NoLoginException;
import com.yjxxt.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * 全局异常处理
 */
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest req,
                                         HttpServletResponse resp, Object handler, Exception ex) {
        //未登录异常处理
        if(ex instanceof NoLoginException) {
            ModelAndView mav = new ModelAndView("redirect:/index");
            return mav;
        }

        //设置默认异常处理
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("code",400);
        mav.addObject("msg","系统异常，请稍后再试");

        //判断handler
        if(handler instanceof HandlerMethod) {
            //类型转换
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //拿到responseBody注解
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            //判断ResponseBody是否存在，如果存在，返回的是json，若不存在，返回试图
            if(null == responseBody) {
                //返回试图
                if(ex instanceof ParamsException) {
                    ParamsException pe = (ParamsException) ex;
                    mav.addObject("code",pe.getCode());
                    mav.addObject("msg",pe.getMsg());
                }
                return mav;
            }else {
                //返回json
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统异常，请重试");
                //若捕获的是自定义异常
                if(ex instanceof ParamsException) {
                    ParamsException pe = (ParamsException) ex;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }
                //设置响应格式和编码格式(json格式)
                resp.setContentType("application/json;charset=utf-8");
                //得到输出流
                PrintWriter out = null;
                try {
                    out = resp.getWriter();
                    //将响应格式转为json格式，通过输出流输出，响应给请求的前台
                    out.write(JSON.toJSONString(resultInfo));
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(out != null) {
                        out.close();
                    }
                }
                return null;
            }
        }
        return mav;
    }
}
