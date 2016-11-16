/* 
 * PojoRiskTradeLog.java  
 * 
 * version TODO
 *
 * 2016年11月14日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.risk.dao;

import java.util.List;
import java.util.Map;

import com.zlebank.zplatform.risk.bean.RiskBean;
import com.zlebank.zplatform.risk.common.dao.BaseDAO;
import com.zlebank.zplatform.risk.pojo.PojoRiskTradeLog;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年11月14日 下午5:38:25
 * @since 
 */
public interface RiskTradeLogDAO extends BaseDAO<PojoRiskTradeLog>{

	/**
	 * 保存风控交易日志
	 * @param riskTradeLog
	 */
	public void saveRiskTradeLo(PojoRiskTradeLog riskTradeLog);
	
	/**
	 * 数据库交易风控
	 * @param riskBean
	 * @return
	 */
	public List<Map<String, Object>> oracleFunRiskControl(RiskBean riskBean);
	
	/**
	 * 取得风控策略
	 * @param orders
	 * @return
	 */
    public List<Map<String,String>> getRiskStrategy(int orders);
}
