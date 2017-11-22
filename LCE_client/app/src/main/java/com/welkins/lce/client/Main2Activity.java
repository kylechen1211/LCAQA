package com.welkins.lce.client;

        import android.content.Context;
        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Base64;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

        import java.security.spec.AlgorithmParameterSpec;

        import javax.crypto.Cipher;
        import javax.crypto.spec.IvParameterSpec;
        import javax.crypto.spec.SecretKeySpec;

        import static java.lang.Character.digit;

public class Main2Activity extends AppCompatActivity {

    private EditText input_p2;
    private TextView output_p2;
    private TextView output2_p2;
    private TextView output_key;
    private Button btn_call;
    private Button btn_encry;
    private Button btn_decry;
    private final static String IvAES = "1234567890abcdef" ;

    private static byte[] stringToBytes(String input) {
        int length = input.length();
        byte[] output = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            output[i / 2] = (byte) ((digit(input.charAt(i), 16) << 4) | digit(input.charAt(i+1), 16));
        }
        return output;
    }
    private void shareTo(String subject, String body, String chooserTitle) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(sharingIntent, chooserTitle));
    }
    private static byte[] EncryptAES(byte[] iv, byte[] key,byte[] text) //key=abc
    {
        try
        {
            AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
            Cipher mCipher = null;
            mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            mCipher.init(Cipher.ENCRYPT_MODE,mSecretKeySpec,mAlgorithmParameterSpec);

            return mCipher.doFinal(text);
        }
        catch(Exception ex)
        {
            return null;
        }
    }
    //AES解密，帶入byte[]型態的16位英數組合文字、32位英數組合Key、需解密文字
    private static byte[] DecryptAES(byte[] iv,byte[] key,byte[] text)
    {
        try
        {
            AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
            Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            mCipher.init(Cipher.DECRYPT_MODE,
                    mSecretKeySpec,
                    mAlgorithmParameterSpec);

            return mCipher.doFinal(text);
        }
        catch(Exception ex)
        {
            return null;
        }
    }
    private String copyFromClipboard(){
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < clipboard.getPrimaryClip().getItemCount(); i++){
                sb.append(clipboard.getPrimaryClip().getItemAt(i).getText());
            }
            return sb.toString();
        } else {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            return clipboard.getText().toString();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        input_p2=(EditText)findViewById(R.id.et_p2);
        output_p2=(TextView)findViewById(R.id.tv_p2);
        output2_p2=(TextView)findViewById(R.id.tv2_p2);
        output_key=(TextView)findViewById(R.id.tv_key);
        btn_call=(Button)findViewById(R.id.btn_call);
        btn_encry = (Button)findViewById(R.id.btn1);
        btn_decry = (Button)findViewById(R.id.btn2);
        btn_call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = getIntent();
                String key = intent.getStringExtra("Pass");
                output_key.setText(key);

            }
        });
        btn_encry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String TextAES=input_p2.getText().toString();
                String KeyAES=output_key.getText().toString();
                try {
                    byte[] TextByte = EncryptAES(IvAES.getBytes("UTF-8"), stringToBytes(KeyAES), TextAES.getBytes("UTF-8"));
                    String TEXT = Base64.encodeToString(TextByte, Base64.DEFAULT);
                    shareTo("subject",TEXT,"chooserTitle");
                    output_p2.setText("密文: "+TEXT);
                }catch (java.lang.Exception ex){
                    System.out.print("Exception!");
                }
            }
        });
        btn_decry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                output_p2.setText("擷取之密文:"+copyFromClipboard());
                String TextAES=output_p2.getText().toString();
                String KeyAES=output_key.getText().toString();
                try {
                    byte[] TextByte = DecryptAES(IvAES.getBytes("UTF-8"), stringToBytes(KeyAES),Base64.decode(TextAES.getBytes("UTF-8"), Base64.DEFAULT));
                    String TEXT = new String(TextByte,"UTF-8");

                    output2_p2.setText("明文:"+TEXT);
                }catch (java.lang.Exception ex){
                    System.out.print("Exception!");
                }
            }
        });
    }
}