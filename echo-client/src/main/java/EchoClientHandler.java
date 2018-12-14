import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import pojo.UserInfo;


public class EchoClientHandler extends SimpleChannelInboundHandler {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i=0;i<3;i++){
            UserInfo userInfo = new UserInfo();
            userInfo.setAge(i);
            userInfo.setName("Name-->"+i);
            ctx.write(userInfo);
        }
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
//        UserInfo userInfo = (UserInfo)msg;
        System.out.println("Client receive the msgpack message , Name is:"+msg);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
