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

import com.entity.JieyuezheEntity;
import com.entity.view.JieyuezheView;

import com.service.JieyuezheService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;
import java.io.IOException;

/**
 * 借阅者
 * 后端接口
 * @author 
 * @email 
 * @date 2023-04-24 23:01:35
 */
@RestController
@RequestMapping("/jieyuezhe")
public class JieyuezheController {
    @Autowired
    private JieyuezheService jieyuezheService;


    
	@Autowired
	private TokenService tokenService;
	
	/**
	 * 登录
	 */
	@IgnoreAuth
	@RequestMapping(value = "/login")
	public R login(String username, String password, String captcha, HttpServletRequest request) {
		JieyuezheEntity u = jieyuezheService.selectOne(new EntityWrapper<JieyuezheEntity>().eq("jieyuezhanghao", username));
		if(u==null || !u.getMima().equals(password)) {
			return R.error("账号或密码不正确");
		}
		
		String token = tokenService.generateToken(u.getId(), username,"jieyuezhe",  "借阅者" );
		return R.ok().put("token", token);
	}

	
	/**
     * 注册
     */
	@IgnoreAuth
    @RequestMapping("/register")
    public R register(@RequestBody JieyuezheEntity jieyuezhe){
    	//ValidatorUtils.validateEntity(jieyuezhe);
    	JieyuezheEntity u = jieyuezheService.selectOne(new EntityWrapper<JieyuezheEntity>().eq("jieyuezhanghao", jieyuezhe.getJieyuezhanghao()));
		if(u!=null) {
			return R.error("注册用户已存在");
		}
		Long uId = new Date().getTime();
		jieyuezhe.setId(uId);
        jieyuezheService.insert(jieyuezhe);
        return R.ok();
    }

	
	/**
	 * 退出
	 */
	@RequestMapping("/logout")
	public R logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return R.ok("退出成功");
	}
	
	/**
     * 获取用户的session用户信息
     */
    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request){
    	Long id = (Long)request.getSession().getAttribute("userId");
        JieyuezheEntity u = jieyuezheService.selectById(id);
        return R.ok().put("data", u);
    }
    
    /**
     * 密码重置
     */
    @IgnoreAuth
	@RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request){
    	JieyuezheEntity u = jieyuezheService.selectOne(new EntityWrapper<JieyuezheEntity>().eq("jieyuezhanghao", username));
    	if(u==null) {
    		return R.error("账号不存在");
    	}
        u.setMima("123456");
        jieyuezheService.updateById(u);
        return R.ok("密码已重置为：123456");
    }


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,JieyuezheEntity jieyuezhe,
		HttpServletRequest request){
        EntityWrapper<JieyuezheEntity> ew = new EntityWrapper<JieyuezheEntity>();

		PageUtils page = jieyuezheService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jieyuezhe), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,JieyuezheEntity jieyuezhe, 
		HttpServletRequest request){
        EntityWrapper<JieyuezheEntity> ew = new EntityWrapper<JieyuezheEntity>();

		PageUtils page = jieyuezheService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jieyuezhe), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( JieyuezheEntity jieyuezhe){
       	EntityWrapper<JieyuezheEntity> ew = new EntityWrapper<JieyuezheEntity>();
      	ew.allEq(MPUtil.allEQMapPre( jieyuezhe, "jieyuezhe")); 
        return R.ok().put("data", jieyuezheService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(JieyuezheEntity jieyuezhe){
        EntityWrapper< JieyuezheEntity> ew = new EntityWrapper< JieyuezheEntity>();
 		ew.allEq(MPUtil.allEQMapPre( jieyuezhe, "jieyuezhe")); 
		JieyuezheView jieyuezheView =  jieyuezheService.selectView(ew);
		return R.ok("查询借阅者成功").put("data", jieyuezheView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        JieyuezheEntity jieyuezhe = jieyuezheService.selectById(id);
        return R.ok().put("data", jieyuezhe);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        JieyuezheEntity jieyuezhe = jieyuezheService.selectById(id);
        return R.ok().put("data", jieyuezhe);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody JieyuezheEntity jieyuezhe, HttpServletRequest request){
    	jieyuezhe.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(jieyuezhe);
    	JieyuezheEntity u = jieyuezheService.selectOne(new EntityWrapper<JieyuezheEntity>().eq("jieyuezhanghao", jieyuezhe.getJieyuezhanghao()));
		if(u!=null) {
			return R.error("用户已存在");
		}
		jieyuezhe.setId(new Date().getTime());
        jieyuezheService.insert(jieyuezhe);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody JieyuezheEntity jieyuezhe, HttpServletRequest request){
    	jieyuezhe.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(jieyuezhe);
    	JieyuezheEntity u = jieyuezheService.selectOne(new EntityWrapper<JieyuezheEntity>().eq("jieyuezhanghao", jieyuezhe.getJieyuezhanghao()));
		if(u!=null) {
			return R.error("用户已存在");
		}
		jieyuezhe.setId(new Date().getTime());
        jieyuezheService.insert(jieyuezhe);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody JieyuezheEntity jieyuezhe, HttpServletRequest request){
        //ValidatorUtils.validateEntity(jieyuezhe);
        jieyuezheService.updateById(jieyuezhe);//全部更新
        return R.ok();
    }


    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        jieyuezheService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	









}
