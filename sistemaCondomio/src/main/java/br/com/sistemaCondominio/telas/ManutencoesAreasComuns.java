/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.sistemaCondominio.telas;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.sql.*;
import br.com.sistemaCondominio.dal.ModuloConexao;
import br.com.sistemaCondominio.dal.UsuarioLogado;
import java.util.Locale;

/**
 *
 * @author laris
 */
public class ManutencoesAreasComuns extends javax.swing.JInternalFrame {

    private Connection conexao = null;
    private DefaultTableModel tableModel;
    private NumberFormat currencyFormat;

    /**
     * Creates new form ManutencoesAreasComuns
     */
    public ManutencoesAreasComuns() {
        initComponents();
        conexao = ModuloConexao.conector();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        setupTable();
        populateComponents();
        atualizarTabela();
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
    }

    private void setupTable() {
        String[] colunas = {"ID", "Área", "Tipo Problema", "Descrição", "Custo Estimado", 
                           "Custo Final", "Status", "Data Criação"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTableManutencoes.setModel(tableModel);
        jTableManutencoes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }

    private void populateComponents() {
        // Popula combo de áreas
        jComboBoxArea.addItem("Piscina");
        jComboBoxArea.addItem("Churrascaria");
        jComboBoxArea.addItem("Sala de Festas");

        // Popula combo de tipos de problema
        jComboBoxTipoProblema.addItem("Elétrica");
        jComboBoxTipoProblema.addItem("Hidráulica");
        jComboBoxTipoProblema.addItem("Estrutural");
        jComboBoxTipoProblema.addItem("Pintura");
        jComboBoxTipoProblema.addItem("Limpeza");
        jComboBoxTipoProblema.addItem("Outros");

        // Popula combo de status
        jComboBoxStatus.addItem("Aberto");
        jComboBoxStatus.addItem("Em andamento");
        jComboBoxStatus.addItem("Concluído");
    }

    private void atualizarTabela() {
        String sql = "SELECT id, area, tipo_problema, descricao, custo_estimado, " +
                     "custo_final, status, data_criacao " +
                     "FROM manutencoes_areas_comuns " +
                     "ORDER BY data_criacao DESC";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            tableModel.setRowCount(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            
            while (rs.next()) {
                String descricao = rs.getString("descricao");
                String descricaoResumida = descricao.length() > 30 ? 
                    descricao.substring(0, 30) + "..." : descricao;
                
                double custoFinal = rs.getDouble("custo_final");
                
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("area"),
                    rs.getString("tipo_problema"),
                    descricaoResumida,
                    currencyFormat.format(rs.getDouble("custo_estimado")),
                    custoFinal > 0 ? currencyFormat.format(custoFinal) : "-",
                    rs.getString("status"),
                    dateFormat.format(rs.getTimestamp("data_criacao"))
                };
                tableModel.addRow(row);
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar manutenções: " + e.getMessage());
        }
    }

    private boolean areaEmManutencao(String area) {
        String sql = "SELECT COUNT(*) FROM manutencoes_areas_comuns " +
                     "WHERE area = ? AND status IN ('Aberto', 'Em andamento')";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            pst.setString(1, area);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                rs.close();
                pst.close();
                return true;
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar manutenções: " + e.getMessage());
        }
        
        return false;
    }

