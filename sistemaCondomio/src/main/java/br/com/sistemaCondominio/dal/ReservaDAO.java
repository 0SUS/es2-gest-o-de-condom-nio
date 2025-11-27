package br.com.sistemaCondominio.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReservaDAO {
    
    public static void atualizarStatusReservas(Connection conexao) {
        if (conexao == null) return;
        
        try {
            // 1. Marcar como EM_USO as que já começaram (PENDENTE -> EM_USO)
            // Considera apenas reservas que começaram e ainda não terminaram
            String sqlEmUso = "UPDATE reservas_areas_comuns " +
                            "SET situacao = 'EM_USO' " +
                            "WHERE situacao = 'PENDENTE' " +
                            "AND data_reserva = CURRENT_DATE " +
                            "AND hora_reserva <= CURRENT_TIME " +
                            "AND hora_fim > CURRENT_TIME";
            
            try (PreparedStatement pst = conexao.prepareStatement(sqlEmUso)) {
                pst.executeUpdate();
            }
            
            // 2. Marcar como EXPIRADA as que já acabaram
            String sqlExpirada = "UPDATE reservas_areas_comuns " +
                               "SET situacao = 'EXPIRADA' " +
                               "WHERE situacao IN ('PENDENTE', 'EM_USO') " +
                               "AND (data_reserva < CURRENT_DATE " +
                               "OR (data_reserva = CURRENT_DATE AND hora_fim <= CURRENT_TIME))";
                               
            try (PreparedStatement pst = conexao.prepareStatement(sqlExpirada)) {
                pst.executeUpdate();
            }
            
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar status das reservas: " + e.getMessage());
        }
    }
}
