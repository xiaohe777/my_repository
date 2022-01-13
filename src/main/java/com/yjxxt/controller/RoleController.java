package com.yjxxt.controller;

import com.yjxxt.annotation.RequiredPermission;
import com.yjxxt.base.BaseController;
import com.yjxxt.base.ResultInfo;
import com.yjxxt.pojo.Role;
import com.yjxxt.query.RoleQuery;
import com.yjxxt.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@RequestMapping("role")
@Controller
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;


    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String, Object>> queryAllRoles(Integer userId) {
        return roleService.queryAllRoles(userId);
    }

    /**
     * 角色的条件查询
     * @param roleQuery
     * @return
     */
    @RequiredPermission(code = "60")
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> list(RoleQuery roleQuery) {
        return roleService.findRoleByParam(roleQuery);
    }


    @RequestMapping("index")
    public String index() {
        return "role/role";
    }

    @RequestMapping("toRoleGrantPage")
    public String toRoleGrantPage(Integer roleId,Model model) {
        model.addAttribute("roleId",roleId);
        return "role/grant";
    }

    /**
     * 角色添加
     * @param role
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(Role role) {
        roleService.addRole(role);
        return success("添加成功");
    }

    /**
     * 权限添加
     * @param
     * @return
     */
    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer[] mids,Integer roleId) {
        roleService.addGrant(mids,roleId);
        return success("添加成功");
    }

    /**
     * 角色删除
     * @param role
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo delete(Role role) {
        roleService.removeRoleById(role);
        return success("删除成功");
    }

    /**
     * 角色修改
     * @param role
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(Role role) {
        roleService.updateRole(role);
        return success("修改成功");
    }

    /**
     * 跳转添加修改页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateRolePage")
    public String addOrUpdateRolePage(Integer id, Model model){
        //根据id查信息
        Role role = roleService.selectByPrimaryKey(id);
        //添加属性
        if(null != role) {
            model.addAttribute("role",role);
        }
        return "role/add_update";
    }
}
