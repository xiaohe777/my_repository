package com.yjxxt.service;

import com.yjxxt.base.BaseService;
import com.yjxxt.dto.TreeDto;
import com.yjxxt.mapper.ModuleMapper;
import com.yjxxt.mapper.PermissionMapper;
import com.yjxxt.pojo.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yjxxt.pojo.Module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {


    @Autowired(required = false)
    private ModuleMapper moduleMapper;

    @Autowired(required = false)
    private PermissionMapper permissionMapper;

    /**
     * 查询所有资源
     * @return
     */
    public List<TreeDto> findModules(){
        return moduleMapper.selectModules();
    }

    /**
     * 查询角色已有的资源
     * @return
     */
    public List<TreeDto> findModulesByRoleId(Integer roleId){
        //获取所有的资源
        List<TreeDto> treeDtos = moduleMapper.selectModules();
        //获取当前角色拥有的资源信息
        List<Integer> roleHasModules = permissionMapper.selectModulesByRoleId(roleId);
        //遍历
        for (TreeDto td:treeDtos) {
            if(roleHasModules.contains(td.getId())) {
                //如果包含，说明该角色有此权限
                td.setChecked(true);
            }
        }
        return treeDtos;
    }

    public Map<String,Object> moduleList(){
        Map<String,Object> result = new HashMap<>();
        List<Module> module = moduleMapper.queryModule();
        //赋值
        result.put("code",0);
        result.put("msg","success");
        result.put("count",module.size());
        result.put("data",module);
        //返回结果
        return result;
    }
}
