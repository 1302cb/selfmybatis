package com.chenbo.selfmybatis.selfmybatis.crawjob.crowJobImpl;

import com.chenbo.selfmybatis.selfmybatis.crawjob.ICrowJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractCrowJob implements ICrowJob {
    private static Logger logger = LoggerFactory.getLogger(AbstractCrowJob.class);
    @Override
    public void beforeRun() {
        logger.info("爬取页面之前");
    }

    @Override
    public void afterRun() {
        logger.info("爬取页面之后");
    }

    /*
    类似于模版方法
     */
    @Override
    public void run() {
        this.beforeRun();
        try {
            this.doFetchPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.afterRun();
    }

    /*
    子类需要实现的方法
     */
    public abstract void doFetchPage() throws Exception;

}
