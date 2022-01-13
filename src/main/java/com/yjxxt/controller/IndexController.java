package com.yjxxt.controller;

import com.yjxxt.base.BaseController;
import com.yjxxt.mapper.PermissionMapper;
import com.yjxxt.pojo.User;
import com.yjxxt.service.UserService;
import com.yjxxt.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired(required = false)
    private PermissionMapper permissionMapper;

    /**
     * 系统登录页
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "index";
    }
    // 系统界面欢迎页
    @RequestMapping("welcome")
    public String welcome(){
        return "welcome";
    }
    /**
     * 后端管理主页面
     * @return
     */
    @RequestMapping("main")
    public String main(HttpServletRequest req){

        int userId = LoginUserUtil.releaseUserIdFromCookie(req);

        User user = userService.selectByPrimaryKey(userId);

        req.setAttribute("user",user);

        //用户拥有的资源权限码
        List<String> permissions = permissionMapper.selectUserHasRolesHasPermissions(userId);

        //存到session
        req.getSession().setAttribute("permissions",permissions);

        //转发
        return "main";
    }
}
