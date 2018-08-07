package foundation.icon.iconex.wallet.main;

import java.io.Serializable;
import java.util.List;

import foundation.icon.iconex.control.WalletInfo;

/**
 * Created by js on 2018. 7. 6..
 */

public class CoinsViewItem implements Serializable {
    private String type;
    private String name;
    private String symbol;
    private List<WalletInfo> wallets;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public List<WalletInfo> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletInfo> wallets) {
        this.wallets = wallets;
    }
}