package com.l1d000.spp;

public interface BluetoothSerialRawListener extends BluetoothSerialListener {

    /**
     * Specified message is read from the serial port.
     *
     * @param bytes The byte array read.
     */
    void onBluetoothSerialReadRaw(byte[] bytes);

    /**
     * Specified message is written to the serial port.
     *
     * @param bytes The byte array written.
     */
    void onBluetoothSerialWriteRaw(byte[] bytes);

}
