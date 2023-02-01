package com.nio.sample.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConnectionConfig {

  private static final String HOSTNAME = "server.hostname";
  private static final String CONTROL_PORT = "server.control.port";
  private static final String SENDING_PORT = "server.sending.port";

  private static ConnectionConfig instance;

  private final Properties properties;

  private ConnectionConfig() {
    properties = new Properties();
    try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
      properties.load(is);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static ConnectionConfig getInstance() {
    if (instance == null) {
      instance = new ConnectionConfig();
    }
    return instance;
  }

  public String getHostname() {
    return getProperty(HOSTNAME);
  }

  public int getControlPort() {
    return Integer.parseInt(getProperty(CONTROL_PORT));
  }

  public int getSendingPort() {
    return Integer.parseInt(getProperty(SENDING_PORT));
  }

  private String getProperty(String propertyKey) {
    return properties.getProperty(propertyKey);
  }
}
