/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package home.cliente.oit.app;

import com.google.gson.Gson;
import gnu.io.CommPortIdentifier;
import home.cliente.oit.util.RXTX;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.table.DefaultTableModel;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 *
 * @author hj
 */
public class JanelaPrincipal extends javax.swing.JFrame {

    private static ArrayList<Agendamento> agendamento = new ArrayList();
    private static Gson gson = new Gson();
    private Session session;
    DefaultTableModel model;
    private static RXTX arduino;
    private static Timer timer = new Timer();
    final long segundos = (1000);

    public JanelaPrincipal() {
        System.out.println("");
        initComponents();
        conectarComArduino();
        conectarComWebsocket();
        carregarTabela();
        timer.scheduleAtFixedRate(tarefa, 0, segundos);
    }

    public void carregarTabela() {
        jTable1.removeAll();
   
        model = new DefaultTableModel();
        model = (DefaultTableModel) jTable1.getModel();
        model.setNumRows(0);

        if (model.getColumnCount() == 0) {
            model.addColumn("Data");
            model.addColumn("Horas");
            model.addColumn("Ventilador");
            model.addColumn("Luz da Sala");
            model.addColumn("Luz do quarto");
            model.addColumn("TV");
        }
        for (Agendamento agenda : agendamento) {
            model.addRow(new Object[]{
                agenda.getData(),
                agenda.getHoras(),
                agenda.getVentilador(),
                agenda.getLuzSala(),
                agenda.getLuzQuarto(),
                agenda.getTv()
            });
        }
             
    }

    public String pegardata() {
        Date data = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        String dat;
        if (cal.get(Calendar.MONTH) < 10) {
            dat = String.valueOf(cal.get(Calendar.YEAR)) + "-" + "0" + String.valueOf(cal.get(Calendar.MONTH) + 1) + "-" + String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        } else {
            dat = String.valueOf(cal.get(Calendar.YEAR)) + "-" + String.valueOf(cal.get(Calendar.MONTH)) + "-" + String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        }
        return dat;

    }

    public String pegarHora() {
        String minuto;
        String seconds;
        Date data = new Date();
        if (data.getMinutes() < 10) {
            minuto = "0" + data.getMinutes();
        } else {
            minuto = String.valueOf(data.getMinutes());
        }
        if (data.getSeconds() < 10) {
            seconds = "0" + data.getSeconds();
        } else {
            seconds = String.valueOf(data.getSeconds());
        }

        return data.getHours() + ":" + minuto + ":" + seconds;
    }

    public String msgArduino(String v, String s, String q, String t) {
        String resposta = "";
        if (v.equals("L")) {
            resposta = resposta + "1";
        } else {
            resposta = resposta + "0";
        }
        if (s.equals("L")) {
            resposta = resposta + "1";
        } else {
            resposta = resposta + "0";
        }
        if (q.equals("L")) {
            resposta = resposta + "1";
        } else {
            resposta = resposta + "0";
        }
        if (t.equals("L")) {
            resposta = resposta + "1";
        } else {
            resposta = resposta + "0";
        }

        return resposta;
    }

    TimerTask tarefa = new TimerTask() {
        @Override
        public void run() {
            carregarTabela();

            for (Agendamento agenda : agendamento) {
                if ((agenda.getData().equals(pegardata())) && (agenda.getHoras().equals(pegarHora()))) {
                    if (arduino != null) {
                        try {
                            arduino.enviarDados(msgArduino(agenda.getVentilador(), agenda.getLuzSala(), agenda.getLuzQuarto(), agenda.getTv()));
                            // agendamento.remove(agenda);
                             campoComando.setText("id=" + agenda.getId() + " data=" + agenda.getData() + " horas=" + agenda.getHoras() + ", ventilador =" + agenda.getVentilador() + " luzSala =" + agenda.getLuzSala() + " luzQuarto =" + agenda.getLuzQuarto() + ", tv=" + agenda.getTv());
                            System.out.println(msgArduino(agenda.getVentilador(), agenda.getLuzSala(), agenda.getLuzQuarto(), agenda.getTv()));
                            System.out.println("id=" + agenda.getId() + " data=" + agenda.getData() + " horas=" + agenda.getHoras() + ", ventilador =" + agenda.getVentilador() + " luzSala =" + agenda.getLuzSala() + " luzQuarto =" + agenda.getLuzQuarto() + ", tv=" + agenda.getTv());

                        } catch (IOException ex) {
                            System.out.println(ex);
                        }
                    }
                }
            }
        }
    };

    private void conectarComArduino() {
        List<CommPortIdentifier> list = RXTX.listarPortasSeriais();
        if (list.size() > 0) {
            CommPortIdentifier porta = list.get(0);
            try {
                System.out.println("Conectado");
                arduino = new RXTX(porta, 9600);

            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }

    private void conectarComWebsocket() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        try {
            session = container.connectToServer(EndPoint.class, URI.create("ws://sysmyhome-iot.herokuapp.com/iot/websocket"));
        } catch (DeploymentException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }

    @ClientEndpoint
    public static class EndPoint {

        @OnOpen
        public void quandoConectar(Session session) {
            System.out.println("Conectado com servidorWebSocket!");
        }

        @OnMessage
        public void quandoChegarMensagem(String mensagem, Session session) {
            Mensagem msg = gson.fromJson(mensagem, Mensagem.class);
            if (msg.getDataEHora() != null) {
                campoHorario.setText(msg.getDataEHora());
            }
            if (msg.getComando() != null) {
                agendamento = new ArrayList();
                
                Agendamento[] agenda = gson.fromJson(msg.getComando(), Agendamento[].class);
                for (int i = 0; i < agenda.length; i++) {
                    agendamento.add(agenda[i]);
                } 
                
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        campoHorario = new javax.swing.JLabel();
        campoComando = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Horario");

        jLabel2.setText("Comando");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoHorario, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoComando, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(0, 516, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(campoHorario, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel1)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(campoComando, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addGap(41, 41, 41))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JLabel campoComando;
    private static javax.swing.JLabel campoHorario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
