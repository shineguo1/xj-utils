import org.xj.commons.redis.client.RedisManager;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/3/15 11:34
 */
public class Test {
    @AllArgsConstructor
    @Data
    static class User {

        private String name;
        private String age;
    }
    public static void main(String[] args) {


        RedisManager.get().setClient(ConfigFactory.dev());


        RedisManager.get().insertObject("test-hello","hello world", 10);

        RedisManager.get().insertObject("AA","a",60);
        Map<String,Object> m  = new HashMap<>();
        m.put("A",new User("aa","bb"));
        m.put("B","b");
        m.put("C","c");
        RedisManager.get(). batchInsertObject(m, 60);
        String a = RedisManager.get().queryObjectByKey("A");
        User a2 = RedisManager.get().queryObjectByKey("A", User.class);
        String aa1 = RedisManager.get().queryObjectByKey("AA");
        String aa = RedisManager.get().queryObjectByKey("AA", String.class);
        List<User> batchuser = RedisManager.get().batchQueryObjectByKey(Arrays.asList("dd","A"), User.class);
        List<String> batchss = RedisManager.get().batchQueryObjectByKey(Arrays.asList("AA","B","D"), String.class);
        System.out.println(RedisManager.get().queryObjectByKey("A"));
    }
}
