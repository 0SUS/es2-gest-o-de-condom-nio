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
public class EditarExcluirResidencia extends javax.swing.JInternalFrame {
    private Connection conexao = null;
    private DefaultTableModel tableModel;
    private Integer idResidenciaSelecionada = null;
    /**
     * Creates new form EditarExcluirResidencia
     */
    public EditarExcluirResidencia() {
        initComponents();
        conexao = ModuloConexao.conector();
        setUpTable();
        carregarResidencias();
        setUpListeners();
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
    }
    
    private void setUpTable(){
        String [] colunas  = {"ID", "Número", "Rua","Proprietário", "Telefone"};
        tableModel = new DefaultTableModel(colunas, 0){
        @Override
        public boolean isCellEditable(int row, int column){
            return false;
        }
        };
        tblResidencia.setModel(tableModel);
        tblResidencia.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }

    private void setUpListeners(){
     // Listener para quando selecionar uma linha na tabela
        tblResidencia.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    preencherCamposComSelecao();
                }
            }
        });

        // Listener para pesquisa (buscar ao pressionar Enter)
        txtPesquisaResidencia.addActionListener(e -> buscarResidencias());
        
        // Listener para buscar quando perder o foco do campo de pesquisa
        txtPesquisaResidencia.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
               buscarResidencias();
            }
        });
        
        // Listener para o botão de busca (jLabel1)
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buscarResidencias();
            }
        });
    }
    
    private void carregarResidencias(){
        String sql = "SELECT id_residencia, numero, rua, nome_proprietario, telefone " +
                     "FROM residencia " +
                     "ORDER BY rua";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            tableModel.setRowCount(0);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_residencia"),
                    rs.getString("numero"),
                    rs.getString("rua"),
                    rs.getString("nome_proprietario"),
                    rs.getString("telefone"),
                   
                };
                tableModel.addRow(row);
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar residencias: " + e.getMessage());
        }
    
    }
    private void buscarResidencias(){
         String pesquisa = txtPesquisaResidencia.getText().trim();
        
        String sql;
        if (pesquisa.isEmpty()) {
            sql = "SELECT id_residencia, numero, rua, nome_proprietario, telefone " +
          "FROM residencia " +
          "ORDER BY rua, numero";

        } else {
            sql = "SELECT id_residencia, numero, rua, nome_proprietario, telefone " +
          "FROM residencia " +
          "WHERE UPPER(numero) LIKE UPPER(?) OR UPPER(rua) LIKE UPPER(?) OR UPPER(nome_proprietario) LIKE UPPER(?) " +
          "ORDER BY rua, numero";
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
                    rs.getInt("id_residencia"),
                    rs.getString("numero"),
                    rs.getString("rua"),
                    rs.getString("nome_proprietario"),
                    rs.getString("telefone"),
                };
                tableModel.addRow(row);
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar residencias: " + e.getMessage());
        }
    }
    
    private void preencherCamposComSelecao(){
         int selectedRow = tblResidencia.getSelectedRow();
        
        if (selectedRow == -1) {
            limparCampos();
            return;
        }

        idResidenciaSelecionada = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        // Busca os dados completos do usuário
        String sql = "SELECT numero, rua, area, nome_proprietario, id_proprietario, telefone " +
                     "FROM residencia WHERE id_residencia = ?";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            pst.setInt(1, idResidenciaSelecionada);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                txtNumeroResidencia.setText(rs.getString("numero"));
                txtRuaResidencia.setText(rs.getString("rua"));
                txtTelefoneResidencia.setText(rs.getString("telefone"));
                
                txtIdProprietario.setText(rs.getString("id_proprietario"));
                txtAreaResidencia.setText(rs.getString("area"));
               txtProprietarioResidencia.setText(rs.getString("nome_proprietario"));
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados da residencia: " + e.getMessage());
        }
    }
    
    private void limparCampos(){
        idResidenciaSelecionada = null;
        txtAreaResidencia.setText("");
        txtIdProprietario.setText("");
        txtNumeroResidencia.setText("");
        txtPesquisaResidencia.setText("");
        txtProprietarioResidencia.setText("");
        txtRuaResidencia.setText("");
        txtTelefoneResidencia.setText("");
        tblResidencia.clearSelection();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtPesquisaResidencia = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblResidencia = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtRuaResidencia = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtNumeroResidencia = new javax.swing.JTextField();
        txtAreaResidencia = new javax.swing.JTextField();
        txtProprietarioResidencia = new javax.swing.JTextField();
        txtTelefoneResidencia = new javax.swing.JTextField();
        btnDeletaResidencia = new javax.swing.JButton();
        btnSalvaResidencia = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtIdProprietario = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Editar/Excluir residência ");
        setMinimumSize(new java.awt.Dimension(910, 560));
        setPreferredSize(new java.awt.Dimension(910, 540));

        jLabel1.setText("🔍 Buscar");

        jLabel2.setText("*Campo obrigatório");

        tblResidencia.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblResidencia);

        jLabel5.setText("*Número:");

        jLabel6.setText("*Rua:");

        jLabel3.setText("*Área: (m²)");

        jLabel4.setText("*Proprietário:");

        jLabel7.setText("*Telefone:");

        txtNumeroResidencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNumeroResidenciaActionPerformed(evt);
            }
        });

        btnDeletaResidencia.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnDeletaResidencia.setText("Deletar");
        btnDeletaResidencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeletaResidenciaActionPerformed(evt);
            }
        });

        btnSalvaResidencia.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnSalvaResidencia.setText("Salvar Alteração");
        btnSalvaResidencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvaResidenciaActionPerformed(evt);
            }
        });

        jLabel8.setText("*ID proprietário:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnSalvaResidencia)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel6)
                                            .addComponent(jLabel5))
                                        .addGap(31, 31, 31)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtAreaResidencia, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                                            .addComponent(txtRuaResidencia)
                                            .addComponent(txtNumeroResidencia)))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(10, 10, 10)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel4)
                                                .addComponent(jLabel7))
                                            .addGap(18, 18, 18)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(txtProprietarioResidencia)
                                                .addComponent(txtTelefoneResidencia)))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel8)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(txtIdProprietario, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(88, 88, 88)
                                .addComponent(btnDeletaResidencia))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtPesquisaResidencia, javax.swing.GroupLayout.PREFERRED_SIZE, 553, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)
                                .addGap(83, 83, 83)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addGap(46, 46, 46))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 784, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPesquisaResidencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(33, 33, 33)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNumeroResidencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtRuaResidencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtAreaResidencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtProprietarioResidencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtIdProprietario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(txtTelefoneResidencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvaResidencia, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeletaResidencia, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        setBounds(0, 0, 910, 560);
    }// </editor-fold>//GEN-END:initComponents

    private void txtNumeroResidenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumeroResidenciaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNumeroResidenciaActionPerformed

    private void btnDeletaResidenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletaResidenciaActionPerformed
        // TODO add your handling code here:
        if (idResidenciaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma residencia na tabela para excluir.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja excluir esta residência?\n\n" +
            "Numero: " + txtNumeroResidencia.getText().trim() + "\n" +
            "Rua: " + txtRuaResidencia.getText().trim() + "\n" +
            "Proprietario: " + txtProprietarioResidencia.getText().trim(),
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM residencia WHERE id_residencia = ?";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            pst.setInt(1, idResidenciaSelecionada);
            
            int resultado = pst.executeUpdate();
            pst.close();
            
            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Residência excluída com sucesso!",
                    "Exclusão Realizada",
                    JOptionPane.INFORMATION_MESSAGE);
                
                limparCampos();
                carregarResidencias();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir residência. Tente novamente.");
            }
        } catch (SQLException e) {
            
                JOptionPane.showMessageDialog(this, "Erro ao excluir residência: " + e.getMessage());
            
        }
    }//GEN-LAST:event_btnDeletaResidenciaActionPerformed

    private void btnSalvaResidenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvaResidenciaActionPerformed
        // TODO add your handling code here:
        if (idResidenciaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma residência na tabela para editar.");
            return;
        }
        // validação dos campos obrigatórios:
        if (txtNumeroResidencia.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o número da residência.");
            txtNumeroResidencia.requestFocus();
            return;
        }
        if (txtRuaResidencia.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha a rua da residência.");
            txtRuaResidencia.requestFocus();
            return;
        }
        if (txtAreaResidencia.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o tamanho da residência.");
            txtAreaResidencia.requestFocus();
            return;
        }
        if (txtProprietarioResidencia.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o nome do proprietário.");
            txtProprietarioResidencia.requestFocus();
            return;
        }
        if (txtIdProprietario.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o ID do proprietário.");
            txtIdProprietario.requestFocus();
            return;
        } 
        if (txtTelefoneResidencia.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o telefone do proprietário.");
            txtTelefoneResidencia.requestFocus();
            return;
        }
        // verifica se foi inserido um numero no campo area
            try{
                
               String area_txt = txtAreaResidencia.getText().trim();
               area_txt = area_txt.replace(",", ".");
              float area_residencia = Float.parseFloat(area_txt);
              if(area_residencia <= 0){
                  JOptionPane.showMessageDialog(this, 
                "A área deve ser maior que zero.Por favor, coloque outro valor",
                "Valor inválido", JOptionPane.WARNING_MESSAGE);
                 txtAreaResidencia.setText(""); 
                 txtAreaResidencia.requestFocus();
                  return;
              }
            }
             catch (NumberFormatException ex){
                 JOptionPane.showMessageDialog(this, 
                "Área inválida! Digite apenas números.",
                "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                txtAreaResidencia.setText(""); 
                txtAreaResidencia.requestFocus();
             }
        // verifica se o id do proprietario é válido
        try{
            String sqlVerifica = "SELECT COUNT(*) FROM usuario WHERE id_usuario = ?";
            int id_proprietario = Integer.parseInt(txtIdProprietario.getText().trim());
            PreparedStatement pstVerifica = conexao.prepareStatement(sqlVerifica);
            pstVerifica.setInt(1, id_proprietario);
            ResultSet rs = pstVerifica.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(this, 
                "Não existem moradores cadastrados com esse ID. Por favor, coloque outro.",
                "ID inválido", JOptionPane.WARNING_MESSAGE);
            rs.close();
            pstVerifica.close();
            return;
            }
            rs.close();
            pstVerifica.close();
        }
        catch (NumberFormatException ex) {
    
            JOptionPane.showMessageDialog(this, 
            "ID inválido! Digite apenas números.",
            "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            txtIdProprietario.setText(""); 
            txtIdProprietario.requestFocus();
        } 
        catch(SQLException e){
            JOptionPane.showMessageDialog(this, 
                "Erro ao verificar ID do proprietário: " + e.getMessage(),
                "Erro de Banco", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Atualiza a residencia
        String sql = "UPDATE residencia SET " +
                     "numero = ?, rua = ?, nome_proprietario = ?, " +
                     "id_proprietario = ?, area = ?, telefone = ? " +
                     "WHERE id_residencia = ?";
        try {
            PreparedStatement pstResidencia = conexao.prepareStatement(sql);
            pstResidencia.setString(1, txtNumeroResidencia.getText().trim());
            pstResidencia.setString(2, txtRuaResidencia.getText().trim());
            pstResidencia.setString(3, txtProprietarioResidencia.getText().trim());
            pstResidencia.setInt(4,Integer.parseInt(txtIdProprietario.getText().trim()));
            pstResidencia.setFloat(5,  Float.parseFloat(txtAreaResidencia.getText().trim()));
            pstResidencia.setString(6, txtTelefoneResidencia.getText().trim());
            pstResidencia.setInt(7,idResidenciaSelecionada);
            
            int resultado = pstResidencia.executeUpdate();
            pstResidencia.close();
            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Residência atualizada com sucesso!",
                    "Atualização Realizada",
                    JOptionPane.INFORMATION_MESSAGE);
                
                carregarResidencias();
                // Mantém a seleção após atualizar
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 0).equals(idResidenciaSelecionada)) {
                        tblResidencia.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar residência. Tente novamente.");
            }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Erro ao atualizar residência: " + e.getMessage());
        }
    }//GEN-LAST:event_btnSalvaResidenciaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeletaResidencia;
    private javax.swing.JButton btnSalvaResidencia;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblResidencia;
    private javax.swing.JTextField txtAreaResidencia;
    private javax.swing.JTextField txtIdProprietario;
    private javax.swing.JTextField txtNumeroResidencia;
    private javax.swing.JTextField txtPesquisaResidencia;
    private javax.swing.JTextField txtProprietarioResidencia;
    private javax.swing.JTextField txtRuaResidencia;
    private javax.swing.JTextField txtTelefoneResidencia;
    // End of variables declaration//GEN-END:variables
}
