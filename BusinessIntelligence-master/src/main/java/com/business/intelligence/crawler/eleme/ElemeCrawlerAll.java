package com.business.intelligence.crawler.eleme;

import com.business.intelligence.dao.UserDao;
import com.business.intelligence.model.ElemeModel.ElemeBean;
import com.business.intelligence.model.Platform;
import com.business.intelligence.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tcqq on 2017/8/10.
 * 爬取所有商户的所有信息
 */
@Slf4j
@Component
public class ElemeCrawlerAll {
    @Autowired
    private ElemeEvaluateCrawler elemeEvaluateCrawler;
    @Autowired
    private ElemeOrderCrawler elemeOrderCrawler;
    @Autowired
    private ElemeActivityCrawler elemeActivityCrawler;
    @Autowired
    private ElemeBillCrawler elemeBillCrawler;
    @Autowired
    private ElemeCommodityCrawler elemeCommodityCrawler;
    @Autowired
    private ElemeFlowCrawler elemeFlowCrawler;
    @Autowired
    private ElemeSaleCrawler elemeSaleCrawler;
    @Autowired
    private UserDao userdao;

    /**
     * 获取所有的商户信息
     */
    public List<ElemeBean> getAllElemeBeans(){
        log.info("开始获取饿了么所有商户信息");
        List<ElemeBean> list = new ArrayList<>();
        List<User> elmUserList = userdao.getUsersForPlatform(Platform.ELM);
        for(User elmUser : elmUserList){
            ElemeBean elemeBean = new ElemeBean();
            elemeBean.setUsername(elmUser.getUserName());
            elemeBean.setPassword(elmUser.getPassWord());
            elemeBean.setShopId(elmUser.getShopId());
            elemeBean.setShopPri(elmUser.getMerchantId());
            list.add(elemeBean);
        }
        log.info("所有饿了么商户信息已经加载完成");
        return list;
    }

    public void runAllCrawler(){
        List<ElemeBean> elemeBeanList = getAllElemeBeans();
        String yesterday = com.business.intelligence.util.DateUtils.date2String(DateUtils.addDays(new Date(), -1));
        for(ElemeBean elemeBean : elemeBeanList){
            log.info("开始依次爬取 {} 的所有项目",elemeBean.getUsername());
            elemeActivityCrawler.doRun(elemeBean);
            elemeBillCrawler.doRun(elemeBean, yesterday,yesterday);
            elemeCommodityCrawler.doRun(elemeBean,yesterday);
            elemeFlowCrawler.doRun(elemeBean,yesterday,yesterday);
            elemeSaleCrawler.doRun(elemeBean,yesterday,yesterday);
            elemeOrderCrawler.doRun(elemeBean,yesterday,yesterday);
            elemeEvaluateCrawler.doRun(elemeBean,yesterday,yesterday);
            log.info("{} 的各个项目已经完成，开始下一个账户",elemeBean.getUsername());
        }
        log.info("饿了么所有商户的所有信息均以完成");
    }

}
