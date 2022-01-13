package com.yjxxt.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.base.BaseService;
import com.yjxxt.mapper.SaleChanceMapper;
import com.yjxxt.pojo.SaleChance;
import com.yjxxt.query.SaleChanceQuery;
import com.yjxxt.utils.AssertUtil;
import com.yjxxt.utils.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance, Integer> {


    @Resource
    private SaleChanceMapper saleChanceMapper;


    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery) {
        //实例化map
        Map<String, Object> map = new HashMap<>();
        //实例化分页单位
        PageHelper.startPage(saleChanceQuery.getPage(), saleChanceQuery.getLimit());
        //实例化pageinfo
        PageInfo<SaleChance> pageInfo = new PageInfo<>(selectByParams(saleChanceQuery));
        //准备数据
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        //返回
        return map;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance) {
        //验证
        checkParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        //state  0,1(0--未分配，1--已分配)
        if (StringUtils.isBlank(saleChance.getAssignMan())) {
            saleChance.setState(0);
            saleChance.setDevResult(0);
        } else {
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }
        //设定默认值
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        //添加
        AssertUtil.isTrue(insertSelective(saleChance) < 0, "添加失败");
    }

    /**
     * 校验基本参数
     * * @param customerName
     *
     * @param linkMan
     * @param linkPhone
     */
    private void checkParams(String customerName, String linkMan, String linkPhone) {
        //客户人，联系人，电话非空
        AssertUtil.isTrue(StringUtils.isBlank(customerName), "客户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan), "联系人不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone), "电话不能为空");
        //电话为11位有效电话
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone), "请输入正确的联系电话");
    }

    /**
     * 修改
     *
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void checkSaleChance(SaleChance saleChance) {
        //用户存在吗
        SaleChance temp = selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(null == temp, "待修改的记录不存在");
        //检查参数
        checkParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        //分配了吗
        //没分配
        if (StringUtils.isBlank(temp.getAssignMan()) && StringUtils.isNotBlank(saleChance.getAssignMan())) {
            //设置数据
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }
        if(StringUtils.isNotBlank(temp.getAssignMan()) && StringUtils.isBlank(saleChance.getAssignMan())) {
            //已经分配
            saleChance.setState(0);
            saleChance.setDevResult(0);
            saleChance.setAssignTime(null);
            saleChance.setAssignMan("");
        }
        //设置默认值
        saleChance.setUpdateDate(new Date());
        //修改成功了没
        AssertUtil.isTrue(updateByPrimaryKeySelective(saleChance) < 0, "修改失败");
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
    }
}
