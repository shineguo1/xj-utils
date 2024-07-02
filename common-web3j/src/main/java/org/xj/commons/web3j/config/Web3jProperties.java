package org.xj.commons.web3j.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/3/14 20:59
 */
@Data
public class Web3jProperties {


    // properties
    //------------------------------------------

    private ThreadPoolExecutor asyncBatchPool;
    private  Integer okHttpMaxRequests;
    private  Integer okHttpMaxRequestsPerHost;


    // methods
    //------------------------------------------

    private static Web3jProperties singleton = new Web3jProperties();

    private Web3jProperties() {
        this.asyncBatchPool = new ThreadPoolExecutor(30, 10000, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50000),
                new ThreadFactoryBuilder().setNameFormat("BatchUtilsThreadPool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());
        this.okHttpMaxRequests = 30;
        this.okHttpMaxRequestsPerHost = 10;
    }

    public static Web3jProperties get() {
        return singleton;
    }

}
