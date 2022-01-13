package com.yjxxt.controller;


import com.yjxxt.base.BaseController;
import com.yjxxt.base.ResultInfo;
import com.yjxxt.model.UserModel;
import com.yjxxt.pojo.User;
import com.yjxxt.query.UserQuery;
import com.yjxxt.service.UserService;
import com.yjxxt.utils.LoginUserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RequestMapping("user")
@Controller
public class UserController extends BaseController {

    @Resource
    private UserService userService;


    @RequestMapping("login")
    @ResponseBody
    public ResultInfo login(String userName, String userPwd) {
        ResultInfo resultInfo = new ResultInfo();

        UserModel model = userService.login(userName, userPwd);
        resultInfo.setResult(model);

        return resultInfo;
    }

    @RequestMapping("toSettingPage")
    public String setting(HttpServletRequest req) {
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
        User user = userService.selectByPrimaryKey(userId);
        req.setAttribute("user", user);
        return "user/setting";
    }

    @PostMapping("setting")
    @ResponseBody
    public ResultInfo setUpdate(User user) {
        ResultInfo resultInfo = new ResultInfo();
        //修改信息
        userService.updateByPrimaryKeySelective(user);
        return resultInfo;
    }

    @PostMapping("updatePwd")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request, String oldPassword,
                                         String newPassword, String confirmPassword) {
        ResultInfo resultInfo = new ResultInfo();
        //通过cookie获取用户id
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //调用修改方法
        userService.updateUserPassword(userId, oldPassword, newPassword, confirmPassword);
        return resultInfo;
    }

    @RequestMapping("toPasswordPage")
    public String toPasswordPage() {
        return "user/password";
    }


    @RequestMapping("addOrUpdateUserPage")
    public String addOrUpdateUserPage(Integer id, Model model) {
        //判断是否id有值
        if(null != id) {
            model.addAttribute("user",userService.selectByPrimaryKey(id));
        }
        return "user/add_update";
    }


    /**
     * 进入用户页面
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "user/user";
    }

    @RequestMapping("sales")
    @ResponseBody
    public List<Map<String, Object>> querySales() {
        return userService.findSales();
    }

    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(User user) {
        //添加
        userService.saveUser(user);
        //返回结果
        return success();//父类BaseController里的success函数，直接返回一个resultInfo
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(User user) {
        //添加
        userService.changeUser(user);
        //返回结果
        return success();//父类BaseController里的success函数，直接返回一个resultInfo
    }

    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> list(UserQuery userQuery) {
        return userService.queryUserByParams(userQuery);
    }


    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo delete(Integer[] ids) {
        //操作
        userService.removeSaleChance(ids);
        //返回
        return success("操作成功");
    }
}
