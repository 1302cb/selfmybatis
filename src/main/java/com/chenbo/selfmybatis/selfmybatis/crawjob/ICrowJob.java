package com.chenbo.selfmybatis.selfmybatis.crawjob;

public interface ICrowJob extends Runnable{
    /*
    运行前需要做的事情
     */
    void beforeRun();
    /*
    运行之后需要做的事情
     */
    void afterRun();
}
