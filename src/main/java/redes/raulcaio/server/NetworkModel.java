package redes.raulcaio.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = "maquinas", allowSetters = true)
public class NetworkModel {
    private int id;
    private int maquinas;
    private String endereco;


    public NetworkModel() {
    }

    public NetworkModel(int id, int numMachines, String networkAddress, int subnetMask) {
        this.id = id;
        this.maquinas = numMachines;
        this.endereco = networkAddress;
    }

    public int getId() {
        return id;
    }

    public int getMaquinas() {
        return maquinas;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMaquinas(int maquinas) {
        this.maquinas = maquinas;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

}
