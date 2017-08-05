package pro.charlychiu.androidasynchw;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    TestTask task = new TestTask();
    private BufferedWriter bw;
    private BufferedReader br;
    private String tmp;
    EditText edt;
    TextView textview;
    String globalText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        task.execute();

        final Button button = (Button) findViewById(R.id.button);
        final Button button_offline = (Button) findViewById(R.id.button1);
        edt = (EditText)findViewById(R.id.editText);
        textview = (TextView)findViewById(R.id.server_data);

        //updateViewThred viewThred = new updateViewThred();
        //viewThred.start();

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    task.testing("OAO");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        button_offline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    task.offline();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
/*
        while(true)
        {
            textview.setText(globalText);
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */
    }
    class TestTask extends AsyncTask<Void,Void,Void> {

        ArrayList<String> test = new ArrayList<String>();
        private Socket clientSocket;        //客戶端的socket

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //InetSocketAddress isa = new InetSocketAddress("10.1.1.5",3333);
                //SocketChannel sc = SocketChannel.open(isa);
                //InetAddress serverIp = InetAddress.getByName("10.1.1.5");
                InetAddress serverIp = InetAddress.getByName("Input your socket server");
                int serverPort = 3333;
                clientSocket = new Socket(serverIp, serverPort);

                //取得網路輸出串流
                bw = new BufferedWriter( new OutputStreamWriter(clientSocket.getOutputStream()));
                // 取得網路輸入串流
                br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while (clientSocket.isConnected()) {
                    // 取得網路訊息
                    tmp = br.readLine();    //宣告一個緩衝,從br串流讀取值
                    // 如果不是空訊息
                    if(tmp!=null){
                        Log.d("***ServerMsg",tmp);
                        globalText = tmp;
                        publishProgress();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("text","Socket連線="+e.toString());
            }
            return null;
        }
        public void testing(String testStr) throws IOException {
            Log.d("***TestSendingStr","testing");
            bw = new BufferedWriter( new OutputStreamWriter(clientSocket.getOutputStream()));
            bw.write(edt.getText().toString()+"\n");
            bw.flush();
            //bw.close();
        }
        public void offline() throws IOException {
            Log.d("***TestSendingStr","offline");
            bw = new BufferedWriter( new OutputStreamWriter(clientSocket.getOutputStream()));
            bw.write("offline\n");
            bw.flush();
            bw.close();
            br.close();
            clientSocket.close();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if(globalText != "") {
                String[] getStr = globalText.split(" ");
                textview.setText("線上人數:" + getStr[0] + " " + "寶物數:" + getStr[1]);
            }
        }
    }

    class updateViewThred extends Thread {
        public void run() {
            while (true)
            {
                //textview.setText(globalText);

            }
        }
    }
}
