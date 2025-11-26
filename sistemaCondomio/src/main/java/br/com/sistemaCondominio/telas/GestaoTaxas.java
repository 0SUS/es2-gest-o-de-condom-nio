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
public class GestaoTaxas extends javax.swing.JInternalFrame {

    private Connection conexao = null;
    private DefaultTableModel tableModel;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;
    private boolean modoEdicao = false;
    private Integer idTaxaEditando = null;

    /**
     * Creates new form GestaoTaxas
     */
    public GestaoTaxas() {
        initComponents();
        conexao = ModuloConexao.conector();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        setupTable();
        populateStatusCombo();
        atualizarTabela();
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        configurarPlaceholders();
    }

    private void setupTable() {
        String[] colunas = {"ID", "Unidade", "Valor", "Data Vencimento", "Status Pagamento", "Data Registro"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTableTaxas.setModel(tableModel);
        jTableTaxas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }

    private void populateStatusCombo() {
        jComboBoxStatus.removeAllItems();
        jComboBoxStatus.addItem("Pendente");
        jComboBoxStatus.addItem("Pago");
    }

    private void atualizarTabela() {
        String sql = "SELECT id, unidade, valor, data_vencimento, status_pagamento, data_registro " +
                     "FROM taxas " +
                     "ORDER BY data_vencimento DESC, data_registro DESC";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            tableModel.setRowCount(0);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("unidade"),
                    currencyFormat.format(rs.getDouble("valor")),
                    dateFormat.format(rs.getDate("data_vencimento")),
                    rs.getString("status_pagamento"),
                    dateFormat.format(rs.getTimestamp("data_registro"))
                };
                tableModel.addRow(row);
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar taxas: " + e.getMessage());
        }
    }

    private void configurarPlaceholders() {
        // Remove placeholders - vamos usar labels e campos normais
        campoUnidade.setText("");
        campoValor.setText("");
        campoData.setText("");
    }

    private void limparFormulario() {
        campoUnidade.setText("");
        campoValor.setText("");
        campoData.setText("");
        jComboBoxStatus.setSelectedIndex(0);
        jTableTaxas.clearSelection();
        modoEdicao = false;
        idTaxaEditando = null;
        btnConfirmar.setText("Registrar Nova Taxa");
    }

    private void preencherFormularioComSelecao() {
        int selectedRow = jTableTaxas.getSelectedRow();
        
        if (selectedRow == -1) {
            return;
        }

        idTaxaEditando = (Integer) tableModel.getValueAt(selectedRow, 0);
        campoUnidade.setText(tableModel.getValueAt(selectedRow, 1).toString());
        
        // Remove formatação de moeda do valor
        String valorFormatado = tableModel.getValueAt(selectedRow, 2).toString();
        String valorSemFormatacao = valorFormatado.replace("R$", "").replace(".", "").replace(",", ".").trim();
        campoValor.setText(valorSemFormatacao);
        
        campoData.setText(tableModel.getValueAt(selectedRow, 3).toString());
        
        String status = tableModel.getValueAt(selectedRow, 4).toString();
        if (status.equals("Pendente")) {
            jComboBoxStatus.setSelectedIndex(0);
        } else {
            jComboBoxStatus.setSelectedIndex(1);
        }
        
        modoEdicao = true;
        btnConfirmar.setText("Atualizar Taxa");
    }

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {
        // Verifica se há usuário logado
        if (!UsuarioLogado.getInstance().isLogado()) {
            JOptionPane.showMessageDialog(this, "Erro: Usuário não está logado. Faça login novamente.");
            return;
        }

        String unidade = campoUnidade.getText().trim();
        String valorText = campoValor.getText().trim();
        String dataText = campoData.getText().trim();
        String status = (String) jComboBoxStatus.getSelectedItem();

        // Validações
        if (unidade.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Unidade.");
            campoUnidade.requestFocus();
            return;
        }

        if (valorText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Valor.");
            campoValor.requestFocus();
            return;
        }

        double valor = 0.0;
        try {
            valorText = valorText.replace(",", ".");
            valor = Double.parseDouble(valorText);
            if (valor <= 0) {
                JOptionPane.showMessageDialog(this, "O valor deve ser maior que zero.");
                campoValor.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido. Use apenas números.");
            campoValor.requestFocus();
            return;
        }

        if (dataText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Data de Vencimento.");
            campoData.requestFocus();
            return;
        }

        // Validação de data
        Date dataVencimento;
        try {
            // Tenta parsear no formato dd/MM/yyyy
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            java.util.Date dataUtil = sdf.parse(dataText);
            dataVencimento = new Date(dataUtil.getTime());
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(this, 
                "Data inválida. Use o formato DD/MM/AAAA (ex: 25/12/2024).",
                "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            campoData.requestFocus();
            return;
        }

        if (status == null || status.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione o status de pagamento.");
            return;
        }

        try {
            if (modoEdicao && idTaxaEditando != null) {
                // Atualizar taxa existente
                String sql = "UPDATE taxas SET unidade = ?, valor = ?, data_vencimento = ?, status_pagamento = ? WHERE id = ?";
                PreparedStatement pst = conexao.prepareStatement(sql);
                pst.setString(1, unidade);
                pst.setDouble(2, valor);
                pst.setDate(3, dataVencimento);
                pst.setString(4, status);
                pst.setInt(5, idTaxaEditando);
                
                int resultado = pst.executeUpdate();
                pst.close();
                
                if (resultado > 0) {
                    JOptionPane.showMessageDialog(this, "Taxa atualizada com sucesso!");
                    atualizarTabela();
                    limparFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar taxa. Taxa não encontrada.");
                }
            } else {
                // Inserir nova taxa
                String sql = "INSERT INTO taxas (unidade, valor, data_vencimento, status_pagamento, usuario_registro_id) " +
                             "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pst = conexao.prepareStatement(sql);
                pst.setString(1, unidade);
                pst.setDouble(2, valor);
                pst.setDate(3, dataVencimento);
                pst.setString(4, status);
                pst.setInt(5, UsuarioLogado.getInstance().getId());
                
                int resultado = pst.executeUpdate();
                pst.close();
                
                if (resultado > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Taxa registrada com sucesso!\n" +
                        "Unidade: " + unidade + "\n" +
                        "Valor: " + currencyFormat.format(valor) + "\n" +
                        "Vencimento: " + dataText + "\n" +
                        "Status: " + status,
                        "Registro Confirmado", JOptionPane.INFORMATION_MESSAGE);
                    atualizarTabela();
                    limparFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao registrar taxa. Tente novamente.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao processar taxa: " + e.getMessage());
        }
    }

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = jTableTaxas.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma taxa na tabela para editar.");
            return;
        }
        
        preencherFormularioComSelecao();
    }

    private void btnMarcarComoPagaActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = jTableTaxas.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma taxa na tabela.");
            return;
        }

        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String statusAtual = tableModel.getValueAt(selectedRow, 4).toString();
        
        if (statusAtual.equals("Pago")) {
            JOptionPane.showMessageDialog(this, "Esta taxa já está marcada como paga.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this, 
            "Deseja marcar esta taxa como paga?",
            "Confirmar Pagamento", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                String sql = "UPDATE taxas SET status_pagamento = 'Pago' WHERE id = ?";
                PreparedStatement pst = conexao.prepareStatement(sql);
                pst.setInt(1, id);
                
                int resultado = pst.executeUpdate();
                pst.close();
                
                if (resultado > 0) {
                    JOptionPane.showMessageDialog(this, "Taxa marcada como paga com sucesso!");
                    atualizarTabela();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar status. Taxa não encontrada.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar status: " + e.getMessage());
            }
        }
    }

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        limparFormulario();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableTaxas = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        campoUnidade = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        campoValor = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        campoData = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxStatus = new javax.swing.JComboBox<>();
        btnConfirmar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnMarcarComoPaga = new javax.swing.JButton();

        setTitle("Gestão de Taxas de Condomínio");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Gestão de Taxas de Condomínio");

        jTableTaxas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTableTaxas);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Lista de Taxas Cadastradas");

        jLabel3.setText("Unidade:");

        jLabel4.setText("Valor (R$):");

        jLabel5.setText("Data de Vencimento (DD/MM/AAAA):");

        jLabel6.setText("Status de Pagamento:");

        btnConfirmar.setText("Registrar Nova Taxa");
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnEditar.setText("Editar Taxa Selecionada");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnMarcarComoPaga.setText("Marcar como Paga");
        btnMarcarComoPaga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarcarComoPagaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(campoUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(campoValor, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(campoData, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnConfirmar)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar)
                        .addGap(18, 18, 18)
                        .addComponent(btnEditar)
                        .addGap(18, 18, 18)
                        .addComponent(btnMarcarComoPaga)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(campoUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(campoValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(campoData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBoxStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirmar)
                    .addComponent(btnCancelar)
                    .addComponent(btnEditar)
                    .addComponent(btnMarcarComoPaga))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnMarcarComoPaga;
    private javax.swing.JTextField campoData;
    private javax.swing.JComboBox<String> jComboBoxStatus;
    private javax.swing.JTextField campoUnidade;
    private javax.swing.JTextField campoValor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableTaxas;
    // End of variables declaration//GEN-END:variables
}
