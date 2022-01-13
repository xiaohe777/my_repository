package com.yjxxt.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.base.BaseService;
import com.yjxxt.mapper.UserMapper;
import com.yjxxt.mapper.UserRoleMapper;
import com.yjxxt.model.UserModel;
import com.yjxxt.pojo.User;
import com.yjxxt.pojo.UserRole;
import com.yjxxt.query.UserQuery;
import com.yjxxt.utils.AssertUtil;
import com.yjxxt.utils.Md5Util;
import com.yjxxt.utils.PhoneUtil;
import com.yjxxt.utils.UserIDBase64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User, Integer> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     * 用户登录
     *
     * @param userName
     * @param userPwd
     * @return
     */
    public UserModel login(String userName, String userPwd) {
        //验证参数
        checkLoginParams(userName, userPwd);
        //查询对象
        User user = userMapper.queryUserByUserName(userName);
        //判断用是否存在
        AssertUtil.isTrue(null == user, "用户不存在");
        //用户存在，校验密码
        checkLoginPwd(userPwd, user.getUserPwd());
        //密码正确，返回用户信息
        return buildInfo(user);
    }

    /**
     * 构建返回的信息
     *
     * @param user
     * @return
     */
    private UserModel buildInfo(User user) {
        UserModel userModel = new UserModel();

        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());

        return userModel;
    }

    /**
     * 校验密码
     *
     * @param userPwd
     * @param userPwd1
     */
    private void checkLoginPwd(String userPwd, String userPwd1) {
        //加密
        userPwd = Md5Util.encode(userPwd);
        //比较加密后的密码
        AssertUtil.isTrue(!userPwd.equals(userPwd1), "密码错误");
    }

    /**
     * 校验参数
     *
     * @param userName
     * @param userPwd
     */
    private void checkLoginParams(String userName, String userPwd) {
        // 判断姓名
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户姓名不能为空！");
        // 判断密码
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "用户密码不能为空！");
    }

    @Transactional
    public void updateUserPassword(Integer userId, String oldPassword, String newPassword, String confirmPassword) {
        //通过id获取对象
        User user = userMapper.selectByPrimaryKey(userId);
        //校验
        checkPasswordParams(user, oldPassword, newPassword, confirmPassword);
        //设置信息
        user.setUserPwd(Md5Util.encode(newPassword));
        //修改密码
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "修改失败");
    }

    private void checkPasswordParams(User user, String oldPassword, String newPassword, String confirmPassword) {
        //用户是否为空
        AssertUtil.isTrue(null == user, "用户未登录或不存在");
        //原始密码、新密码、确认密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword), "原密码不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(newPassword), "新密码不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword), "确认密码不能为空");
        //原始密码是否正确
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPassword))), "原密码不正确");
        //新密码是否和原始密码一致
        AssertUtil.isTrue(oldPassword.equals(newPassword), "原密码和新密码不能一致");
        //确认密码是否和新密码一致
        AssertUtil.isTrue(!(newPassword.equals(confirmPassword)), "确认密码和新密码不一致");
    }


    public List<Map<String, Object>> findSales() {
        return userMapper.selectSales();
    }


    public Map<String, Object> queryUserByParams(UserQuery userQuery) {
        //实例化map
        Map<String, Object> map = new HashMap<>();
        //实例化分页
        PageHelper.startPage(userQuery.getPage(), userQuery.getLimit());
        //分页
        PageInfo<User> pageInfo = new PageInfo<>(userMapper.selectByParams(userQuery));
        //设置信息
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        //返回map
        return map;
    }

    /**
     * 添加用户
     *
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(User user) {
        //校验
        checkLoginParams(user);
        //给默认值
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123"));//默认密码123
        //添加
//        AssertUtil.isTrue(userMapper.insertSelective(user) < 0,"添加失败");
        AssertUtil.isTrue(insertHasKey(user) < 0, "添加失败");
        //添加角色分配
        System.out.println(user.getId() + "--" + user.getRoleIds());
        relationUserRole(user.getId(), user.getRoleIds());
    }

    private void relationUserRole(Integer userId, String roleIds) {
        //非空判断
        AssertUtil.isTrue(StringUtils.isBlank(roleIds), "请选择角色信息");
        //统计当前用户有多少个角色
        int count = userRoleMapper.countUserRoleNum(userId);
        //判断有没有角色信息
        if (count > 0) {
            //删除用户角色
            userRoleMapper.delUserRole(userId);
        }
        //容器
        List<UserRole> list = new ArrayList<>();
        //分割
        String[] roleId = roleIds.split(",");
        //遍历添加
        for (String rid : roleId) {
            //循环赋值
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(Integer.parseInt(rid));
            userRole.setCreateDate(new Date());
            userRole.setUpdateDate(new Date());
            //装进容器
            list.add(userRole);
        }
        //批量添加
        AssertUtil.isTrue(userRoleMapper.insertBatch(list) < list.size(), "添加失败");
    }

    private void checkLoginParams(User user) {
        //用户名为空吗
        AssertUtil.isTrue(StringUtils.isBlank(user.getUserName()), "用户名不能为空");
        //手机号有吗
        AssertUtil.isTrue(StringUtils.isBlank(user.getPhone()), "手机号不能为空");
        //邮箱有吗
        AssertUtil.isTrue(StringUtils.isBlank(user.getEmail()), "请输入邮箱地址");
        //数据库有这个人吗
        User temp = userMapper.queryUserByUserName(user.getUserName());
        if (null == user.getId()) {
            //添加
            AssertUtil.isTrue(null != temp, "用户已存在");
        } else {
            //修改
            AssertUtil.isTrue(null != temp && !(user.getId().equals(temp.getId())), "用户已存在");
        }
        //手机号校验
        AssertUtil.isTrue(!PhoneUtil.isMobile(user.getPhone()), "请输入合法手机号");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void changeUser(User user) {
        //查询是否有此用户
        User temp = userMapper.selectByPrimaryKey(user.getId());
        //判断是否存在
        AssertUtil.isTrue(null == temp, "用户不存在");
        //校验
        checkLoginParams(user);
        //给默认值
        user.setUpdateDate(new Date());
        //修改
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "修改失败");
        //更新角色分配
        Integer userId = userMapper.queryUserByUserName(user.getUserName()).getId();
        relationUserRole(userId, user.getRoleIds());
    }


    /**
     * 批量删除
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeSaleChance(Integer[] ids) {
        //校验
        AssertUtil.isTrue(null == ids || 0 == ids.length, "请选择数据");
        //操作
        AssertUtil.isTrue(deleteBatch(ids) < ids.length, "批量删除失败");
        //删除信息的同时把用户角色表里的信息也删除
        AssertUtil.isTrue(userRoleMapper.deleteBatch(ids) < ids.length, "删除角色不成功");
    }
}
