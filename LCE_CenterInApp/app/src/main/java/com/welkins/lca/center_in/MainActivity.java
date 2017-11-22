package com.welkins.lca.center_in;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import java.security.MessageDigest;
public class MainActivity extends AppCompatActivity {
    private EditText input;
    private EditText input2;
    private TextView output;
    private Button btn;
    private Button btn_next;
    private int btn1count=1;
    private String result;
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
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2){
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input=(EditText)findViewById(R.id.et);
        input2=(EditText)findViewById(R.id.et2);
        output=(TextView)findViewById(R.id.tv);
        btn=(Button)findViewById(R.id.btn1);
        btn_next = (Button)findViewById(R.id.btn2);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String sk="idfhdlhgdsihfildhilshgisdhigd";//center secret key
                String temp=input.getText().toString();
                String temp2=input2.getText().toString();

                byte[] sk_byte=new byte[sk.length()];
                byte[] outcome=new byte[temp.length()];
                byte[] outcome2=new byte[temp2.length()];
                try {
                    sk_byte = shaa(sk);
                    outcome = shaa(temp);
                    outcome2 = shaa(temp2);
                }catch (java.lang.Exception ex){
                    System.out.print("Exception!");
                }
                if(btn1count==1) {
                    result = And(outcome, sk_byte);
                    result =And(hexStringToByteArray(result),outcome2);//如果outcome2是空的會hash到字串
                }
                else
                    result =And(hexStringToByteArray(result),outcome2);
                output.setText(result);
                input2.setText("");
                btn1count++;
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String result_pass=output.getText().toString();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this , page2.class);
                intent.putExtra("Pass",result_pass);
                startActivity(intent);
            }
        });
    }
}