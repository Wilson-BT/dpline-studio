import {config} from '@/utils/config'
import SocketIO from "vue-socket.io";
import ClientSocketIO from "socket.io-client";
const url = config.domain + config.apiRoot
const socketJs = new SocketIO({
    debug: false,//开启调试模式
    connection: ClientSocketIO.connect(url, {
        transports: ["websocket"],//默认使用的请求方式
        autoConnect: false,//是否自动连接
    }),
})
export default socketJs