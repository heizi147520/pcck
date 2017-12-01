package com.business.intelligence.crawler.eleme;

import com.business.intelligence.dao.CrawlerStatusDao;
import com.business.intelligence.dao.ElemeDao;
import com.business.intelligence.model.Authenticate;
import com.business.intelligence.model.CrawlerName;
import com.business.intelligence.model.ElemeModel.ElemeBean;
import com.business.intelligence.model.ElemeModel.ElemeFlow;
import com.business.intelligence.util.DateUtils;
import com.business.intelligence.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Tcqq on 2017/7/24.
 * 排名流量 POST请求
 */
@Slf4j
@Component
public class ElemeFlowCrawler extends ElemeCrawler{
    //默认抓取前一天的，具体值已经在父类设置
    private Date crawlerDate = super.crawlerDate;
    private Date endCrawlerDate = crawlerDate;
    @Autowired
    private ElemeDao elemeDao;
    @Autowired
    private CrawlerStatusDao crawlerStatusDao;

    private static final String URL = "https://app-api.shop.ele.me/stats/invoke/?method=trafficStats.getTrafficStatsV2";

    public void doRun(ElemeBean elemeBean,String startTime,String endTime) {
        //更新爬取状态为进行中
        int i = crawlerStatusDao.updateStatusING(CrawlerName.ELM_CRAWLER_FLOW);
        if(i ==1){
            log.info("更新爬取状态成功");
        }else{
            log.info("更新爬取状态失败");
        }
        //开始转换前台传入的时间
        Date start = DateUtils.string2Date(startTime);
        Date end = DateUtils.string2Date(endTime);
        if(start != null && end != null ){
            this.crawlerDate =start;
            this.endCrawlerDate = end;
        }
        //开始爬取
        CloseableHttpClient client = getClient(elemeBean);
        if(client != null){
            log.info("开始爬取饿了么流量排名，日期： {} 至 {} ，URL： {} ，用户名： {}", DateUtils.date2String(crawlerDate), DateUtils.date2String(endCrawlerDate),URL,elemeBean.getUsername());
            crawlerLogger.log("开始爬取饿了么用户名为"+username+"的流量排名");
            List<LinkedHashMap<String, Object>> flowList = getFlowText(client);
            List<ElemeFlow> elemeFlowBeans = getElemeFlowBeans(flowList);
            for(ElemeFlow elemeFlow : elemeFlowBeans){
                elemeDao.insertFlow(elemeFlow);
            }
            log.info("用户名为 {} 的流量排名已入库",username);
            crawlerLogger.log("完成爬取饿了么用户名为"+username+"的流量排名");
        }
        //更新爬取状态为已完成
        int f = crawlerStatusDao.updateStatusFinal(CrawlerName.ELM_CRAWLER_FLOW);
        if(f ==1){
            log.info("更新爬取状态成功");
        }else{
            log.info("更新爬取状态失败");
        }
    }

    /**
     * 通过爬虫获得所有的对应日期的流量统计
     * @param client
     * @return
     */
    public List<LinkedHashMap<String, Object>> getFlowText(CloseableHttpClient client){
        log.info("ksid id {}",ksId);
        CloseableHttpResponse execute = null;
        HttpPost post = new HttpPost(URL);
        StringEntity jsonEntity = null;
        String date = DateUtils.date2String(crawlerDate);
        String endDate = DateUtils.date2String(endCrawlerDate);
        String json = "{\"id\":\"bce6735e-27dd-441b-982c-19b6422327b3\",\"method\":\"getTrafficStatsV2\",\"service\":\"trafficStats\",\"params\":{\"shopId\":"+shopId+",\"beginDate\":\""+date+"\",\"endDate\":\""+endDate+"\"},\"metas\":{\"appName\":\"melody\",\"appVersion\":\"4.4.0\",\"ksid\":\""+ksId+"\"},\"ncp\":\"2.0.0\"}";
        jsonEntity = new StringEntity(json, "UTF-8");
        post.setEntity(jsonEntity);
        setElemeHeader(post);
        post.setHeader("X-Eleme-RequestID", "bce6735e-27dd-441b-982c-19b6422327b3");
        try {
            execute = client.execute(post);
            HttpEntity entity = execute.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            log.info("result is {}",result);
            List<LinkedHashMap<String, Object>> mapsByJsonPath = WebUtils.getMapsByJsonPath(result, "$.result.restaurantTrafficStatsList");
            return mapsByJsonPath;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (execute != null){
                    execute.close();
                }
//                if(client != null){
//                    client.close();
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }


    /**
     * 获取ElemeFlow实体类
     * @param flowList
     * @return
     */
    public List<ElemeFlow> getElemeFlowBeans(List<LinkedHashMap<String, Object>> flowList){
        List<ElemeFlow> list = new ArrayList<>();
        for(LinkedHashMap<String,Object> map : flowList){
            ElemeFlow elemeFlow = new ElemeFlow();
            elemeFlow.setFlowId((String)map.getOrDefault("shopName","")+"~"+(String)map.getOrDefault("statsDate",""));
            elemeFlow.setCrawlerDate(notNull((String)map.getOrDefault("statsDate","")));
            elemeFlow.setShopName(notNull((String)map.getOrDefault("shopName","")));
            elemeFlow.setExposureTotalCount((Integer)map.getOrDefault("exposureTotalCount",0));
            elemeFlow.setVisitorNum((Integer)map.getOrDefault("visitorNum",0));
            elemeFlow.setBuyerNum((Integer)map.getOrDefault("buyerNum",0));
            if(merchantId != null){
                elemeFlow.setMerchantId(merchantId);
            }
            list.add(elemeFlow);
        }
        return list;
    }

    @Override
    public void doRun() {

    }
}