    private void novaManutencaoActionPerformed(java.awt.event.ActionEvent evt) {
        // Verifica se há usuário logado
        if (!UsuarioLogado.getInstance().isLogado()) {
            JOptionPane.showMessageDialog(this, "Erro: Usuário não está logado. Faça login novamente.");
            return;
        }

        String area = (String) jComboBoxArea.getSelectedItem();
        String tipoProblema = (String) jComboBoxTipoProblema.getSelectedItem();
        String descricao = jTextAreaDescricao.getText().trim();
        
        // Validação
        if (area == null || area.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma área.");
            return;
        }
        
        if (tipoProblema == null || tipoProblema.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione o tipo de problema.");
            return;
        }
        
        if (descricao == null || descricao.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha a descrição do problema.");
            return;
        }

        // Verifica se área já está em manutenção
        if (areaEmManutencao(area)) {
            JOptionPane.showMessageDialog(this, 
                "Atenção! A área " + area + " já possui uma manutenção em aberto ou em andamento.",
                "Área em Manutenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtém custo estimado
        double custoEstimado = 0.0;
        try {
            String custoText = jTextFieldCustoEstimado.getText().trim().replace(",", ".");
            if (!custoText.isEmpty()) {
                custoEstimado = Double.parseDouble(custoText);
                if (custoEstimado < 0) {
                    JOptionPane.showMessageDialog(this, "Custo estimado não pode ser negativo.");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Custo estimado inválido. Use apenas números.");
            return;
        }

        // Insere nova manutenção no banco
        String sql = "INSERT INTO manutencoes_areas_comuns " +
                     "(area, tipo_problema, descricao, custo_estimado, status, usuario_criacao_id) " +
                     "VALUES (?, ?, ?, ?, 'Aberto', ?)";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            pst.setString(1, area);
            pst.setString(2, tipoProblema);
            pst.setString(3, descricao);
            pst.setDouble(4, custoEstimado);
            pst.setInt(5, UsuarioLogado.getInstance().getId());
            
            int resultado = pst.executeUpdate();
            pst.close();
            
            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, "Manutenção cadastrada com sucesso!");
                atualizarTabela();
                limparFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar manutenção. Tente novamente.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar manutenção: " + e.getMessage());
        }
    }

    private void atualizarStatusActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = jTableManutencoes.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma manutenção na tabela.");
            return;
        }

        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String novoStatus = (String) jComboBoxStatus.getSelectedItem();
        double novoCustoFinal = 0.0;
        
        try {
            String custoText = jTextFieldCustoFinal.getText().trim().replace(",", ".");
            if (!custoText.isEmpty()) {
                novoCustoFinal = Double.parseDouble(custoText);
                if (novoCustoFinal < 0) {
                    JOptionPane.showMessageDialog(this, "Custo final não pode ser negativo.");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Custo final inválido. Use apenas números.");
            return;
        }

        // Atualiza no banco de dados
        String sql = "UPDATE manutencoes_areas_comuns " +
                     "SET status = ?, custo_final = ? " +
                     "WHERE id = ?";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            pst.setString(1, novoStatus);
            pst.setDouble(2, novoCustoFinal);
            pst.setInt(3, id);
            
            int resultado = pst.executeUpdate();
            pst.close();
            
            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, "Status atualizado com sucesso!");
                atualizarTabela();
                limparFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar status. Manutenção não encontrada.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar status: " + e.getMessage());
        }
    }

    private void limparFormulario() {
        jTextAreaDescricao.setText("");
        jTextFieldCustoEstimado.setText("");
        jTextFieldCustoFinal.setText("");
        jComboBoxArea.setSelectedIndex(0);
        jComboBoxTipoProblema.setSelectedIndex(0);
        jComboBoxStatus.setSelectedIndex(0);
        jTableManutencoes.clearSelection();
    }

    private void gerarRelatorioActionPerformed(java.awt.event.ActionEvent evt) {
        String sql = "SELECT id, area, tipo_problema, descricao, custo_estimado, " +
                     "custo_final, status, data_criacao, data_atualizacao " +
                     "FROM manutencoes_areas_comuns " +
                     "ORDER BY data_criacao DESC";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(this, "Não há manutenções cadastradas para gerar relatório.");
                rs.close();
                pst.close();
                return;
            }

            StringBuilder relatorio = new StringBuilder();
            relatorio.append("=== RELATÓRIO DE MANUTENÇÕES ===\n\n");
            
