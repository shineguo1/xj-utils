package org.xj.commons.web3j.protocol.core.method.response.model.ton;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/15 17:20
 */
@Data
public class Price {
    /**
     * e.g.
     * "prices": {
     * "USD": 0.0015510209895819222,
     * "CNY": 0.01101876331418789,
     * "TON": 0.0007134411175629817
     * }
     */
    private Map<String, BigDecimal> prices;
    /**
     * "diff_24h": {
     * "EUR": "-0.34%",
     * "CNY": "+0.31%",
     * "TON": "-0.21%",
     * "USD": "+0.27%",
     * "RUB": "+0.59%"
     * }
     */
    private Map<String, String> diff_24h;
    private Map<String, String> diff_7d;
    private Map<String, String> diff_30d;

    public BigDecimal getPrice(String currency) {
        return prices == null ? null : prices.get(currency);
    }

    public String getDiff24h(String currency) {
        return diff_24h == null ? null : diff_24h.get(currency);
    }

    public String getDiff7d(String currency) {
        return diff_7d == null ? null : diff_7d.get(currency);
    }

    public String getDiff30d(String currency) {
        return diff_30d == null ? null : diff_30d.get(currency);
    }
}
