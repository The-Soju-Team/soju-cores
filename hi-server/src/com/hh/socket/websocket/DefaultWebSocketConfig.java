/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.hh.socket.websocket;

public class DefaultWebSocketConfig implements WebSocketConfig {
    private boolean sslEnable;

    private String certChainFile;

    private String pkcs8PrivateKeyFile;

    private String bindTo = "localhost";

    private String externalPath;

    private String port = "9999";

    private String maxFrameSize = "20000000";

    public boolean sslEnabled() {
        return sslEnable;
    }

    public void setSslEnable(boolean sslEnable) {
        this.sslEnable = sslEnable;
    }

    public String getCertChainFilename() {
        return certChainFile;
    }

    public void setCertChainFile(String certChainFile) {
        this.certChainFile = certChainFile;
    }

    public String getPrivateKeyFilename() {
        return pkcs8PrivateKeyFile;
    }

    public void setPkcs8PrivateKeyFile(String pkcs8PrivateKeyFile) {
        this.pkcs8PrivateKeyFile = pkcs8PrivateKeyFile;
    }

    public String getBindTo() {
        return bindTo;
    }

    public void setBindTo(String bindTo) {
        this.bindTo = bindTo;
    }

    public String getExternalPath() {
        return externalPath;
    }

    public void setExternalPath(String externalPath) {
        this.externalPath = externalPath;
    }

    public int getPort() {
        return Integer.parseInt(port);
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int maxFrameSize() {
        return Integer.parseInt(maxFrameSize);
    }

    public void setMaxFrameSize(int maxFrameSize) {
        this.maxFrameSize = "" + maxFrameSize;
    }

}
