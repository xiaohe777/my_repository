package com.yjxxt.controller;

import com.yjxxt.base.BaseController;
import com.yjxxt.dto.TreeDto;
import com.yjxxt.service.ModuleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {


    @Resource
    private ModuleService moduleService;



    @RequestMapping("index")
    public String index (){
        return "module/module";
    }

    @RequestMapping("findModules")
    @ResponseBody
    public List<TreeDto> findModules(Integer roleId){
        return moduleService.findModulesByRoleId(roleId);
    }

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object>list(){
        return moduleService.moduleList();
    }
}
