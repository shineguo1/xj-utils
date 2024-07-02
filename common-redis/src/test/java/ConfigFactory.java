import org.redisson.config.Config;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/3/15 11:34
 */
public class ConfigFactory {

    public static Config local() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379");
        return config;
    }

    public static Config dev() {
        String clusterName = "mymaster";
        String url = "redis://10.254.21.143:26379";
        int masterConnectionPoolSize = 250;
        int slaveConnectionPoolSize = 250;
        int connectionMinimumIdleSize = 10;

        Config config = new Config();
        config.useSentinelServers()
                .setMasterName(clusterName)
                .addSentinelAddress(url)
                .setMasterConnectionPoolSize(masterConnectionPoolSize)
                .setSlaveConnectionPoolSize(slaveConnectionPoolSize)
                .setMasterConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setSlaveConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setCheckSentinelsList(false);
        return config;
    }
    public static Config prod() {
        String clusterName = "cache";
        String url = "redis://10.106.79.137:26379";
        int masterConnectionPoolSize = 50;
        int slaveConnectionPoolSize = 50;
        int connectionMinimumIdleSize = 20;

        Config config = new Config();
        config.useSentinelServers()
                .setMasterName(clusterName)
                .addSentinelAddress(url)
                .setMasterConnectionPoolSize(masterConnectionPoolSize)
                .setSlaveConnectionPoolSize(slaveConnectionPoolSize)
                .setMasterConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setSlaveConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setCheckSentinelsList(false);
        return config;
    }
}
