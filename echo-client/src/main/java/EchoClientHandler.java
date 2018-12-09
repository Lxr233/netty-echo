import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;


public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private int counter = 0;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String req = "QUERY TIME ORDER"+System.getProperty("line.separator");
        for(int i=0;i<100;i++){
            ctx.writeAndFlush(Unpooled.copiedBuffer(req,
                    CharsetUtil.UTF_8));
        }

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf in) throws Exception {
        System.out.print("Now is: " +
                in.toString(CharsetUtil.UTF_8) +
                "; the counter is: " + ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
