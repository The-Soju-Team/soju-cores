package com.hh.webservice.websocket;

import com.hh.net.httpserver.HttpExchange;
import com.hh.server.HHServer;
import com.hh.web.HttpUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

public class WebsocketActor {
    private HttpUtils httpUtil;
    
    public WebsocketActor(HttpUtils hu) {
        httpUtil = hu;
    }    
    
    public void onReceive() {
        try {
            handleHttpRequest();
        } catch(Exception ex) {
            HHServer.mainLogger.error("HHServer error: ", ex);
        }
    }
    
    private void handleHttpRequest() throws SocketException, IOException, NoSuchAlgorithmException {
        HttpExchange exchange = httpUtil.httpExchange;
        exchange.getResponseHeaders().set("Connection", "Upgrade");
        exchange.getResponseHeaders().set("Upgrade", "websocket");
        String swAccept = exchange.getRequestHeaders().getFirst("Sec-WebSocket-Key")
                + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        swAccept = new String(Base64.encodeBase64(
                MessageDigest.getInstance("SHA-1").digest(swAccept.getBytes("UTF-8"))));
        exchange.getResponseHeaders().set("Sec-WebSocket-Accept", swAccept);
        exchange.sendResponseHeaders(101, -1); // 101: Switching Protocols
        
        Socket client = exchange.getChannel().socket();
        httpUtil.websocketConnection.setChannel(client);
        httpUtil.websocketConnection.onConnect();
        client.setKeepAlive(true);
        try(InputStream input = client.getInputStream();)
        {
            byte[] bufData = new byte[205];
            int nRead;
            boolean isClose = false;
            while (!isClose) {
                try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();) {
                    // Check lai truong hop bang 205
                    while ((nRead = input.read(bufData, 0, bufData.length)) != -1) {
                        buffer.write(bufData, 0, nRead);
                        if(nRead < 205) break; 
                    }
                    if(nRead != -1) {
                        buffer.flush();
                        WebsocketFrame.Frame data = WebsocketFrame.decodeWebsocketFrame(buffer.toByteArray());
                        byte[] payload = data.payload;
                        // close socket message
                        if(payload.length == 2 && payload[0] == 3 && payload[1] == -23) { 
                            httpUtil.websocketConnection.onDisconnect();
                            httpUtil.websocketConnection.disconnect();
                            break;
                        } else {
                            httpUtil.websocketConnection.onReceive(data.payload);
                        }
                    }
                    buffer.close();
                } catch(Exception ex) {
                    httpUtil.websocketConnection.onDisconnect();
                    httpUtil.websocketConnection.disconnect();
                    isClose = true;
                    HHServer.mainLogger.error("HHServer error: ", ex);
                    break;
                }
            } 
        }
    }
}
