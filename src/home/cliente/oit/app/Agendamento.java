/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package home.cliente.oit.app;

/**
 *
 * @author hj
 */
public class Agendamento {

    private String id;
    private String data;
    private String horas;
    private String ventilador;
    private String luzSala;
    private String luzQuarto;
    private String tv;
    private String createdAt;
    private String updatedAt;

    // mesmo nome dos campos que esta sendo enviada pelo front-end em formatdo gson
      public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHoras() {
        return horas;
    }

    public void setHoras(String horas) {
        this.horas = horas;
    }

    public String getVentilador() {
        return ventilador;
    }

    public void setVentilador(String ventilador) {
        this.ventilador = ventilador;
    }

    public String getLuzSala() {
        return luzSala;
    }

    public void setLuzSala(String luzSala) {
        this.luzSala = luzSala;
    }


    public String getLuzQuarto() {
        return luzQuarto;
    }

    public void setLuzQuarto(String luzQuarto) {
        this.luzQuarto = luzQuarto;
    }
    
       public String getTv() {
        return tv;
    }

    public void setTv(String tv) {
        this.tv = tv;
    }



    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Agendamento{" + "id=" + id + ", data=" + data + ", horas=" + horas + ", ventilador=" + ventilador + ", luzSala=" + luzSala + ", luzQuarto=" + luzQuarto + ", tv=" + tv + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
    }
    
    

}
