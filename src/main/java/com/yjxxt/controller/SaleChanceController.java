package com.yjxxt.controller;


import com.yjxxt.base.BaseController;
import com.yjxxt.base.ResultInfo;
import com.yjxxt.pojo.SaleChance;
import com.yjxxt.query.SaleChanceQuery;
import com.yjxxt.service.SaleChanceService;
import com.yjxxt.service.UserService;
import com.yjxxt.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {


    @Autowired
    private SaleChanceService saleChanceService;

    @Autowired
    private UserService userService;


    /**
     * 机会数据添加与更新页面视图转发
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateSaleChancePage")
    public String addOrUpdateSaleChancePage(Integer id, Model model) {
        //判断
        if (null != id) {
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            model.addAttribute("saleChance", saleChance);
        }
        return "saleChance/add_update";
    }


    @RequestMapping("index")
    public String index() {
        return "saleChance/sale_chance";
    }


    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> list(SaleChanceQuery saleChanceQuery) {
        //调用方法
        Map<String, Object> map = saleChanceService.querySaleChanceByParams(saleChanceQuery);
        return map;
    }

    @RequestMapping("save")
    @ResponseBody
    public ResultInfo add(HttpServletRequest req, SaleChance saleChance) {
        //创建人
        Integer id = LoginUserUtil.releaseUserIdFromCookie(req);
        String trueName = userService.selectByPrimaryKey(id).getTrueName();
        //调用方法
        saleChance.setCreateMan(trueName);
        //添加
        saleChanceService.addSaleChance(saleChance);
        return success("添加成功");
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(SaleChance saleChance) {
        //操作
        saleChanceService.checkSaleChance(saleChance);
        //返回
        return success("修改成功");
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
        saleChanceService.removeSaleChance(ids);
        //返回
        return success("操作成功");
    }
}
