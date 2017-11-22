package com.welkins.lce.client;

        import android.content.Intent;
        import android.os.Handler;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Base64;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;
        import java.net.InetAddress;
        import java.net.Socket;
        import java.security.MessageDigest;
        import java.security.spec.AlgorithmParameterSpec;

        import javax.crypto.Cipher;
        import javax.crypto.spec.IvParameterSpec;
        import javax.crypto.spec.SecretKeySpec;

        import static java.lang.Character.digit;

public class MainActivity extends AppCompatActivity {
    public static Handler mHandler = new Handler();
    TextView TextView01;    // 用來顯示文字訊息
    EditText EditText01;    // 文字方塊
    EditText EditText02;
    String tmp1=null;
    String tmp2=null;     // 暫存文字訊息
    Socket clientSocket;    // 客戶端socket
    String AESID1;
    String AESID2;
    String result;
    String finalkey;
    String returnkey;
    String KeyAES="abcdefghijklmnopqrstuvwxyz123456";
    String serverIP;
    private final static String IvAES = "1234567890abcdef" ;
    private static String bytesToHexString(final byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for (final byte b : bytes) {
            if ((b & 0xF0) == 0) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(b & 0x00FF));
        }
        return sb.toString().toLowerCase();
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2){
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static byte[] shaa(String args) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final byte[] sha256 = md.digest(args.getBytes("UTF-8"));
        return sha256;
    }
    public static String And(byte[] by1,byte[] by2)
    {
        byte[][] byt1=new byte[16][2];
        for(int i=0;i<16;i++)
            for(int j=0;j<2;j++)
                byt1[i][j]=by1[i*2+j];
        byte[][] byt2=new byte[16][2];
        for(int i=0;i<16;i++)
            for(int j=0;j<2;j++)
                byt2[i][j]=by2[i*2+j];
        final int a[]=new int[16];
        final int b[]=new int[16];
        for(int i=0;i<16;i++)
        {
            a[i] = Integer.parseInt(bytesToHexString(byt1[i]), 16);
            b[i] = Integer.parseInt(bytesToHexString(byt2[i]), 16);
        }
        final int result[]=new int[16];
        for(int i=0;i<16;i++)
            result[i] = a[i] & b[i];
        final String output[]=new String[16];
        for(int i=0;i<16;i++)
            output[i] = String.format("%4s",Integer.toHexString(result[i])).replace(' ', '0');
        String o=output[0]+output[1]+output[2]+output[3]+output[4]+output[5]+output[6]+output[7]+output[8]
                +output[9]+output[10]+output[11]+output[12]+output[13]+output[14]+output[15];
        return o;
    }
    private static byte[] stringToBytes(String input) {
        int length = input.length();
        byte[] output = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            output[i / 2] = (byte) ((digit(input.charAt(i), 16) << 4) | digit(input.charAt(i+1), 16));
        }
        return output;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 從資源檔裡取得位址後強制轉型成文字方塊
        TextView01 = (TextView) findViewById(R.id.TextView01);
        EditText01=(EditText) findViewById(R.id.EditText01);
        // 以新的執行緒來讀取資料
        Thread t = new Thread(readData);
        // 啟動執行緒
        t.start();
        // 從資源檔裡取得位址後強制轉型成按鈕

        Button button1=(Button) findViewById(R.id.Button01);

        button1.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {

                String TextAES=EditText01.getText().toString();
                byte[] Textoutcome=new byte[TextAES.length()];
                try {
                    Textoutcome = shaa(TextAES);
                }catch (java.lang.Exception ex){
                    System.out.print("Exception!");
                }
                try {
                    String tempAESID=And(Textoutcome,Textoutcome);
                    byte[] TextByte1 = EncryptAES(IvAES.getBytes("UTF-8"), stringToBytes(KeyAES), tempAESID.substring(0,32).getBytes("UTF-8"));
                    byte[] TextByte2 = EncryptAES(IvAES.getBytes("UTF-8"), stringToBytes(KeyAES), tempAESID.substring(32,64).getBytes("UTF-8"));
                    AESID1=Base64.encodeToString(TextByte1, Base64.DEFAULT);
                    AESID2=Base64.encodeToString(TextByte2, Base64.DEFAULT);
                }catch (java.lang.Exception ex){
                    System.out.print("Exception!");
                }
                // 如果已連接則
                if(clientSocket.isConnected()){

                    BufferedWriter bw;

                    try {
                        // 取得網路輸出串流
                        bw = new BufferedWriter( new OutputStreamWriter(clientSocket.getOutputStream()));
                        // 寫入訊息
                        bw.write(AESID1);
                        // 立即發送
                        bw.flush();
                        bw.write(AESID2);
                        // 立即發送
                        bw.flush();
                    } catch (IOException e) {
                    }
                }
                EditText01.setText("");
            }
        });
        Button button4=(Button) findViewById(R.id.Button04);

        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    byte[] TextByte1 = DecryptAES(IvAES.getBytes("UTF-8"), stringToBytes(KeyAES),Base64.decode(tmp1.getBytes("UTF-8"), Base64.DEFAULT));
                    byte[] TextByte2 = DecryptAES(IvAES.getBytes("UTF-8"), stringToBytes(KeyAES),Base64.decode(tmp2.getBytes("UTF-8"), Base64.DEFAULT));
                    String tmp1TextByte = new String(TextByte1,"UTF-8");
                    String tmp2TextByte = new String(TextByte2,"UTF-8");
                    returnkey=tmp1TextByte+tmp2TextByte;
                }catch (java.lang.Exception ex){
                    System.out.print("Exception!");
                }
                finalkey=returnkey;
                TextView01.setText(finalkey);

                // TextView01.setText(tmp1/*.replaceAll("\\n", "")*/+'\n' + tmp2/*.replaceAll("\\n", "")*/ );

            }
        });
        Button button2=(Button) findViewById(R.id.Button02);

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String newmember=EditText01.getText().toString();
                byte[] outcome1=new byte[newmember.length()];

                try {
                    outcome1 = shaa(newmember);
                }catch (java.lang.Exception ex){
                    System.out.print("Exception!");
                }
                finalkey =And(outcome1,stringToBytes(finalkey));
                TextView01.setText(finalkey);
                EditText01.setText("");

            }
        });

        Button button3=(Button) findViewById(R.id.Button03);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(MainActivity.this ,  Main2Activity.class);
                intent.putExtra("Pass",finalkey);
                startActivity(intent);
            }
        });
    }
    // 顯示更新訊息
    private Runnable updateText = new Runnable() {
        public void run() {
            // 加入新訊息並換行
            TextView01.append(tmp1 + "\n" + tmp2 );
        }
    };

    // 取得網路資料
    private Runnable readData = new Runnable() {
        public void run() {
            // server端的IP
            InetAddress serverIp;

            try {
                // 以內定(本機電腦端)IP為Server端
                serverIp = InetAddress.getByName("140.117.213.10");
                int serverPort = 5050;
                clientSocket = new Socket(serverIp, serverPort);
                // 取得網路輸入串流
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));

                // 當連線後
                while (clientSocket.isConnected()) {
                    // 取得網路訊息
                    tmp1 = br.readLine();
                    // 如果不是空訊息則
                    tmp2 = br.readLine();
                    //  if(tmp1!=null)
                    // 顯示新的訊息
                    //  mHandler.post(updateText);
                    /*if(tmp2!=null)
                        // 顯示新的訊息
                        mHandler.post(updateText);*/

                }



            } catch (IOException e) {

            }
        }
    };

}