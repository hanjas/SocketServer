/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client_Server;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {
    
    static Server s;
    static ServerSocket ss;
    static Socket clientSocket;
    
    public static void main(String[] args) throws IOException {
        s = new Server();
        ss = new ServerSocket(1122);
        while(true) {
            System.out.println("waiting for a connection....");
            clientSocket = ss.accept();
            Thread thread = new Thread(s);
            thread.start();
//            server();
        }
    }
    public void run() {
        try {
            server();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void server() throws IOException {
            System.out.println("accepted connection :"+clientSocket);
            
            BufferedReader bfr = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String fileName = bfr.readLine();
            System.out.println("server side got the file name : "+fileName);
            OutputStream os = clientSocket.getOutputStream();
            File myFile = new File(fileName);
            PrintWriter pw = new PrintWriter(os, true);
            pw.println(myFile.length());
            
            System.out.println(myFile.length());
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            byte[] buffer = new byte[clientSocket.getSendBufferSize()];
            
            long expect = myFile.length();
            long left = expect;
            int inlen = 0;
            while (left > 0 && (inlen = bis.read(buffer, 0, (int)Math.min(left, buffer.length))) >= 0) {
                dos.write(buffer, 0, inlen);
                left -= inlen;
            }
            dos.flush();
            if (left > 0) {
                throw new IllegalStateException("We expected " + expect + " bytes but came up short by " + left);
            }
            if (bis.read() >= 0) {
                throw new IllegalStateException("We expected only " + expect + " bytes, but additional data has been added to the file");
            }
    }
}