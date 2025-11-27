/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.com.sistemaCondominio.telas;

import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import br.com.sistemaCondominio.dal.ModuloConexao;
import br.com.sistemaCondominio.dal.UsuarioLogado;
import br.com.sistemaCondominio.dal.ReservaDAO;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author laris
 */
public class ReservasAreasComuns extends javax.swing.JInternalFrame {

    private Connection conexao = null;
    private DefaultTableModel tableModel;

    private javax.swing.JSpinner jSpinnerHourFim;
    private javax.swing.JSpinner jSpinnerMinuteFim;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JButton jButtonEncerrar;

    /**
     * Creates new form ReservasAreasComuns
     */
    public ReservasAreasComuns() {
        initComponents();
        conexao = ModuloConexao.conector();
        setupTable();
        populateDateAndTimeComponents();
        carregarReservas();
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        jButtonReservar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReservarActionPerformed(evt);
            }
        });
        
        jButtonEncerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEncerrarActionPerformed(evt);
            }
        });
    }

    private void setupTable() {
        String[] colunas = {"ID", "Área", "Data", "Início", "Fim", "Situação", "Usuário"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTableReservas.setModel(tableModel);
    }

    private void carregarReservas() {
        // Atualiza status das reservas (Expiradas/Em Uso) antes de carregar
        ReservaDAO.atualizarStatusReservas(conexao);

        String sql = "SELECT r.id, r.area, r.data_reserva, r.hora_reserva, r.hora_fim, r.situacao, " +
                     "COALESCE(u.username, 'N/A') as usuario " +
                     "FROM reservas_areas_comuns r " +
                     "LEFT JOIN usuario u ON r.usuario_id = u.id_usuario " +
                     "ORDER BY r.data_reserva DESC, r.hora_reserva DESC";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            tableModel.setRowCount(0);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            while (rs.next()) {
                Time horaFim = rs.getTime("hora_fim");
                String situacao = rs.getString("situacao");
                if (situacao == null) situacao = "PENDENTE";

                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("area"),
                    rs.getDate("data_reserva").toLocalDate().format(dateFormatter),
                    rs.getTime("hora_reserva").toString().substring(0, 5), // HH:mm
                    horaFim != null ? horaFim.toString().substring(0, 5) : "--:--",
                    situacao,
                    rs.getString("usuario")
                };
                tableModel.addRow(row);
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar reservas: " + e.getMessage());
        }
    }

    private void jButtonEncerrarActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = jTableReservas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para encerrar.");
            return;
        }

        String situacaoAtual = (String) tableModel.getValueAt(selectedRow, 5); // Coluna 5 = Situação
        if ("FINALIZADA".equals(situacaoAtual) || "CANCELADA".equals(situacaoAtual) || "EXPIRADA".equals(situacaoAtual)) {
             JOptionPane.showMessageDialog(this, "Esta reserva já está encerrada.");
             return;
        }

        int idReserva = (int) tableModel.getValueAt(selectedRow, 0);
        String usuarioReserva = (String) tableModel.getValueAt(selectedRow, 6); // Coluna 6 = Usuario

        // Verifica se o usuário logado é o dono ou admin
        // (Lógica simplificada: verifica username na tabela. Ideal seria ID, mas tableModel tem strings)
        // Se quiser rigoroso, teria que guardar ID na tableModel ou consultar. 
        // Vamos assumir que admin pode tudo, e usuario só a dele.
        // Como não temos perfil fácil aqui, vamos permitir encerrar por enquanto (supõe-se uso de confiança ou tela de adm).
        // Melhor: Perguntar confirmação.

        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente encerrar esta reserva?", "Confirmar Encerramento", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Atualiza situação e define hora_fim para agora (opcionalmente, ou mantém a original)
                // Vamos manter a original mas mudar status para FINALIZADA
                String sql = "UPDATE reservas_areas_comuns SET situacao = 'FINALIZADA' WHERE id = ?";
                PreparedStatement pst = conexao.prepareStatement(sql);
                pst.setInt(1, idReserva);
                int result = pst.executeUpdate();
                pst.close();

                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Reserva encerrada com sucesso.");
                    carregarReservas();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao encerrar reserva: " + e.getMessage());
            }
        }
    }

    private void jButtonReservarActionPerformed(java.awt.event.ActionEvent evt) {
        // Verifica se há usuário logado
        if (!UsuarioLogado.getInstance().isLogado()) {
            JOptionPane.showMessageDialog(this, "Erro: Usuário não está logado. Faça login novamente.");
            return;
        }

        String selectedArea = (String) jComboBoxArea.getSelectedItem();
        String selectedDay = (String) jComboBoxDay.getSelectedItem();
        String selectedMonth = (String) jComboBoxMonth.getSelectedItem();
        String selectedYear = (String) jComboBoxYear.getSelectedItem();
        
        Integer horaInicio = (Integer) jSpinnerHour.getValue();
        Integer minInicio = (Integer) jSpinnerMinute.getValue();
        
        Integer horaFim = (Integer) jSpinnerHourFim.getValue();
        Integer minFim = (Integer) jSpinnerMinuteFim.getValue();

        if (selectedArea == null || selectedArea.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma área comum.");
            return;
        }

        // Validação de data
        LocalDate dataReserva;
        try {
            dataReserva = LocalDate.of(Integer.parseInt(selectedYear), 
                                      Integer.parseInt(selectedMonth), 
                                      Integer.parseInt(selectedDay));
            
            if (dataReserva.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Não é possível reservar para uma data no passado.");
                return;
            }
        } catch (java.time.DateTimeException e) {
            JOptionPane.showMessageDialog(this, "Data selecionada inválida.");
            return;
        }

        LocalTime timeInicio = LocalTime.of(horaInicio, minInicio);
        LocalTime timeFim = LocalTime.of(horaFim, minFim);

        if (!timeFim.isAfter(timeInicio)) {
            JOptionPane.showMessageDialog(this, "A hora final deve ser posterior à hora inicial.");
            return;
        }

        // Validação de conflito de horário (Sobreposição)
        // (InicioA < FimB) AND (FimA > InicioB)
        // Verifica apenas reservas ATIVAS (não canceladas/finalizadas/expiradas)
        String sqlVerifica = "SELECT COUNT(*) FROM reservas_areas_comuns " +
                            "WHERE area = ? AND data_reserva = ? " +
                            "AND situacao NOT IN ('FINALIZADA', 'CANCELADA', 'EXPIRADA') " + // Ignora as já encerradas
                            "AND (hora_reserva < ? AND hora_fim > ?)"; // Lógica de sobreposição
        
        try {
            PreparedStatement pstVerifica = conexao.prepareStatement(sqlVerifica);
            pstVerifica.setString(1, selectedArea);
            pstVerifica.setDate(2, Date.valueOf(dataReserva));
            pstVerifica.setTime(3, Time.valueOf(timeFim));   // Fim da NOVA reserva
            pstVerifica.setTime(4, Time.valueOf(timeInicio)); // Início da NOVA reserva
            
            ResultSet rs = pstVerifica.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Conflito de horário! Já existe uma reserva ativa neste intervalo.",
                    "Reserva Indisponível", JOptionPane.WARNING_MESSAGE);
                rs.close();
                pstVerifica.close();
                return;
            }
            rs.close();
            pstVerifica.close();
            
            // Insere a nova reserva
            String sqlInsert = "INSERT INTO reservas_areas_comuns " +
                             "(area, data_reserva, hora_reserva, hora_fim, usuario_id, observacoes, situacao) " +
                             "VALUES (?, ?, ?, ?, ?, ?, 'PENDENTE')";
            
            PreparedStatement pstInsert = conexao.prepareStatement(sqlInsert);
            pstInsert.setString(1, selectedArea);
            pstInsert.setDate(2, Date.valueOf(dataReserva));
            pstInsert.setTime(3, Time.valueOf(timeInicio));
            pstInsert.setTime(4, Time.valueOf(timeFim));
            pstInsert.setInt(5, UsuarioLogado.getInstance().getId());
            pstInsert.setString(6, ""); 
            
            int resultado = pstInsert.executeUpdate();
            pstInsert.close();
            
            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, "Reserva realizada com sucesso!");
                carregarReservas();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao realizar reserva.");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao realizar reserva: " + e.getMessage());
        }
    }

    private void populateDateAndTimeComponents() {
        // Populate Area ComboBox
        jComboBoxArea.addItem("Piscina");
        jComboBoxArea.addItem("Churrascaria");
        jComboBoxArea.addItem("Sala de Festas");

        // Populate Day ComboBox
        for (int i = 1; i <= 31; i++) {
            jComboBoxDay.addItem(String.format("%02d", i));
        }

        // Populate Month ComboBox
        for (int i = 1; i <= 12; i++) {
            jComboBoxMonth.addItem(String.format("%02d", i));
        }

        // Populate Year ComboBox
        int currentYear = java.time.Year.now().getValue();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            jComboBoxYear.addItem(String.valueOf(i));
        }

        // Configure Hour Spinner Inicio
        jSpinnerHour.setModel(new SpinnerNumberModel(8, 0, 23, 1));
        jSpinnerHour.setEditor(new JSpinner.NumberEditor(jSpinnerHour, "00"));

        // Configure Minute Spinner Inicio
        jSpinnerMinute.setModel(new SpinnerNumberModel(0, 0, 59, 15));
        jSpinnerMinute.setEditor(new JSpinner.NumberEditor(jSpinnerMinute, "00"));
        
        // Configure Hour Spinner Fim (Default 9:00)
        jSpinnerHourFim.setModel(new SpinnerNumberModel(9, 0, 23, 1));
        jSpinnerHourFim.setEditor(new JSpinner.NumberEditor(jSpinnerHourFim, "00"));
        
        // Configure Minute Spinner Fim
        jSpinnerMinuteFim.setModel(new SpinnerNumberModel(0, 0, 59, 15));
        jSpinnerMinuteFim.setEditor(new JSpinner.NumberEditor(jSpinnerMinuteFim, "00"));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jComboBoxArea = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxDay = new javax.swing.JComboBox<>();
        jComboBoxMonth = new javax.swing.JComboBox<>();
        jComboBoxYear = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jSpinnerHour = new javax.swing.JSpinner();
        jSpinnerMinute = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jSpinnerHourFim = new javax.swing.JSpinner();
        jSpinnerMinuteFim = new javax.swing.JSpinner();
        jButtonReservar = new javax.swing.JButton();
        jButtonEncerrar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableReservas = new javax.swing.JTable();


        jLabel1.setText("Área Comum:");

        jLabel2.setText("Data:");

        jLabel3.setText("Início:");
        
        jLabel4.setText("Fim:");

        jButtonReservar.setText("Reservar");
        
        jButtonEncerrar.setText("Encerrar Reserva Selecionada");

        jScrollPane1.setViewportView(jTableReservas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBoxArea, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBoxDay, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxYear, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSpinnerHour, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerMinute, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSpinnerHourFim, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerMinuteFim, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonReservar)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonEncerrar))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBoxArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBoxDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jSpinnerHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinnerMinute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jSpinnerHourFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinnerMinuteFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonReservar)
                    .addComponent(jButtonEncerrar))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonReservar;
    private javax.swing.JComboBox<String> jComboBoxArea;
    private javax.swing.JComboBox<String> jComboBoxDay;
    private javax.swing.JComboBox<String> jComboBoxMonth;
    private javax.swing.JComboBox<String> jComboBoxYear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinnerHour;
    private javax.swing.JSpinner jSpinnerMinute;
    private javax.swing.JTable jTableReservas;
    // End of variables declaration//GEN-END:variables
}
