/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.sistemaCondominio.telas;

import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import br.com.sistemaCondominio.dal.ModuloConexao;

/**
 *
 * @author laris
 */
public class EditarExcluirMoradores extends javax.swing.JInternalFrame {

    private Connection conexao = null;
    private DefaultTableModel tableModel;
    private Integer idUsuarioSelecionado = null;

    /**
     * Creates new form EditarExcluirMoradores
     */
    public EditarExcluirMoradores() {
        initComponents();
        conexao = ModuloConexao.conector();
        setupTable();
        carregarMoradores();
        setupListeners();
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
    }

    private void setupTable() {
        String[] colunas = {"ID", "Nome", "CPF", "Telefone", "Rua", "Número", "Username", "Perfil"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblMoradores.setModel(tableModel);
        tblMoradores.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupListeners() {
        // Listener para quando selecionar uma linha na tabela
        tblMoradores.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    preencherCamposComSelecao();
                }
            }
        });

        // Listener para pesquisa (buscar ao pressionar Enter)
        txtPesquisaMoradores.addActionListener(e -> buscarMoradores());
        
        // Listener para buscar quando perder o foco do campo de pesquisa
        txtPesquisaMoradores.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                buscarMoradores();
            }
        });
        
        // Listener para o botão de busca (jLabel7)
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buscarMoradores();
            }
        });
    }

    private void carregarMoradores() {
        String sql = "SELECT id_usuario, nome, cpf, telefone, rua, numero, username, perfil " +
                     "FROM usuario " +
                     "ORDER BY nome";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            tableModel.setRowCount(0);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_usuario"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("telefone"),
                    rs.getString("rua"),
                    rs.getString("numero"),
                    rs.getString("username"),
                    rs.getString("perfil")
                };
                tableModel.addRow(row);
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar moradores: " + e.getMessage());
        }
    }

    private void buscarMoradores() {
        String pesquisa = txtPesquisaMoradores.getText().trim();
        
        String sql;
        if (pesquisa.isEmpty()) {
            sql = "SELECT id_usuario, nome, cpf, telefone, rua, numero, username, perfil " +
                  "FROM usuario " +
                  "ORDER BY nome";
        } else {
            sql = "SELECT id_usuario, nome, cpf, telefone, rua, numero, username, perfil " +
                  "FROM usuario " +
                  "WHERE UPPER(nome) LIKE UPPER(?) OR cpf LIKE ? OR UPPER(username) LIKE UPPER(?) " +
                  "ORDER BY nome";
        }
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            if (!pesquisa.isEmpty()) {
                String likePattern = "%" + pesquisa + "%";
                pst.setString(1, likePattern);
                pst.setString(2, likePattern);
                pst.setString(3, likePattern);
            }
            
            ResultSet rs = pst.executeQuery();
            
            tableModel.setRowCount(0);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_usuario"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("telefone"),
                    rs.getString("rua"),
                    rs.getString("numero"),
                    rs.getString("username"),
                    rs.getString("perfil")
                };
                tableModel.addRow(row);
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar moradores: " + e.getMessage());
        }
    }

    private void preencherCamposComSelecao() {
        int selectedRow = tblMoradores.getSelectedRow();
        
        if (selectedRow == -1) {
            limparCampos();
            return;
        }

        idUsuarioSelecionado = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        // Busca os dados completos do usuário
        String sql = "SELECT nome, cpf, telefone, rua, numero, veiculo, placa, username, senha, perfil " +
                     "FROM usuario WHERE id_usuario = ?";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            pst.setInt(1, idUsuarioSelecionado);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                txtNomeMorador.setText(rs.getString("nome"));
                txtCpfMorador.setText(rs.getString("cpf"));
                txtTelefoneMorador.setText(rs.getString("telefone"));
                txtRua.setText(rs.getString("rua"));
                txtNumero.setText(rs.getString("numero"));
                
                String veiculo = rs.getString("veiculo");
                if (veiculo != null && !veiculo.trim().isEmpty()) {
                    cbbVeiculo.setSelectedItem(veiculo);
                } else {
                    cbbVeiculo.setSelectedIndex(0);
                }
                
                String placa = rs.getString("placa");
                txtPlacaVeiculo.setText(placa != null ? placa : "");
                
                txtLogin.setText(rs.getString("username"));
                txtSenha.setText(rs.getString("senha"));
                
                String perfil = rs.getString("perfil");
                if (perfil != null) {
                    cbbPerfil.setSelectedItem(perfil);
                } else {
                    cbbPerfil.setSelectedIndex(0);
                }
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do morador: " + e.getMessage());
        }
    }

    private void limparCampos() {
        idUsuarioSelecionado = null;
        txtNomeMorador.setText("");
        txtCpfMorador.setText("");
        txtTelefoneMorador.setText("");
        txtRua.setText("");
        txtNumero.setText("");
        txtPlacaVeiculo.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        cbbVeiculo.setSelectedIndex(0);
        cbbPerfil.setSelectedIndex(0);
        tblMoradores.clearSelection();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtPesquisaMoradores = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMoradores = new javax.swing.JTable();
        btnSalvaResidencia = new javax.swing.JButton();
        btnSalvaResidencia1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtNomeMorador = new javax.swing.JTextField();
        txtCpfMorador = new javax.swing.JTextField();
        txtTelefoneMorador = new javax.swing.JTextField();
        txtPlacaVeiculo = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtRua = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cbbVeiculo = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cbbPerfil = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        txtLogin = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtSenha = new javax.swing.JPasswordField();
        jLabel13 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Editar/Excluir moradores");
        setPreferredSize(new java.awt.Dimension(910, 540));

        jLabel2.setText("*Campo obrigatório");

        tblMoradores.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblMoradores);

        btnSalvaResidencia.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnSalvaResidencia.setText("Deletar");
        btnSalvaResidencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvaResidenciaActionPerformed(evt);
            }
        });

        btnSalvaResidencia1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnSalvaResidencia1.setText("Salvar Alteração");
        btnSalvaResidencia1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvaResidencia1ActionPerformed(evt);
            }
        });

        jLabel5.setText("Placa:");

        jLabel6.setText("*Rua:");

        jLabel8.setText("*Nome");

        jLabel9.setText("*CPF:");

        jLabel3.setText("Tipo de veículo:");

        jLabel10.setText("*Numero");

        jLabel4.setText("*Telefone:");

        cbbVeiculo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "  ", "Carro", "Moto", "Caminhão", "Outro" }));
        cbbVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbbVeiculoActionPerformed(evt);
            }
        });

        jLabel7.setText("🔍 Buscar");

        cbbPerfil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Administrador", "Morador" }));

        jLabel11.setText("*Login:");

        jLabel12.setText("*Senha:");

        txtSenha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSenhaActionPerformed(evt);
            }
        });

        jLabel13.setText("*Perfil:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbbVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(338, 338, 338)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(txtPlacaVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtPesquisaMoradores, javax.swing.GroupLayout.PREFERRED_SIZE, 553, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addGap(68, 68, 68)
                                .addComponent(jLabel2))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 794, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(57, 57, 57)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtNomeMorador)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(282, 282, 282)
                                        .addComponent(jLabel4)
                                        .addGap(33, 33, 33)
                                        .addComponent(txtTelefoneMorador, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel9))
                                .addGap(67, 67, 67)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCpfMorador, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtRua, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(24, 24, 24)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel11))
                                .addGap(55, 55, 55)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(33, 33, 33)
                                        .addComponent(jLabel12)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(cbbPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 54, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSalvaResidencia1)
                        .addGap(93, 93, 93)
                        .addComponent(btnSalvaResidencia)
                        .addGap(184, 184, 184))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPesquisaMoradores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(23, 23, 23)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtNomeMorador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(txtTelefoneMorador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(7, 7, 7))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtCpfMorador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtRua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbbVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(jLabel5)
                    .addComponent(txtPlacaVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel11)
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(cbbPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvaResidencia, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalvaResidencia1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(68, 68, 68))
        );

        setBounds(0, 0, 910, 558);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalvaResidenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvaResidenciaActionPerformed
        if (idUsuarioSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um morador na tabela para excluir.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja excluir este morador?\n\n" +
            "Nome: " + txtNomeMorador.getText() + "\n" +
            "CPF: " + txtCpfMorador.getText(),
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            pst.setInt(1, idUsuarioSelecionado);
            
            int resultado = pst.executeUpdate();
            pst.close();
            
            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Morador excluído com sucesso!",
                    "Exclusão Realizada",
                    JOptionPane.INFORMATION_MESSAGE);
                
                limparCampos();
                carregarMoradores();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir morador. Tente novamente.");
            }
        } catch (SQLException e) {
            // Verifica se é erro de foreign key (usuário está sendo usado em outras tabelas)
            if (e.getMessage().contains("foreign key") || e.getMessage().contains("violates foreign key")) {
                JOptionPane.showMessageDialog(this, 
                    "Não é possível excluir este morador pois ele possui registros relacionados " +
                    "(reservas, manutenções, etc.).\n\n" +
                    "Para excluir, primeiro remova todos os registros relacionados.",
                    "Erro de Integridade",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir morador: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnSalvaResidenciaActionPerformed

    private void btnSalvaResidencia1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvaResidencia1ActionPerformed
        if (idUsuarioSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um morador na tabela para editar.");
            return;
        }

        // Validação dos campos obrigatórios
        if (txtNomeMorador.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o nome do morador.");
            txtNomeMorador.requestFocus();
            return;
        }
        
        if (txtCpfMorador.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o CPF do morador.");
            txtCpfMorador.requestFocus();
            return;
        }
        
        if (txtTelefoneMorador.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o telefone do morador.");
            txtTelefoneMorador.requestFocus();
            return;
        }
        
        if (txtRua.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha a rua.");
            txtRua.requestFocus();
            return;
        }
        
        if (txtNumero.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o número.");
            txtNumero.requestFocus();
            return;
        }
        
        if (txtLogin.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o login.");
            txtLogin.requestFocus();
            return;
        }
        
        if (txtSenha.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha a senha.");
            txtSenha.requestFocus();
            return;
        }

        String perfilSelecionado = (String) cbbPerfil.getSelectedItem();
        if (perfilSelecionado == null || perfilSelecionado.trim().isEmpty() || perfilSelecionado.equals(" ")) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um perfil.");
            cbbPerfil.requestFocus();
            return;
        }

        String username = txtLogin.getText().trim();
        String cpf = txtCpfMorador.getText().trim();

        // Verifica se o username já existe em outro usuário
        String sqlVerificaUsername = "SELECT COUNT(*) FROM usuario WHERE username = ? AND id_usuario != ?";
        try {
            PreparedStatement pstVerifica = conexao.prepareStatement(sqlVerificaUsername);
            pstVerifica.setString(1, username);
            pstVerifica.setInt(2, idUsuarioSelecionado);
            ResultSet rs = pstVerifica.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Este login já está em uso por outro usuário. Por favor, escolha outro.",
                    "Login Duplicado", JOptionPane.WARNING_MESSAGE);
                rs.close();
                pstVerifica.close();
                return;
            }
            rs.close();
            pstVerifica.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar login: " + e.getMessage());
            return;
        }

        // Verifica se o CPF já existe em outro usuário
        String sqlVerificaCpf = "SELECT COUNT(*) FROM usuario WHERE cpf = ? AND id_usuario != ?";
        try {
            PreparedStatement pstVerifica = conexao.prepareStatement(sqlVerificaCpf);
            pstVerifica.setString(1, cpf);
            pstVerifica.setInt(2, idUsuarioSelecionado);
            ResultSet rs = pstVerifica.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Este CPF já está cadastrado para outro usuário.",
                    "CPF Duplicado", JOptionPane.WARNING_MESSAGE);
                rs.close();
                pstVerifica.close();
                return;
            }
            rs.close();
            pstVerifica.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar CPF: " + e.getMessage());
            return;
        }

        // Atualiza o usuário
        String sql = "UPDATE usuario SET " +
                     "nome = ?, cpf = ?, telefone = ?, rua = ?, numero = ?, " +
                     "veiculo = ?, placa = ?, username = ?, senha = ?, perfil = ? " +
                     "WHERE id_usuario = ?";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            pst.setString(1, txtNomeMorador.getText().trim());
            pst.setString(2, cpf);
            pst.setString(3, txtTelefoneMorador.getText().trim());
            pst.setString(4, txtRua.getText().trim());
            pst.setString(5, txtNumero.getText().trim());
            
            // Veículo - pode ser null se não selecionado
            String tipoVeiculo = (String) cbbVeiculo.getSelectedItem();
            if (tipoVeiculo == null || tipoVeiculo.trim().isEmpty() || tipoVeiculo.equals("  ")) {
                pst.setString(6, null);
            } else {
                pst.setString(6, tipoVeiculo);
            }
            
            // Placa - pode ser null se vazio
            String placa = txtPlacaVeiculo.getText().trim();
            if (placa.isEmpty()) {
                pst.setString(7, null);
            } else {
                pst.setString(7, placa);
            }
            
            pst.setString(8, username);
            pst.setString(9, new String(txtSenha.getPassword()));
            pst.setString(10, perfilSelecionado);
            pst.setInt(11, idUsuarioSelecionado);
            
            int resultado = pst.executeUpdate();
            pst.close();
            
            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Morador atualizado com sucesso!",
                    "Atualização Realizada",
                    JOptionPane.INFORMATION_MESSAGE);
                
                carregarMoradores();
                // Mantém a seleção após atualizar
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 0).equals(idUsuarioSelecionado)) {
                        tblMoradores.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar morador. Tente novamente.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar morador: " + e.getMessage());
        }
    }//GEN-LAST:event_btnSalvaResidencia1ActionPerformed

    private void cbbVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbbVeiculoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbbVeiculoActionPerformed

    private void txtSenhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSenhaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSenhaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSalvaResidencia;
    private javax.swing.JButton btnSalvaResidencia1;
    private javax.swing.JComboBox<String> cbbPerfil;
    private javax.swing.JComboBox<String> cbbVeiculo;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblMoradores;
    private javax.swing.JTextField txtCpfMorador;
    private javax.swing.JTextField txtLogin;
    private javax.swing.JTextField txtNomeMorador;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JTextField txtPesquisaMoradores;
    private javax.swing.JTextField txtPlacaVeiculo;
    private javax.swing.JTextField txtRua;
    private javax.swing.JPasswordField txtSenha;
    private javax.swing.JTextField txtTelefoneMorador;
    // End of variables declaration//GEN-END:variables
}
