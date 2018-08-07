package foundation.icon.iconex.wallet.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.List;
import java.util.Locale;

import foundation.icon.iconex.ICONexApp;
import foundation.icon.iconex.MyConstants;
import foundation.icon.iconex.R;
import foundation.icon.iconex.control.WalletEntry;
import foundation.icon.iconex.control.WalletInfo;
import foundation.icon.iconex.util.ConvertUtil;
import loopchain.icon.wallet.core.Constants;

import static foundation.icon.iconex.MyConstants.EXCHANGE_USD;

/**
 * Created by js on 2018. 3. 6..
 */

public class WalletRecyclerAdapter extends RecyclerView.Adapter<WalletRecyclerAdapter.ViewHolder> {

    private static final String TAG = WalletRecyclerAdapter.class.getSimpleName();

    private Context mContext;
    private List<WalletEntry> mData;
    private LayoutInflater mInflater;
    private WalletInfo mWalletInfo;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public WalletRecyclerAdapter(Context context, WalletInfo walletInfo) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mWalletInfo = walletInfo;
        this.mData = mWalletInfo.getWalletEntries();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_wallet_contents, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WalletEntry item = mData.get(position);
        BigInteger balance;
        String value;

        if (!item.getBalance().isEmpty()) {
            holder.loadingBalance.setVisibility(View.GONE);

            if (item.getBalance().equals(MyConstants.NO_BALANCE)) {
                holder.txtBalance.setText(MyConstants.NO_BALANCE);
                holder.txtTransBalance.setText(MyConstants.NO_BALANCE);
            } else {
                balance = new BigInteger(item.getBalance());
                value = ConvertUtil.getValue(balance, item.getDefaultDec());
                Double doubBalance = Double.parseDouble(value);

                holder.txtBalance.setText(String.format(Locale.getDefault(), "%,.4f", doubBalance));

                String exchange = item.getSymbol().toLowerCase() + ((MainActivity) mContext).getExchangeUnit().toLowerCase();
                String strPrice;
                if (exchange.equals("etheth"))
                    strPrice = "1";
                else
                    strPrice = ICONexApp.EXCHANGE_TABLE.get(exchange);
                if (strPrice != null) {
                    if (strPrice.equals(MyConstants.NO_EXCHANGE)) {
                        holder.txtTransBalance.setText(MyConstants.NO_BALANCE);
                    } else {
                        Double price = Double.parseDouble(strPrice);

                        if (((MainActivity) mContext).getExchangeUnit().equals(EXCHANGE_USD)) {
                            holder.txtTransBalance.setText((String.format(Locale.getDefault(), "%,.2f", (doubBalance * price))));
                        } else {
                            holder.txtTransBalance.setText((String.format(Locale.getDefault(), "%,.4f", (doubBalance * price))));
                        }
                    }
                }
            }
        }
        holder.txtName.setText(item.getUserName());
        holder.txtUnit.setText(item.getSymbol());
        holder.txtTransUnit.setText(((MainActivity) mContext).getExchangeUnit());

        holder.txtLoadingUnit.setText(item.getSymbol());
        holder.txtLoadingTrUnit.setText(((MainActivity) mContext).getExchangeUnit());

        if (position == getItemCount() - 1)
            holder.line.setVisibility(View.INVISIBLE);

        if (item.getType().equals(MyConstants.TYPE_TOKEN)
                && item.getSymbol().equals(Constants.KS_COINTYPE_ICX))
            holder.btnSwap.setVisibility(View.VISIBLE);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtName;
        TextView txtBalance, txtUnit;
        TextView txtTransBalance, txtTransUnit;
        TextView txtLoadingUnit, txtLoadingTrUnit;
        Button btnSwap;
        ViewGroup loadingBalance;
        View line;

        ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name);
            txtBalance = itemView.findViewById(R.id.txt_total_balance);
            txtUnit = itemView.findViewById(R.id.txt_unit);
            txtTransBalance = itemView.findViewById(R.id.txt_trans_balance);
            txtTransUnit = itemView.findViewById(R.id.txt_trans_unit);
            btnSwap = itemView.findViewById(R.id.btn_swap);
            btnSwap.setOnClickListener(this);

            loadingBalance = itemView.findViewById(R.id.loading_balance);
            itemView.setOnClickListener(this);
            txtLoadingUnit = itemView.findViewById(R.id.txt_loading_unit);
            txtLoadingTrUnit = itemView.findViewById(R.id.txt_loading_trans_unit);

            line = itemView.findViewById(R.id.line_view);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_swap:
                    if (mClickListener != null)
                        mClickListener.onRequestSwap(mWalletInfo.getWalletEntries().get(0),
                                mWalletInfo.getWalletEntries().get(getAdapterPosition()));
                    break;

                default:
                    if (mClickListener != null) {
                        if (!mWalletInfo.getWalletEntries().get(getAdapterPosition()).getBalance().isEmpty())
                            mClickListener.onItemClick(mWalletInfo.getWalletEntries().get(getAdapterPosition()));
                    }
            }
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(WalletEntry walletEntry);

        void onRequestSwap(WalletEntry own, WalletEntry coin);
    }
}