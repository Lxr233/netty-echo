package io.netty.codec.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;


public class MsgpackDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        final byte[] array;
        System.out.println("server decode");
        /**
         * 如果ByteToMessageDecoder前没有添加分包粘包支持
         * 则此处传入的byteBuf是所有的字节，不会分开
         * 即如果传入两个int，则需要调用两次byteBuf.readInt()来解码
         * 具体参考《netty实战》p127
         */
        final int length = byteBuf.readableBytes();
        array = new byte[length];
        /**
         * 这里要使用byteBuf.readBytes，而不能使用getBytes
         * 如果List<Object> out增长的话,每次都必须从in里读出一些字节
         * 具体参考https://blog.csdn.net/zougen/article/details/79047252
         */
        byteBuf.readBytes(array,0,length);
        MessagePack messagePack = new MessagePack();
        list.add(messagePack.read(array));
    }
}