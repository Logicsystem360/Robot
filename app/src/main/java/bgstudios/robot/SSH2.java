package bgstudios.robot;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PipedReader;
import java.io.PipedWriter;

public class SSH2 extends Thread {

    final String user = "pi";
    final String password = "raspberry";
    final String hostname = "10.3.141.1";

    private PipedReader pr;
    private PipedWriter pw;

    SSH2(String name, PipedReader pr, PipedWriter pw) {
        super(name);

        this.pr = pr;
        this.pw = pw;
    }

    public void run() {
        try {

            System.out.println("Starting thread.");
            String s;

            JSch jsch=new JSch();
            Session session=jsch.getSession(user, hostname, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");

            session.connect();

            Channel channel=session.openChannel("shell");

            channel.connect();

            DataInputStream dataIn = new DataInputStream(channel.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(dataIn));
            DataOutputStream dataOut = new DataOutputStream(channel.getOutputStream());

            System.out.println("Starting telnet connection...");
            //dataOut.writeBytes(s +"\n");

            int item;
            while ((item = pr.read()) != -1){
                s = String.valueOf((char) item);
                System.out.print(s);
                dataOut.writeBytes(s);
                dataOut.flush();
            }
            /*
            String line = reader.readLine();
            String result = line +"\n";

            while ((line= reader.readLine())!=null){
                result += line +"\n";
            }
            dataIn.close();
            dataOut.close();
            channel.disconnect();
            session.disconnect();*/
        } catch (Exception e) {
                e.printStackTrace();
        }
    }
}
