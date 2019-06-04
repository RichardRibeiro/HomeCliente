/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package home.cliente.oit.util;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author Gustavo
 */
public class RXTX {

    private OutputStream serialOut;
    private SerialPort serialPort;
    private InputStream serialIn;
    private byte[] readBuffer;

    public RXTX(CommPortIdentifier porta, int baudRate) throws Exception {
        conectar(porta, baudRate);
        readBuffer = new byte[400];
    }

    private static List<CommPortIdentifier> listaDePortas;

    public static List<CommPortIdentifier> listarPortasSeriais() {
        Enumeration<CommPortIdentifier> identificadoresDasPortas = CommPortIdentifier.getPortIdentifiers();
        listaDePortas = new ArrayList<CommPortIdentifier>();
        CommPortIdentifier porta;
        while (identificadoresDasPortas.hasMoreElements()) {
            porta = identificadoresDasPortas.nextElement();
            listaDePortas.add(porta);
        }
        return listaDePortas;
    }

    private void conectar(CommPortIdentifier porta, int baudRate) throws Exception {
        serialPort = (SerialPort) porta.open("Comunicação serial", baudRate);
        serialOut = serialPort.getOutputStream();
        serialIn = serialPort.getInputStream();

        serialPort.setSerialPortParams(baudRate,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
    }

    public void desconectar() throws Exception {
        serialPort.close();
        serialOut.close();
    }

    public void enviarDados(String mensagem) throws IOException {
        serialOut.write(mensagem.getBytes());
    }

    public String receberDados() throws IOException {
        int availableBytes = serialIn.available();
        if (availableBytes > 0) {
            serialIn.read(readBuffer, 0, availableBytes);
            return new String(readBuffer, 0, availableBytes);
        }
        return null;
    }
}
