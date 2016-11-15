/* 
 * TradeRiskControlServiceImpl.java  
 * 
 * version TODO
 *
 * 2016年11月15日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.risk.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.zlebank.zplatform.risk.bean.RiskBean;
import com.zlebank.zplatform.risk.dao.RiskTradeLogDAO;
import com.zlebank.zplatform.risk.dao.TxnsLogDAO;
import com.zlebank.zplatform.risk.enums.RiskLevelEnum;
import com.zlebank.zplatform.risk.pojo.PojoRiskTradeLog;
import com.zlebank.zplatform.risk.pojo.PojoTxnsLog;
import com.zlebank.zplatform.risk.service.TradeRiskControlService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年11月15日 上午8:52:37
 * @since 
 */
@Service("tradeRiskControlService")
public class TradeRiskControlServiceImpl implements TradeRiskControlService {

	private static final Logger log = LoggerFactory.getLogger(TradeRiskControlServiceImpl.class);
	@Autowired
	private RiskTradeLogDAO riskTradeLogDAO;
	@Autowired
	private TxnsLogDAO txnsLogDAO;
	/**
	 *
	 * @param riskBean
	 */
	@Override
	public void RealTimeTradeRiskControl(RiskBean riskBean) {
		// TODO Auto-generated method stub
		log.info("trade risk control start");
        int riskLevel = 0;
        int riskOrder = 0;
        RiskLevelEnum riskLevelEnum = null;
        String riskInfo = "";
        log.info("risk paramaters:");
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> riskList = (List<Map<String, Object>>)riskTradeLogDAO.oracleFunRiskControl(riskBean);
        log.info("trade risk result:"+JSON.toJSONString(riskList));
        
        if(riskList.size()>0){
            riskInfo = riskList.get(0).get("RISK")+"";
            if(riskInfo.indexOf(",")>0){
            	String[] riskInfos =riskInfo.split(",");
            	try {
    				riskOrder = Integer.valueOf(riskInfos[0]);
    				riskLevel = Integer.valueOf(riskInfos[1]);
    				riskLevelEnum = RiskLevelEnum.fromValue(riskLevel);
    				log.info("riskOrder:"+riskOrder);
    				log.info("riskLevel:"+riskLevel);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    				//throw new TradeException("T034");
    			}
            }else{
            	riskLevelEnum = RiskLevelEnum.fromValue(Integer.valueOf(riskInfo));
            }
            
            
        }else{
            //throw new TradeException("T034");
        }
        if(RiskLevelEnum.PASS==riskLevelEnum){//交易通过
            return;
        }
        //记录风控日志
        List<Map<String,String>> riskStrategyList =  riskTradeLogDAO.getRiskStrategy(riskOrder);
        log.info("riskStrategyList:"+JSON.toJSONString(riskStrategyList));
        if(riskStrategyList.size()>0){
            PojoTxnsLog txnsLog =txnsLogDAO.getTxnsLogByTxnseqno(riskBean.getTxnseqno());
            Map<String,String> riskMap = riskStrategyList.get(0);
            PojoRiskTradeLog tradeLog = new PojoRiskTradeLog();
            tradeLog.setRiskTradeType(riskOrder);
            tradeLog.setTxnseqno(riskBean.getTxnseqno());
            tradeLog.setOrderno(txnsLog.getAccordno());
            tradeLog.setMerchno(txnsLog.getAccfirmerno());
            //tradeLog.setMerchname(txnsLog.getaccm);
            tradeLog.setSubmerchno(txnsLog.getAccsecmerno());
            // tradeLog.setSummerchname("");
            tradeLog.setMemberid(txnsLog.getAccmemberid());
            tradeLog.setAmount(txnsLog.getAmount());
            tradeLog.setPan(riskBean.getCardNo()); 
            //tradeLog.inpan ;
            tradeLog.setTradeRiskLevel(riskLevel);  ;
            Map<String, Object> cardMap = txnsLogDAO.getCardInfo(riskBean.getCardNo());
            tradeLog.setCardIssuerCode(cardMap.get("BANKCODE")+""); ;
            tradeLog.setCardIssuerName(cardMap.get("BANKNAME")+""); ;
            tradeLog.setAcqInistCode(txnsLog.getAccordinst());
            tradeLog.setAcqdatetime(txnsLog.getAccordcommitime());
            tradeLog.setBusicode(riskBean.getBusiCode());
            tradeLog.setBusitype(txnsLog.getBusitype());
            tradeLog.setStrategy(riskMap.get("STRATEGY"));
            riskTradeLogDAO.saveRiskTradeLo(tradeLog);
        }
        if(RiskLevelEnum.REFUSE==riskLevelEnum){//交易拒绝
            //throw new TradeException("T035");
        }else{
            
        }
        log.info(" trade risk control end");
	}

}
