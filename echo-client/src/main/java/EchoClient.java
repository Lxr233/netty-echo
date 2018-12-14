import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.codec.msgpack.MsgpackDecoder;
import io.netty.codec.msgpack.MsgpackEncoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;


import java.net.InetSocketAddress;

public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host,int port){
        this.port = port;
        this.host = host;
    }

    public void start() throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host,port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel )throws Exception{
                            socketChannel.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
                            socketChannel.pipeline().addLast("msgpack decoder",new MsgpackDecoder());
                            socketChannel.pipeline().addLast("frameEncoder",new LengthFieldPrepender(2));
                            socketChannel.pipeline().addLast("msgpack encoder",new MsgpackEncoder());
                            socketChannel.pipeline().addLast(new EchoClientHandler());

                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        }
        finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        if(args.length !=2){
            System.err.println("Usage: " + EchoClient.class.getSimpleName()+"<host><port>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        new EchoClient(host,port).start();
    }
}
