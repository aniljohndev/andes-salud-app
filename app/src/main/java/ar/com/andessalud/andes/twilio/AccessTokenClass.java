package ar.com.andessalud.andes.twilio;

import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.VideoGrant;

import ar.com.andessalud.andes.BuildConfig;

public class AccessTokenClass {
   /* public static final String twilioAccountSid = BuildConfig.TWILIO_ACCOUNT_SID;
    public static final String twilioApiKey = BuildConfig.TWILIO_API_KEY_TOKEN;
    public static final String twilioApiSecret = BuildConfig.TWILIO_API_KEY_SECRET;


        // Required for Video

        // Create Video grant
//        VideoGrant grant = new VideoGrant().setRoom("cool room");


    public static  String generateToken() {P
        String identity = "user";

        final VideoGrant grant = new VideoGrant();
        grant.setRoom("cool room");

        // Create an Access Token
        AccessToken token = new AccessToken.Builder(
                twilioAccountSid,
                twilioApiKey,
                twilioApiSecret
        ).identity(identity).grant(grant).build();


        // Serialize the token as a JWT
        //        System.out.println(jwt);
        return token.toJwt();
    }*/
}
