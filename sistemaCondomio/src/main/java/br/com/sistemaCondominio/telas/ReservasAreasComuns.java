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
    }

    private void setupTable() {
        String[] colunas = {"ID", "Área", "Data", "Hora", "Usuário", "Observações"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTableReservas.setModel(tableModel);
    }

    private void carregarReservas() {
        String sql = "SELECT r.id, r.area, r.data_reserva, r.hora_reserva, " +
                     "COALESCE(u.username, 'N/A') as usuario, r.observacoes " +
                     "FROM reservas_areas_comuns r " +
                     "LEFT JOIN usuario u ON r.usuario_id = u.id_usuario " +
                     "ORDER BY r.data_reserva DESC, r.hora_reserva DESC";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            tableModel.setRowCount(0);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("area"),
                    rs.getDate("data_reserva").toLocalDate().format(dateFormatter),
                    rs.getTime("hora_reserva").toString().substring(0, 5), // HH:mm
                    rs.getString("usuario"),
                    rs.getString("observacoes") != null ? rs.getString("observacoes") : ""
                };
                tableModel.addRow(row);
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar reservas: " + e.getMessage());
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
        Integer selectedHour = (Integer) jSpinnerHour.getValue();
        Integer selectedMinute = (Integer) jSpinnerMinute.getValue();

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
            
            // Verifica se a data não é no passado
            if (dataReserva.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Não é possível reservar para uma data no passado.");
                return;
            }
        } catch (java.time.DateTimeException e) {
            JOptionPane.showMessageDialog(this, "Data selecionada inválida. Por favor, verifique o dia, mês e ano.");
            return;
        }

        // Validação de hora
        LocalTime horaReserva = LocalTime.of(selectedHour, selectedMinute);
        
        // Verifica se já existe reserva para o mesmo horário e área
        String sqlVerifica = "SELECT COUNT(*) FROM reservas_areas_comuns " +
                            "WHERE area = ? AND data_reserva = ? AND hora_reserva = ?";
        
        try {
            PreparedStatement pstVerifica = conexao.prepareStatement(sqlVerifica);
            pstVerifica.setString(1, selectedArea);
            pstVerifica.setDate(2, Date.valueOf(dataReserva));
            pstVerifica.setTime(3, Time.valueOf(horaReserva));
            
            ResultSet rs = pstVerifica.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Já existe uma reserva para esta área no horário selecionado. Por favor, escolha outro horário.",
                    "Reserva Indisponível", JOptionPane.WARNING_MESSAGE);
                rs.close();
                pstVerifica.close();
                return;
            }
            rs.close();
            pstVerifica.close();
            
            // Insere a nova reserva
            String sqlInsert = "INSERT INTO reservas_areas_comuns " +
                             "(area, data_reserva, hora_reserva, usuario_id, observacoes) " +
                             "VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement pstInsert = conexao.prepareStatement(sqlInsert);
            pstInsert.setString(1, selectedArea);
            pstInsert.setDate(2, Date.valueOf(dataReserva));
            pstInsert.setTime(3, Time.valueOf(horaReserva));
            pstInsert.setInt(4, UsuarioLogado.getInstance().getId());
            pstInsert.setString(5, ""); // Observações vazias por padrão
            
            int resultado = pstInsert.executeUpdate();
            pstInsert.close();
            
            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Reserva realizada com sucesso!\n" +
                    "Área: " + selectedArea + "\n" +
                    "Data: " + String.format("%02d/%02d/%s", 
                        Integer.parseInt(selectedDay), 
                        Integer.parseInt(selectedMonth), 
                        selectedYear) + "\n" +
                    "Hora: " + String.format("%02d:%02d", selectedHour, selectedMinute),
                    "Reserva Confirmada", JOptionPane.INFORMATION_MESSAGE);
                
                // Recarrega a tabela
                carregarReservas();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao realizar reserva. Tente novamente.");
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

        // Populate Year ComboBox (e.g., current year +/- 5 years)
        int currentYear = java.time.Year.now().getValue();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            jComboBoxYear.addItem(String.valueOf(i));
        }

        // Configure Hour Spinner
        SpinnerModel hourModel = new SpinnerNumberModel(8, 0, 23, 1); // Default 8, min 0, max 23, step 1
        jSpinnerHour.setModel(hourModel);
        JSpinner.NumberEditor hourEditor = new JSpinner.NumberEditor(jSpinnerHour, "00");
        jSpinnerHour.setEditor(hourEditor);

        // Configure Minute Spinner
        SpinnerModel minuteModel = new SpinnerNumberModel(0, 0, 59, 15); // Default 0, min 0, max 59, step 15
        jSpinnerMinute.setModel(minuteModel);
        JSpinner.NumberEditor minuteEditor = new JSpinner.NumberEditor(jSpinnerMinute, "00");
        jSpinnerMinute.setEditor(minuteEditor);
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
        jButtonReservar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableReservas = new javax.swing.JTable();



        jLabel1.setText("Área Comum:");

        jLabel2.setText("Data:");

        jLabel3.setText("Hora:");

        jButtonReservar.setText("Reservar");

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
                        .addComponent(jSpinnerMinute, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonReservar)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(5, Short.MAX_VALUE))
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
                    .addComponent(jSpinnerMinute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButtonReservar)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
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
