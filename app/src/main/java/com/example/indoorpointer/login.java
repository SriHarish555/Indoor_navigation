package com.example.indoorpointer;

import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class login {

    String response =new String();
    public Boolean auth(String roll,String pass)
    {
        SocketClient socketClient = new SocketClient("172.20.10.5", 501);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketClient.connect();
                    socketClient.send("roll%"+roll+"%pass:%"+pass);
                    response=socketClient.receive();
                    Log.d("SocketClient", "Received: "+response );
                    socketClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        try{Thread.sleep(1000);}catch(InterruptedException e){System.out.println(e);}
        System.out.println("-------------------------------hello"+response+"-----------------------");
        try{
            if(response.equals("SUCCESS"))
            {
                return true;
            }
        }
        catch(Exception e)
        {
            return false;
        }
        return false;
    }
}
