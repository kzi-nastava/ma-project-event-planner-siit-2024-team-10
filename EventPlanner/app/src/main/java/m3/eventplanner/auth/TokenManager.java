package m3.eventplanner.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

public class TokenManager {
    private static final String PREF_NAME = "jwt_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ROLE = "role";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ACCOUNT_ID = "account_id";
    private SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        DecodedJWT decodedJWT = JWT.decode(token);
        editor.putInt(KEY_USER_ID,decodedJWT.getClaim(KEY_USER_ID).asInt());
        editor.putInt(KEY_ACCOUNT_ID,decodedJWT.getClaim(KEY_ACCOUNT_ID).asInt());
        editor.putString(KEY_ROLE,decodedJWT.getClaim(KEY_ROLE).asString());
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, null);
    }

    public int getAccountId() {
        return sharedPreferences.getInt(KEY_ACCOUNT_ID, 0);
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, 0);
    }

    public void clearToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_ACCOUNT_ID);
        editor.remove(KEY_ROLE);
        editor.apply();
    }
}
