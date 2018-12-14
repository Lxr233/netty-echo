

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.codec.msgpack.MsgpackDecoder;
import io.netty.codec.msgpack.MsgpackEncoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;

public class EchoServer {
    private final int port;

    public EchoServer(int port){
        this.port = port;
    }

    public static void main(String[] args) throws Exception{
        if(args.length!=1){
            System.err.println("Usage: "+ EchoServer.class.getSimpleName()+"<port>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        System.out.println("Echo Server start! port:"+port);
        new EchoServer(port).start();

    }

    private void start() throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
                            socketChannel.pipeline().addLast("msgpack decoder",new MsgpackDecoder());
                            socketChannel.pipeline().addLast("frameEncoder",new LengthFieldPrepender(2));
                            socketChannel.pipeline().addLast("msgpack encoder",new MsgpackEncoder());
                            socketChannel.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        }
        finally {
            group.shutdownGracefully().sync();
        }
    }
}
