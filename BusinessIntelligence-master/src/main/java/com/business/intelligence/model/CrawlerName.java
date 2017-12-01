package com.business.intelligence.model;

import lombok.Data;

/**
 * Created by zjy on 17/8/11.
 */


public enum CrawlerName {

    ELM_CRAWLER_EVALUATE,  //抓取全部评论状态
    ELM_CRAWLER_ACTIVITY,  //抓取全部活动状态
    ELM_CRAWLER_BILL,  //爬取全部账单状态
    ELM_CRAWLER_COMMODITY,  //爬取全部商品状态
    ELM_CRAWLER_FLOW,  //爬取流量统计状态
    ELM_CRAWLER_SALE,  //爬取营业统计状态
    ELM_CRAWLER_ORDER,   //爬取所有订单状态



    MT_REPORT_FORMS, //报表
    MT_CRAWLER_SALE, //营业统计
    MT_CRAWLER_FLOW, //流量分析
    MT_GOODS_SALE, //热销商品
    MT_ORDER_CHECKING, //订单对账
    MT_CRAWLER_EVALUATE, //指定门店点评内容
    MT_SALE_ACTIVITY, //店铺活动

    BD_ORDERDETAILS,   //订单信息
    BD_CRAWLER,   //百度外卖商户数据
    BD_COMMENT   //用户评论信息

}
