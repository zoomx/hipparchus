package serial;

import orchestration.Orchestrator;
import gnu.io.*;
import gui.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TooManyListenersException;
import java.awt.Color;

public class Communicator implements SerialPortEventListener {

    Orchestrator mh = new Orchestrator();
    private Enumeration<?> ports = null;
    private HashMap<String, CommPortIdentifier> portMap = new HashMap<String, CommPortIdentifier>();
    private CommPortIdentifier selectedPortIdentifier = null;
    private SerialPort serialPort = null;
    private InputStream input = null;
    private OutputStream output = null;
    private boolean bConnected = false;
    private final static int TIMEOUT = 2000;
    private final static char END_OF_LINE = '\n';
    private final static int BAUD_RATE = 9600;

    /*
     * TODO: Fix this in order to be the proper size by adding the
     * carriage return as an extra character
     */
    private final static int MESSAGE_SIZE = 100;
    private String arduinoMessage = "";

    public Communicator() {
    }

    public ArrayList<String> searchForPorts() {
        ports = CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> portList = new ArrayList<String>();
        while (ports.hasMoreElements()) {
            CommPortIdentifier curPort = (CommPortIdentifier) ports.nextElement();
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                portList.add(curPort.getName());
                portMap.put(curPort.getName(), curPort);
            }
        }
        return portList;
    }

    public void connect(String port) {
        selectedPortIdentifier = (CommPortIdentifier) portMap.get(port);
        CommPort commPort = null;
        try {

            commPort = selectedPortIdentifier.open("ArdustellarMediator", TIMEOUT);
            serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(
                    BAUD_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            setConnected(true);
            GuiUpdater.updateLog("Port " + port + " connected.");
        } catch (PortInUseException e) {
            GuiUpdater.updateLog(
                    "Connection Error: Port "
                    + port + " is in use. Error message:\n"
                    + e.toString(), Color.RED);
        } catch (Exception e) {
            GuiUpdater.updateLog(
                    "Connection Error: Failed to open port "
                    + port +". Error message:\n"
                    + e.toString(), Color.RED);
        }
    }

    public boolean initIOStream() {

        boolean successful = false;
        try {
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();
            successful = true;
            return successful;
        } catch (IOException e) {
            GuiUpdater.updateLog("I/O Streams failed to open. (" + e.toString() + ")", Color.RED);
            return successful;
        }
    }

    public void initListener() {
        try {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (TooManyListenersException e) {
            GuiUpdater.updateLog("Too many listeners. (" + e.toString() + ")", Color.RED);
        }
    }

    public void disconnect() {
        try {
            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
            setConnected(false);
            GuiUpdater.updateLog("Serial port disconnected.");
        } catch (Exception e) {
            GuiUpdater.updateLog("Failed to close " + serialPort.getName() + "(" + e.toString() + ")", Color.RED);
        }
    }

    final public boolean getConnected() {
        return bConnected;
    }

    public void setConnected(boolean bConnected) {
        this.bConnected = bConnected;
    }

    @Override
    public void serialEvent(SerialPortEvent evt) {

        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {

            byte[] buffer = new byte[MESSAGE_SIZE];
            int data;
            try {
                int len = 0;
                while ((data = input.read()) > -1) {

                    if (data == END_OF_LINE) {
                        break;
                    }
                    buffer[len++] = (byte) data;
                }
                arduinoMessage = (new String(buffer, 0, len));
                mh.setArduinoMessage(arduinoMessage);
                mh.getMessage();
            } catch (Exception e) {
                GuiUpdater.updateLog("Failed to get data from serial. (" + e.toString() + ")", Color.RED);
            }
        }
    }

    public void writeData(byte[] cmd) {
        try {
            output.write(cmd);
            output.flush();
        } catch (Exception e) {
            GuiUpdater.updateLog("Failed to send data to serial. (" + e.toString() + ")", Color.RED);
        }
    }
}