package org.dyndns.fzoli.http;

/**
 *
 * @author zoli
 */
public interface CountingListener {
    void onWrite(long length, long transferred);
}