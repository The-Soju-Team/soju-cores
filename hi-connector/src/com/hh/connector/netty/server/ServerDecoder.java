package com.hh.connector.netty.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

/**
 * HienDM1
 */
public class ServerDecoder extends ByteToMessageDecoder {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ServerDecoder.class.getSimpleName());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            while (in.readableBytes() > 4) {
                in.markReaderIndex();
                int size = in.readInt();
                if (in.readableBytes() < size) {
                    in.resetReaderIndex();
                    return;
                }

                ByteBuf bb = in.readSlice(size);
                byte[] data = new byte[size];
                bb.readBytes(data);
                
                out.add(data);
            }
        } catch (Exception ex) {
            in.resetReaderIndex();
            log.error("Error when decode Netty: ", ex);
        }
    }

    public static byte[] mapToByteArray(LinkedTreeMap obj) throws IOException {
        byte[] data = null;
        try {
            Gson gson = new Gson();
            byte[] bytes = gson.toJson(obj).getBytes(Charset.forName("UTF-8"));

            int length = bytes.length;
            ByteBuffer buffer = ByteBuffer.allocate(4);
            byte[] lengthArr = buffer.putInt(length).array();

            data = new byte[length + 4];
            System.arraycopy(lengthArr, 0, data, 0, 4); // add do dai vao 4 byte dau
            System.arraycopy(bytes, 0, data, 4, length); // add du lieu Object
        } catch (Exception ex) {
            log.error("Error convert object to bytes: ", ex);
        }
        return data;
    }

    public static LinkedTreeMap byteArrayToMap(byte[] bytes) throws IOException, ClassNotFoundException {
        LinkedTreeMap obj = null;
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            String json = new String(bytes, Charset.forName("UTF-8"));
            obj = gson.fromJson(json, LinkedTreeMap.class);
        } catch (Exception ex) {
            log.error("Error convert bytes to object: ", ex);
        }
        return obj;
    }
}
