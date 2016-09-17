package com.github.client;

import com.github.socks.ServerType;
import com.github.socks.SocksServer;
import com.github.utils.ConfigFileReader;


public class LocalServer {
    public static void main(String[] args) throws Exception{
        String configPath = args[0];
        ConfigFileReader.readConfig(configPath);
        String local_port = ConfigFileReader.property("local_port");
        SocksServer ss = new SocksServer(local_port, ServerType.LOCAL_SERVER);
        System.out.println(" Listener on : "+local_port);
        ss.start();
    }
}
