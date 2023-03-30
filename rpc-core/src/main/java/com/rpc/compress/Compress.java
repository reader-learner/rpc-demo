package com.rpc.compress;

import extension.SPI;

@SPI
public interface Compress {
    byte[] compress(byte[] bytes);

    byte[] deCompress(byte[] bytes);
}
