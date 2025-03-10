package com.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.annotation.IgnoreAuth;

import com.entity.TushuxinxiEntity;
import com.entity.view.TushuxinxiView;

import com.service.TushuxinxiService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;
import java.io.IOException;
import com.service.StoreupService;
import com.entity.StoreupEntity;

/**
 * 图书信息
 * 后端接口
 * @author 
 * @email 
 * @date 2023-04-24 23:01:36
 */
@RestController
@RequestMapping("/tushuxinxi")
public class TushuxinxiController {
    @Autowired
    private TushuxinxiService tushuxinxiService;

    @Autowired
    private StoreupService storeupService;

    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,TushuxinxiEntity tushuxinxi,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("chujiezhe")) {
			tushuxinxi.setChujiezhanghao((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<TushuxinxiEntity> ew = new EntityWrapper<TushuxinxiEntity>();

		PageUtils page = tushuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tushuxinxi), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,TushuxinxiEntity tushuxinxi, 
		HttpServletRequest request){
        EntityWrapper<TushuxinxiEntity> ew = new EntityWrapper<TushuxinxiEntity>();

		PageUtils page = tushuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tushuxinxi), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( TushuxinxiEntity tushuxinxi){
       	EntityWrapper<TushuxinxiEntity> ew = new EntityWrapper<TushuxinxiEntity>();
      	ew.allEq(MPUtil.allEQMapPre( tushuxinxi, "tushuxinxi")); 
        return R.ok().put("data", tushuxinxiService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(TushuxinxiEntity tushuxinxi){
        EntityWrapper< TushuxinxiEntity> ew = new EntityWrapper< TushuxinxiEntity>();
 		ew.allEq(MPUtil.allEQMapPre( tushuxinxi, "tushuxinxi")); 
		TushuxinxiView tushuxinxiView =  tushuxinxiService.selectView(ew);
		return R.ok("查询图书信息成功").put("data", tushuxinxiView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        TushuxinxiEntity tushuxinxi = tushuxinxiService.selectById(id);
		tushuxinxi.setClicknum(tushuxinxi.getClicknum()+1);
		tushuxinxi.setClicktime(new Date());
		tushuxinxiService.updateById(tushuxinxi);
        return R.ok().put("data", tushuxinxi);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        TushuxinxiEntity tushuxinxi = tushuxinxiService.selectById(id);
		tushuxinxi.setClicknum(tushuxinxi.getClicknum()+1);
		tushuxinxi.setClicktime(new Date());
		tushuxinxiService.updateById(tushuxinxi);
        return R.ok().put("data", tushuxinxi);
    }
    


    /**
     * 赞或踩
     */
    @RequestMapping("/thumbsup/{id}")
    public R vote(@PathVariable("id") String id,String type){
        TushuxinxiEntity tushuxinxi = tushuxinxiService.selectById(id);
        if(type.equals("1")) {
        	tushuxinxi.setThumbsupnum(tushuxinxi.getThumbsupnum()+1);
        } else {
        	tushuxinxi.setCrazilynum(tushuxinxi.getCrazilynum()+1);
        }
        tushuxinxiService.updateById(tushuxinxi);
        return R.ok("投票成功");
    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody TushuxinxiEntity tushuxinxi, HttpServletRequest request){
    	tushuxinxi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(tushuxinxi);
        tushuxinxiService.insert(tushuxinxi);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
	@IgnoreAuth
    @RequestMapping("/add")
    public R add(@RequestBody TushuxinxiEntity tushuxinxi, HttpServletRequest request){
    	tushuxinxi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(tushuxinxi);
        tushuxinxiService.insert(tushuxinxi);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody TushuxinxiEntity tushuxinxi, HttpServletRequest request){
        //ValidatorUtils.validateEntity(tushuxinxi);
        tushuxinxiService.updateById(tushuxinxi);//全部更新
        return R.ok();
    }


    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        tushuxinxiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	
	/**
     * 前端智能排序
     */
	@IgnoreAuth
    @RequestMapping("/autoSort")
    public R autoSort(@RequestParam Map<String, Object> params,TushuxinxiEntity tushuxinxi, HttpServletRequest request,String pre){
        EntityWrapper<TushuxinxiEntity> ew = new EntityWrapper<TushuxinxiEntity>();
        Map<String, Object> newMap = new HashMap<String, Object>();
        Map<String, Object> param = new HashMap<String, Object>();
		Iterator<Map.Entry<String, Object>> it = param.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			String key = entry.getKey();
			String newKey = entry.getKey();
			if (pre.endsWith(".")) {
				newMap.put(pre + newKey, entry.getValue());
			} else if (StringUtils.isEmpty(pre)) {
				newMap.put(newKey, entry.getValue());
			} else {
				newMap.put(pre + "." + newKey, entry.getValue());
			}
		}
		params.put("sort", "clicknum");
        params.put("order", "desc");
		PageUtils page = tushuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tushuxinxi), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 协同算法（按收藏推荐）
     */
    @RequestMapping("/autoSort2")
    public R autoSort2(@RequestParam Map<String, Object> params,TushuxinxiEntity tushuxinxi, HttpServletRequest request){
        String userId = request.getSession().getAttribute("userId").toString();
        String inteltypeColumn = "tushufenlei";
        List<StoreupEntity> storeups = storeupService.selectList(new EntityWrapper<StoreupEntity>().eq("type", 1).eq("userid", userId).eq("tablename", "tushuxinxi").orderBy("addtime", false));
        List<String> inteltypes = new ArrayList<String>();
        Integer limit = params.get("limit")==null?10:Integer.parseInt(params.get("limit").toString());
        List<TushuxinxiEntity> tushuxinxiList = new ArrayList<TushuxinxiEntity>();
        //去重
        if(storeups!=null && storeups.size()>0) {
            for(StoreupEntity s : storeups) {
                tushuxinxiList.addAll(tushuxinxiService.selectList(new EntityWrapper<TushuxinxiEntity>().eq(inteltypeColumn, s.getInteltype())));
            }
        }
        EntityWrapper<TushuxinxiEntity> ew = new EntityWrapper<TushuxinxiEntity>();
        params.put("sort", "id");
        params.put("order", "desc");
        PageUtils page = tushuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tushuxinxi), params), params));
        List<TushuxinxiEntity> pageList = (List<TushuxinxiEntity>)page.getList();
        if(tushuxinxiList.size()<limit) {
            int toAddNum = (limit-tushuxinxiList.size())<=pageList.size()?(limit-tushuxinxiList.size()):pageList.size();
            for(TushuxinxiEntity o1 : pageList) {
                boolean addFlag = true;
                for(TushuxinxiEntity o2 : tushuxinxiList) {
                    if(o1.getId().intValue()==o2.getId().intValue()) {
                        addFlag = false;
                        break;
                    }
                }
                if(addFlag) {
                    tushuxinxiList.add(o1);
                    if(--toAddNum==0) break;
                }
            }
        } else if(tushuxinxiList.size()>limit) {
            tushuxinxiList = tushuxinxiList.subList(0, limit);
        }
        page.setList(tushuxinxiList);
        return R.ok().put("data", page);
    }







}
