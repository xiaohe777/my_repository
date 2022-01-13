package com.yjxxt.mapper;

import com.yjxxt.base.BaseMapper;
import com.yjxxt.pojo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {


    int countPermissionByRoleId(Integer roleId);

    int deletePermissionByRoleId(Integer roleId);

    List<Integer> selectModulesByRoleId(Integer roleId);

    List<String> selectUserHasRolesHasPermissions(Integer userId);
}