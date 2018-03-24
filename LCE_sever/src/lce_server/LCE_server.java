package lce_server;
import java.io.BufferedReader;
import java.util.Base64;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class LCE_server {
	private static int serverport = 5050;
    private static ServerSocket serverSocket;
    private final static String IvAES = "1234567890abcdef" ;
    private final static String KeyAES = "abcdefghijklmnopqrstuvwxyz123456" ;//AES用key
    private final static String CenterKey ="idfhdlhgdsihfildhilshgisdhigdxxx" ;//中心key
 
    // 程式進入點
    public static void main(String[] args) {
    	        
        try {
            serverSocket = new ServerSocket(serverport);
            System.out.println("Server is start.");
 
            // 當Server運作中時
            while (!serverSocket.isClosed()) {
                // 顯示等待客戶端連接
                System.out.println("Wait new clinet connect");
 
                // 呼叫等待接受客戶端連接
                waitNewPlayer();
            }
 
        } catch (IOException e) {
            System.out.println("Server Socket ERROR");
        }
 
        
    }
    //SHA256
    public static byte[] shaa(String args) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final byte[] sha256 = md.digest(args.getBytes("UTF-8"));
        return sha256;
    }
    //AND
    public static String AND(byte[] by1,byte[] by2)
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
    //字串轉byte陣列
    private static byte[] stringToBytes(String input) {
        int length = input.length();
        byte[] output = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            output[i / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4) | Character.digit(input.charAt(i+1), 16));
        }
        return output;
    }
    //byte陣列轉16進位字串
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
    //AES加密
	private static byte[] EncryptAES(byte[] iv, byte[] key,byte[] text)
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
    //AES解密
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
    // 等待接受客戶端連接
    public static void waitNewPlayer() {
        try {
            Socket socket = serverSocket.accept();
 
            // 呼叫創造新的使用者
            createNewPlayer(socket);
        } catch (IOException e) {
 
        }
 
    }
 
    // 創造新的使用者
    public static void createNewPlayer(final Socket socket) {
 
        // 以新的執行緒來執行
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 取得網路串流 
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
 
                    // 當Socket已連接時連續執行
                    while (socket.isConnected()) {
                        // 取得網路串流的訊息
                        String inTextAES= br.readLine();
                        String inTextAES2= br.readLine();
                        /*AES解密失敗*/
                        System.out.println("inTextAES");
                        System.out.println(inTextAES);
                        System.out.println("inTextAES2");
                        System.out.println(inTextAES2);
                        byte[] inTextByte = DecryptAES(IvAES.getBytes("UTF-8"), stringToBytes(KeyAES),Base64.getDecoder().decode(inTextAES.getBytes("UTF-8")));
                        byte[] inTextByte2 = DecryptAES(IvAES.getBytes("UTF-8"), stringToBytes(KeyAES),Base64.getDecoder().decode(inTextAES2.getBytes("UTF-8")));
                        String TEXT = new String(inTextByte,"UTF-8");
                        String TEXT2 = new String(inTextByte2,"UTF-8");
                        // 2段AES解密並合併，輸出user id's hash
                        String mergeTEXT =TEXT+TEXT2;
                        System.out.println("mergeTEXT");
                        System.out.println(mergeTEXT);
                        
                        String afterAND=null;
                        byte[] CenterKeySHA =new byte[CenterKey.length()];
                        try {
                        	CenterKeySHA=shaa(CenterKey);//center key sha256
                        	afterAND=AND(stringToBytes(mergeTEXT),CenterKeySHA);//and(id's hash,center key)
                        }catch (java.lang.Exception ex){
                            System.out.print("Exception!");
                        }
                        //and後一樣切2段傳回去
                        String afterAND0_32=afterAND.substring(0,32);
                        String afterAND33_64=afterAND.substring(32,64);
                        byte[] outTextByte = EncryptAES(IvAES.getBytes("UTF-8"), stringToBytes(KeyAES),afterAND0_32.getBytes("UTF-8"));
                        byte[] outTextByte2 = EncryptAES(IvAES.getBytes("UTF-8"), stringToBytes(KeyAES),afterAND33_64.getBytes("UTF-8"));
                        String outTextAES = Base64.getEncoder().encodeToString(outTextByte);
                        String outTextAES2 = Base64.getEncoder().encodeToString(outTextByte2);
                        System.out.println("afterAND");
                        System.out.println(afterAND);
                        System.out.println("outTextAES");
                        System.out.println(outTextAES);
                        System.out.println("outTextAES2");
                        System.out.println(outTextAES2);
                        // 廣播訊息給其它的客戶端
                        toClientMsg(outTextAES,socket);
                        toClientMsg(outTextAES2,socket);
                    }
 
                } catch (IOException e) {
                }
         
            }
        });
 
        // 啟動執行緒
        t.start();
    }
 
    // 廣播訊息給其它的客戶端
    public static void toClientMsg(String Msg,Socket socket){
            try {
                // 創造網路輸出串流
                BufferedWriter bw;
                bw = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()));
                // 寫入訊息到串流
                bw.write(Msg+"\n");
                // 立即發送
                bw.flush();
            } catch (IOException e) {
 
            }
        }
    
}