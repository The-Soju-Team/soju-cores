/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.webservice.websocket;

import java.nio.ByteBuffer;

/**
 *
 * @author hiendm1
 */
public class WebsocketFrame {    
    public static class Frame {
        byte opcode;
        boolean fin;
        byte payload[];
    }

    public static Frame decodeWebsocketFrame(byte raw[]) {
        // easier to do this via ByteBuffer
        ByteBuffer buf = ByteBuffer.wrap(raw);

        // Fin + RSV + OpCode byte
        Frame frame = new Frame();
        byte b = buf.get();
        frame.fin = ((b & 0x80) != 0);
        boolean rsv1 = ((b & 0x40) != 0);
        boolean rsv2 = ((b & 0x20) != 0);
        boolean rsv3 = ((b & 0x10) != 0);
        frame.opcode = (byte) (b & 0x0F);

        // TODO: add control frame fin validation here
        // TODO: add frame RSV validation here
        // Masked + Payload Length
        b = buf.get();
        boolean masked = ((b & 0x80) != 0);
        int payloadLength = (byte) (0x7F & b);
        int byteCount = 0;
        if (payloadLength == 0x7F) {
            // 8 byte extended payload length
            byteCount = 8;
        } else if (payloadLength == 0x7E) {
            // 2 bytes extended payload length
            byteCount = 2;
        }

        // Decode Payload Length
        while (--byteCount > 0) {
            b = buf.get();
            payloadLength |= (b & 0xFF) << (8 * byteCount);
        }

        // TODO: add control frame payload length validation here
        byte maskingKey[] = null;
        if (masked) {
            // Masking Key
            maskingKey = new byte[4];
            buf.get(maskingKey, 0, 4);
        }

        // TODO: add masked + maskingkey validation here
        // Payload itself
        frame.payload = new byte[payloadLength];
        buf.get(frame.payload, 0, payloadLength);

        // Demask (if needed)
        if (masked) {
            for (int i = 0; i < frame.payload.length; i++) {
                frame.payload[i] ^= maskingKey[i % 4];
            }
        }

        return frame;
    }
    
    public static byte[] encodeWebsocketFrame(byte[] rawData) {
        int frameCount = 0;
        byte[] frame = new byte[10];

        frame[0] = (byte) 129;

        if (rawData.length <= 125) {
            frame[1] = (byte) rawData.length;
            frameCount = 2;
        } else if (rawData.length >= 126 && rawData.length <= 65535) {
            frame[1] = (byte) 126;
            int len = rawData.length;
            frame[2] = (byte) ((len >> 8) & (byte) 255);
            frame[3] = (byte) (len & (byte) 255);
            frameCount = 4;
        } else {
            frame[1] = (byte) 127;
            int len = rawData.length;
            frame[2] = (byte) ((len >> 56) & (byte) 255);
            frame[3] = (byte) ((len >> 48) & (byte) 255);
            frame[4] = (byte) ((len >> 40) & (byte) 255);
            frame[5] = (byte) ((len >> 32) & (byte) 255);
            frame[6] = (byte) ((len >> 24) & (byte) 255);
            frame[7] = (byte) ((len >> 16) & (byte) 255);
            frame[8] = (byte) ((len >> 8) & (byte) 255);
            frame[9] = (byte) (len & (byte) 255);
            frameCount = 10;
        }

        int bLength = frameCount + rawData.length;

        byte[] reply = new byte[bLength];

        int bLim = 0;
        for (int i = 0; i < frameCount; i++) {
            reply[bLim] = frame[i];
            bLim++;
        }
        for (int i = 0; i < rawData.length; i++) {
            reply[bLim] = rawData[i];
            bLim++;
        }
        return reply;
    }
}
