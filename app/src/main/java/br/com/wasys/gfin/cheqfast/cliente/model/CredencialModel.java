package br.com.wasys.gfin.cheqfast.cliente.model;

/**
 * Created by pascke on 09/10/16.
 */
public class CredencialModel {

    public String login;
    public String senha;

    public CredencialModel(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }
}
