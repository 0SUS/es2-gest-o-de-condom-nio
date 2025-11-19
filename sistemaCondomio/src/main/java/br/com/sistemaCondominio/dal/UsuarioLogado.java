/*
 * Classe utilitária para gerenciar o usuário logado no sistema
 */
package br.com.sistemaCondominio.dal;

/**
 * Classe singleton para armazenar informações do usuário logado
 * @author sistema
 */
public class UsuarioLogado {
    
    private static UsuarioLogado instance;
    private Integer id;
    private String username;
    private String perfil;
    
    private UsuarioLogado() {
        // Construtor privado para garantir singleton
    }
    
    /**
     * Retorna a instância única do UsuarioLogado
     * @return instância do UsuarioLogado
     */
    public static UsuarioLogado getInstance() {
        if (instance == null) {
            instance = new UsuarioLogado();
        }
        return instance;
    }
    
    /**
     * Define os dados do usuário logado
     * @param id ID do usuário
     * @param username Nome de usuário
     * @param perfil Perfil do usuário (Administrador, Morador, etc)
     */
    public void setUsuario(Integer id, String username, String perfil) {
        this.id = id;
        this.username = username;
        this.perfil = perfil;
    }
    
    /**
     * Limpa os dados do usuário logado (logout)
     */
    public void limpar() {
        this.id = null;
        this.username = null;
        this.perfil = null;
    }
    
    /**
     * Verifica se há um usuário logado
     * @return true se houver usuário logado, false caso contrário
     */
    public boolean isLogado() {
        return id != null;
    }
    
    // Getters
    public Integer getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPerfil() {
        return perfil;
    }
}

