package br.com.infortech.model;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Parking implements Serializable {

    String nome;
    String data_criacao;
    int tipo;
    double latitude;
    double longitude;
    double taxa;
    String sinalizacaoAutor;
    LocalDateTime sinalizacaoHora;
    Boolean exclusividade_cliente;

    public Parking() {}

    public Parking(String nome, String data_criacao, int tipo, double latitude, double longitude, double taxa, String sinalizacaoAutor, LocalDateTime sinalizacaoHora, Boolean exclusividade_cliente) {
        this.nome = nome;
        this.data_criacao = data_criacao;
        this.tipo = tipo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.taxa = taxa;
        this.sinalizacaoAutor = sinalizacaoAutor;
        this.sinalizacaoHora = sinalizacaoHora;
        this.exclusividade_cliente = exclusividade_cliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getData_criacao() {
        return data_criacao;
    }

    public void setData_criacao(String data_criacao) {
        this.data_criacao = data_criacao;
    }

    public double getTaxa() {
        return taxa;
    }

    public void setTaxa(double taxa) {
        this.taxa = taxa;
    }

    public String getSinalizacaoAutor() {
        return sinalizacaoAutor;
    }

    public void setSinalizacaoAutor(String sinalizacaoAutor) {
        this.sinalizacaoAutor = sinalizacaoAutor;
    }

    public LocalDateTime getSinalizacaoHora() {
        return sinalizacaoHora;
    }

    public void setSinalizacaoHora(LocalDateTime sinalizacaoHora) {
        this.sinalizacaoHora = sinalizacaoHora;
    }

    public Boolean getExclusividade_cliente() {
        return exclusividade_cliente;
    }

    public void setExclusividade_cliente(Boolean exclusividade_cliente) {
        this.exclusividade_cliente = exclusividade_cliente;
    }

    public Parking fromDataSnapshot(DataSnapshot dsnpt) {
        return new Parking(
                dsnpt.child("nome").getValue(String.class),
                dsnpt.child("data_criacao").getValue(String.class),
                dsnpt.child("tipo").getValue(Integer.class),
                dsnpt.child("latitude").getValue(Double.class),
                dsnpt.child("longitude").getValue(Double.class),
                dsnpt.child("taxa").getValue(Double.class),
                dsnpt.child("sinalizacaoAutor").getValue(String.class),
                fromStringToLocalDateTime(dsnpt.child("sinalizacaoHora").getValue(String.class)),
                dsnpt.child("exclusividade_cliente").getValue(Boolean.class));
    }

    public LocalDateTime fromStringToLocalDateTime(String dateAsString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateAsString, formatter);
        return localDateTime;
    }
}
