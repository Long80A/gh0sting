package com.github;

import com.github.socks.ServerType;
import com.github.socks.SocksServer;
import com.github.utils.ConfigFileReader;

/**
 * Created by Gh0st on 1/31/16.
 */
public class ServerDemo {
    public static void main(String[] args) throws Exception {
        SocksServer ss = new SocksServer(ConfigFileReader.property("server_port"), ServerType.REMOTE_SERVER);
        ss.start();
    }
}
