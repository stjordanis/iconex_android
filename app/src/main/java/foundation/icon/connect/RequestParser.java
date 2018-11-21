package foundation.icon.connect;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.Locale;

import foundation.icon.iconex.R;
import foundation.icon.iconex.util.ConvertUtil;

public class RequestParser {
    private static final String TAG = RequestParser.class.getSimpleName();

    private final Context mContext;
    public static final int SUCCESS = 0;

    public RequestParser(Context context) {
        mContext = context;
    }

    public static RequestParser newInstance(Context context) {
        RequestParser rp = new RequestParser(context);
        return rp;
    }

    public int requestValidate(RequestData request) throws ErrorCodes.Error {
        String data = request.getData();
        String caller = request.getCaller();
        String receiver = request.getReceiver();

        if (caller == null || caller.isEmpty())
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND_CALLER, mContext.getString(R.string.descNotFoundCaller));

        checkCaller(caller, receiver);

        if (receiver == null || receiver.isEmpty())
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND_CALLER, mContext.getString(R.string.descNotFoundCaller));

        if (data == null || data.isEmpty())
            throw new ErrorCodes.Error(ErrorCodes.ERR_PARSE, mContext.getString(R.string.descParseError));

        return SUCCESS;
    }

    public Constants.Method getMethod(String data) throws ErrorCodes.Error {
        JSONObject requestData = getData(data);

        if (requestData == null)
            throw new ErrorCodes.Error(ErrorCodes.ERR_PARSE, mContext.getString(R.string.descParseError));

        try {
            String method = requestData.getString("method");
            switch (method) {
                case "bind":
                    return Constants.Method.BIND;

                case "sign":
                    return Constants.Method.SIGN;

                case "sendICX":
                    return Constants.Method.SendICX;

                case "sendToken":
                    return Constants.Method.SendToken;
            }

            throw new ErrorCodes.Error(ErrorCodes.ERR_INVALID_METHOD, mContext.getString(R.string.descInvalidMethod));
        } catch (JSONException e) {
            throw new ErrorCodes.Error(ErrorCodes.ERR_PARSE, mContext.getString(R.string.descParseError));
        }
    }

    public String validateParameters(Constants.Method method, JSONObject params) throws ErrorCodes.Error {
        String address = null;
        switch (method) {
            case SIGN:
                address = valSignParams(params);
                break;

            case SendICX:
                address = valSendICXParams(params);
                break;

            case SendToken:
                address = valSendTokenParams(params);
                break;
        }

        return address;
    }

    private String valSignParams(JSONObject params) throws ErrorCodes.Error {
        String address;
        if (!params.has("version") || params.isNull("version"))
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "version"));

        if (!params.has("from") || params.isNull("from"))
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "from"));

        if (!params.has("to") || params.isNull("to"))
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "to"));

        if (!params.has("value") || params.isNull("value"))
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "value"));

        if (!params.has("stepLimit") || params.isNull("stepLimit"))
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "stepLimit"));

        if (!params.has("timestamp") || params.isNull("timestamp"))
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "timestamp"));

        if (!params.has("nid") || params.isNull("nid"))
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "nid"));

        if (!params.has("nonce") || params.isNull("nonce"))
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "nonce"));

        try {
            address = params.getString("from");
        } catch (JSONException e) {
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "from"));
        }

        return address;
    }

    private String valSendICXParams(JSONObject params) throws ErrorCodes.Error {
        String address;

        if (!params.has("from") || params.isNull("from"))
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "from"));

        if (!params.has("to") || params.isNull("to"))
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "to"));

        if (!params.has("value") || params.isNull("value"))
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "value"));

        try {
            address = params.getString("from");
        } catch (JSONException e) {
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "from"));
        }

        return address;
    }

    private String valSendTokenParams(JSONObject params) throws ErrorCodes.Error {
        String address;

        if (!params.has("from") || params.isNull("from"))
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "from"));

        if (!params.has("to") || params.isNull("to"))
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "to"));

        if (!params.has("value") || params.isNull("value"))
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "value"));

        if (!params.has("contractAddress") || params.isNull("contractAddress"))
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "contractAddress"));
        try {
            address = params.getString("from");
        } catch (JSONException e) {
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND,
                    String.format(Locale.getDefault(), mContext.getString(R.string.descNotFoundParameter), "from"));
        }

        return address;
    }

    public int getId(String data) {
        JSONObject jsonData = getData(data);
        try {
            return jsonData.getInt("id");
        } catch (JSONException e) {
            return -1;
        }
    }

    public JSONObject getData(String data) {
        byte[] base64Data = Base64.decode(data, Base64.NO_WRAP);
        JSONObject jsonData = null;
        try {
            jsonData = new JSONObject(new String(base64Data));
        } catch (JSONException e) {
            return jsonData;
        }

        return jsonData;
    }

    public JSONObject getParams(JSONObject data) {
        JSONObject jsonData = null;
        try {
            jsonData = data.getJSONObject("params");
        } catch (JSONException e) {
            try {
                JsonObject gson = new JsonParser().parse(data.getString("params")).getAsJsonObject();
                jsonData = new JSONObject(gson.toString());
            } catch (JSONException e1) {
                return null;
            }
        }

        return jsonData;
    }

    private int checkCaller(String caller, String receiver) throws ErrorCodes.Error {
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(caller, PackageManager.GET_RECEIVERS);
            for (ActivityInfo resReceiver : info.receivers) {
                if (resReceiver.name.equals(receiver))
                    return SUCCESS;
            }

            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND_CALLER, mContext.getString(R.string.descNotFoundCaller));
        } catch (PackageManager.NameNotFoundException e) {
            throw new ErrorCodes.Error(ErrorCodes.ERR_NOT_FOUND_CALLER, mContext.getString(R.string.descNotFoundCaller));
        }
    }

    public String paramsToString(JSONObject requestData) {
        StringBuilder sb = new StringBuilder();

        try {
            JSONObject params = getParams(requestData);

            sb.append("{").append("\n");
            while (params.keys().hasNext()) {
                String key = params.keys().next();
                if (key.equals("data")) {
                    JSONObject data = params.getJSONObject("data");
                    JSONObject dataParmas = data.getJSONObject("params");

                    sb.append("\t\"data: \": {\n")
                            .append("\t\t\"method\": ").append("\"" + data.getString("method") + "\"\n")
                            .append("\t\t\"parmas\": {\n");

                    while (dataParmas.keys().hasNext()) {
                        String dataKey = dataParmas.keys().next();
                        sb.append("\t\t\t\"" + dataKey + "\": ").append("\"" + dataParmas.getString(dataKey) + "\"\n");
                    }

                    sb.append("\t\t}\n");
                    sb.append("\t}\n");
                } else {
                    sb.append("\t\"" + key + "\": ").append("\"" + params.getString("version") + "\"\n");
                }
            }

            sb.append("}");
        } catch (JSONException e) {

        }

        return sb.toString();
    }
}
