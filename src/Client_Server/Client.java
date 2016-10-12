/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client_Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    public static void main(String[] args) throws IOException {
        
        File outdir = new File("copiedfiles");
        if (!outdir.isDirectory()) {
            outdir.mkdirs();
        }
        String fileName = "video.avi";
        Socket sock = new Socket("localhost", 1122);
        System.out.println("connecting.....");
        OutputStream os = sock.getOutputStream();
        PrintWriter pw = new PrintWriter(os, true);
        pw.println(fileName);
        BufferedReader bfr = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        String fileSize = bfr.readLine();
        
        DataInputStream clientData = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
        OutputStream output = new BufferedOutputStream(new FileOutputStream(new File(outdir, "received_from_client1_" + fileName)));
        long size = Integer.parseInt(fileSize);
        long bytesRemaining = size;
        byte[] buffer = new byte[sock.getReceiveBufferSize()];
        int bytesRead = 0;
        System.out.println("going to loop");
        while (bytesRemaining > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, bytesRemaining))) >= 0) {
            output.write(buffer, 0, bytesRead);
            bytesRemaining -= bytesRead;
            System.out.println("BytesRemaining "+bytesRemaining);
        }
        output.flush();
    }
}