            int totalAberto = 0, totalEmAndamento = 0, totalConcluido = 0;
            double totalCustoEstimado = 0.0, totalCustoFinal = 0.0;
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            while (rs.next()) {
                relatorio.append(String.format("ID: %d\n", rs.getInt("id")));
                relatorio.append(String.format("Área: %s\n", rs.getString("area")));
                relatorio.append(String.format("Tipo: %s\n", rs.getString("tipo_problema")));
                relatorio.append(String.format("Descrição: %s\n", rs.getString("descricao")));
                relatorio.append(String.format("Custo Estimado: %s\n", 
                    currencyFormat.format(rs.getDouble("custo_estimado"))));
                
                double custoFinal = rs.getDouble("custo_final");
                relatorio.append(String.format("Custo Final: %s\n", 
                    custoFinal > 0 ? currencyFormat.format(custoFinal) : "Não informado"));
                
                String status = rs.getString("status");
                relatorio.append(String.format("Status: %s\n", status));
                relatorio.append(String.format("Data Criação: %s\n", 
                    dateFormat.format(rs.getTimestamp("data_criacao"))));
                relatorio.append(String.format("Última Atualização: %s\n", 
                    dateFormat.format(rs.getTimestamp("data_atualizacao"))));
                relatorio.append("----------------------------------------\n");
                
                if (status.equals("Aberto")) totalAberto++;
                else if (status.equals("Em andamento")) totalEmAndamento++;
                else if (status.equals("Concluído")) totalConcluido++;
                
                totalCustoEstimado += rs.getDouble("custo_estimado");
                totalCustoFinal += custoFinal;
            }
            
            rs.close();
            pst.close();
            
            relatorio.append("\n=== RESUMO ===\n");
            relatorio.append(String.format("Total de Manutenções: %d\n", 
                totalAberto + totalEmAndamento + totalConcluido));
            relatorio.append(String.format("Abertas: %d\n", totalAberto));
            relatorio.append(String.format("Em Andamento: %d\n", totalEmAndamento));
            relatorio.append(String.format("Concluídas: %d\n", totalConcluido));
            relatorio.append(String.format("Total Custo Estimado: %s\n", 
                currencyFormat.format(totalCustoEstimado)));
            relatorio.append(String.format("Total Custo Final: %s\n", 
                currencyFormat.format(totalCustoFinal)));

            JOptionPane.showMessageDialog(this, relatorio.toString(), "Relatório de Manutenções", 
                                         JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + e.getMessage());
        }
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
        jComboBoxTipoProblema = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaDescricao = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldCustoEstimado = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldCustoFinal = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxStatus = new javax.swing.JComboBox<>();
        jButtonNovaManutencao = new javax.swing.JButton();
        jButtonAtualizarStatus = new javax.swing.JButton();
        jButtonGerarRelatorio = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableManutencoes = new javax.swing.JTable();

        setTitle("Gerenciamento de Manutenções");

        jLabel1.setText("Área:");

        jLabel2.setText("Tipo de Problema:");

        jLabel3.setText("Descrição:");

        jTextAreaDescricao.setColumns(20);
        jTextAreaDescricao.setRows(5);
        jScrollPane1.setViewportView(jTextAreaDescricao);

        jLabel4.setText("Custo Estimado (R$):");

        jLabel5.setText("Custo Final (R$):");

        jLabel6.setText("Status:");

        jButtonNovaManutencao.setText("Nova Manutenção");
        jButtonNovaManutencao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                novaManutencaoActionPerformed(evt);
            }
        });

        jButtonAtualizarStatus.setText("Atualizar Status");
        jButtonAtualizarStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atualizarStatusActionPerformed(evt);
            }
        });

        jButtonGerarRelatorio.setText("Gerar Relatório");
        jButtonGerarRelatorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gerarRelatorioActionPerformed(evt);
            }
        });

        jTableManutencoes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTableManutencoes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBoxArea, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBoxTipoProblema, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldCustoEstimado, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldCustoFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBoxStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonNovaManutencao)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonAtualizarStatus)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonGerarRelatorio)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBoxArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBoxTipoProblema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldCustoEstimado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldCustoFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBoxStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonNovaManutencao)
                    .addComponent(jButtonAtualizarStatus)
                    .addComponent(jButtonGerarRelatorio))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAtualizarStatus;
    private javax.swing.JButton jButtonGerarRelatorio;
    private javax.swing.JButton jButtonNovaManutencao;
    private javax.swing.JComboBox<String> jComboBoxArea;
    private javax.swing.JComboBox<String> jComboBoxStatus;
    private javax.swing.JComboBox<String> jComboBoxTipoProblema;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableManutencoes;
    private javax.swing.JTextArea jTextAreaDescricao;
    private javax.swing.JTextField jTextFieldCustoEstimado;
    private javax.swing.JTextField jTextFieldCustoFinal;
    // End of variables declaration//GEN-END:variables
}

