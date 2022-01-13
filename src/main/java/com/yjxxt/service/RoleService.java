package com.yjxxt.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.base.BaseService;
import com.yjxxt.mapper.ModuleMapper;
import com.yjxxt.mapper.PermissionMapper;
import com.yjxxt.mapper.RoleMapper;
import com.yjxxt.pojo.Permission;
import com.yjxxt.pojo.Role;
import com.yjxxt.query.RoleQuery;
import com.yjxxt.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RoleService extends BaseService<Role, Integer> {

    @Autowired(required = false)
    private RoleMapper roleMapper;

    @Autowired(required = false)
    private PermissionMapper permissionMapper;

    @Autowired(required = false)
    private ModuleMapper moduleMapper;

    /**
     * 查询角色列表
     *
     * @return
     */
    public List<Map<String, Object>> queryAllRoles(Integer userId) {
        return roleMapper.queryAllRoles(userId);
    }

    /**
     * 角色的条件查询以及分页
     * @param roleQuery
     * @return
     */
    public Map<String, Object> findRoleByParam(RoleQuery roleQuery) {
        //容器
        Map<String, Object> map = new HashMap<>();
        //分页
        PageHelper.startPage(roleQuery.getPage(), roleQuery.getLimit());
        PageInfo pageInfo = new PageInfo(roleMapper.selectByParams(roleQuery));
        //赋值
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        //返回
        return map;
    }

    /**
     * 添加角色
     * @param role
     */
    public void addRole(Role role){
        //校验
        //数据是否为空
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输入角色名");
        //是否已经存在
        Role temp = roleMapper.selectRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(null != temp,"角色名已经存在");
        //赋值
        role.setIsValid(1);
        role.setUpdateDate(new Date());
        role.setCreateDate(new Date());
        AssertUtil.isTrue(insertHasKey(role) < 0,"添加失败");
    }

    /**
     * 修改角色
     * @param role
     */
    public void updateRole(Role role){
        //校验
        //数据是否为空
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输入角色名");
        //必须存在才能修改
        Role temp = roleMapper.selectByPrimaryKey(role.getId());
        AssertUtil.isTrue(null == temp,"角色不存在");
        //角色名唯一
        Role tempAnother = roleMapper.selectRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(null != tempAnother && tempAnother.getId() != role.getId(),"角色已经存在");
        //修改
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(updateByPrimaryKeySelective(role) < 1,"添加失败");
    }

    /**
     * 删除角色
     * @param role
     */
    public void removeRoleById(Role role) {
        //必须存在才能删除
        Role temp = roleMapper.selectByPrimaryKey(role.getId());
        AssertUtil.isTrue(null == role.getId() || null == temp,"角色不存在");
        //设定值
        role.setIsValid(0);
        role.setUpdateDate(new Date());
        //删除
        AssertUtil.isTrue(updateByPrimaryKeySelective(role) < 1,"删除失败");
    }

    /**
     * 批量添加权限
     * @param mids
     * @param roleId
     */
    public void addGrant(Integer[] mids,Integer roleId){
        //校验角色存在不存在
        //如果角色存在原始权限，删除原始权限
        //添加新的权限
        Role temp = selectByPrimaryKey(roleId);
        AssertUtil.isTrue(null == roleId || null == temp,"授权角色不存在");
        int count = permissionMapper.countPermissionByRoleId(roleId);
        if(count > 0) {
            AssertUtil.isTrue(permissionMapper.deletePermissionByRoleId(roleId) < count,"权限分配失败");
        }
        if(null != mids && mids.length > 0) {
            List<Permission> list = new ArrayList<>();
            for (Integer mid:mids) {
                //实例化对象
                Permission permission = new Permission();
                permission.setUpdateDate(new Date());
                permission.setCreateDate(new Date());
                permission.setModuleId(mid);
                permission.setRoleId(roleId);
                //权限码
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());
                list.add(permission);
            }
            permissionMapper.insertBatch(list);
        }
    }
}
