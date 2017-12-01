package com.business.intelligence.controller;

import com.alibaba.fastjson.JSONObject;
import com.business.intelligence.model.baidu.BDCrawlerStatus;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by zjy on 17/7/19.
 */

@Slf4j
@RestController
@RequestMapping("/bi/")
public class WebController {


    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

//
//    @RequestMapping(value = "getStatus", method = RequestMethod.GET)
//    @ApiOperation(value = "获取爬虫抓取状态", httpMethod = "GET")
//    public String getStatus(@RequestParam String crawlerName) {
//        CrawlerStatusDao status = new CrawlerStatusDao();
//
//
//        switch (crawlerName){
//            case "ELM_CRAWLER_ACTIVITY" :
//                return status.getStatus(CrawlerName.ELM_CRAWLER_ACTIVITY);
//            case "ELM_CRAWLER_BILL":
//                return  status.getStatus(CrawlerName.ELM_CRAWLER_BILL);
//            case "ELM_CRAWLER_COMMODITY":
//                return  status.getStatus(CrawlerName.ELM_CRAWLER_COMMODITY);
//            case "ELM_CRAWLER_EVALUATE":
//                return  status.getStatus(CrawlerName.ELM_CRAWLER_EVALUATE);
//            case "ELM_CRAWLER_FLOW":
//                return  status.getStatus(CrawlerName.ELM_CRAWLER_FLOW);
//            case "ELM_CRAWLER_ORDER":
//                return  status.getStatus(CrawlerName.ELM_CRAWLER_ORDER);
//            case "ELM_CRAWLER_SALE":
//                return  status.getStatus(CrawlerName.ELM_CRAWLER_SALE);
//        }
//
//        return "状态获取失败";
//    }
//

    @RequestMapping(value = "getAllStatus", method = RequestMethod.GET)
    @ApiOperation(value = "获取爬虫抓取状态", httpMethod = "GET")
    public String getAllStatus() {
        List<BDCrawlerStatus> bd = sqlSessionTemplate.selectList("com.business.intelligence.model.getStatusForPlatform", "BD_%");
        List<BDCrawlerStatus> mt = sqlSessionTemplate.selectList("com.business.intelligence.model.getStatusForPlatform", "MT_%");
        List<BDCrawlerStatus> elm = sqlSessionTemplate.selectList("com.business.intelligence.model.getStatusForPlatform", "ELM_%");

        JSONObject object = new JSONObject();

        object.put("bd", bd);
        object.put("mt", mt);
        object.put("elm", elm);


        return object.toJSONString();

    }

}
