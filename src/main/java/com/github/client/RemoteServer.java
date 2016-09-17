package com.github.client;

import com.github.socks.ServerType;
import com.github.socks.SocksServer;
import com.github.utils.ConfigFileReader;


public class RemoteServer {
    public static void main(String[] args) throws Exception{
        String configPath = args[0];
        ConfigFileReader.readConfig(configPath);
        String server_port = ConfigFileReader.property("server_port");
        SocksServer ss = new SocksServer(server_port, ServerType.REMOTE_SERVER);
        System.out.println(" Listener on : "+server_port);
        ss.start();
    }
}
