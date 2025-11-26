/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.sistemaCondominio.telas;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.sql.*;
import br.com.sistemaCondominio.dal.ModuloConexao;
import br.com.sistemaCondominio.dal.UsuarioLogado;

/**
 *
 * @author laris
 */
public class ComunicacaoInterna extends javax.swing.JInternalFrame {

    private Connection conexao = null;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat;
    private String modoAtual = "ENVIAR"; // "ENVIAR" ou "VISUALIZAR"

    /**
     * Creates new form ComunicacaoInterna
     */
    public ComunicacaoInterna() {
        initComponents();
        conexao = ModuloConexao.conector();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        setupTable();
        carregarUsuarios();
        atualizarTabelaMensagens();
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        mostrarPainelEnviar();
    }

    private void setupTable() {
        String[] colunas = {"ID", "Remetente", "Assunto", "Data/Hora", "Lida"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTableMensagens.setModel(tableModel);
        jTableMensagens.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }

    private void carregarUsuarios() {
        jComboBoxDestinatario.removeAllItems();
        jComboBoxDestinatario.addItem("Selecione um destinatário...");
        
        String sql = "SELECT id_usuario, username, nome FROM usuario ORDER BY nome";
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Integer id = rs.getInt("id_usuario");
                String username = rs.getString("username");
                String nome = rs.getString("nome");
                // Não permite enviar mensagem para si mesmo
                if (!id.equals(UsuarioLogado.getInstance().getId())) {
                    jComboBoxDestinatario.addItem(nome + " (" + username + ")");
                    // Armazenar ID no item (usando setClientProperty ou lista separada)
                }
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar usuários: " + e.getMessage());
        }
    }

    private Integer obterIdDestinatario(String itemSelecionado) {
        if (itemSelecionado == null || itemSelecionado.equals("Selecione um destinatário...")) {
            return null;
        }
        
        // Extrai o username do formato "Nome (username)"
        String username = itemSelecionado.substring(itemSelecionado.indexOf("(") + 1, itemSelecionado.indexOf(")"));
        
        String sql = "SELECT id_usuario FROM usuario WHERE username = ?";
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                Integer id = rs.getInt("id_usuario");
                rs.close();
                pst.close();
                return id;
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao obter ID do destinatário: " + e.getMessage());
        }
        
        return null;
    }

    private void atualizarTabelaMensagens() {
        if (!UsuarioLogado.getInstance().isLogado()) {
            return;
        }

        String sql = "SELECT m.id, u.nome as remetente, m.assunto, m.data_envio, m.lida " +
                     "FROM mensagens m " +
                     "INNER JOIN usuario u ON m.remetente_id = u.id_usuario " +
                     "WHERE m.destinatario_id = ? " +
                     "ORDER BY m.data_envio DESC";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            pst.setInt(1, UsuarioLogado.getInstance().getId());
            ResultSet rs = pst.executeQuery();
            
            tableModel.setRowCount(0);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("remetente"),
                    rs.getString("assunto"),
                    dateFormat.format(rs.getTimestamp("data_envio")),
                    rs.getBoolean("lida") ? "Sim" : "Não"
                };
                tableModel.addRow(row);
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar mensagens: " + e.getMessage());
        }
    }

    private void mostrarPainelEnviar() {
        modoAtual = "ENVIAR";
        jPanelEnviar.setVisible(true);
        jPanelVisualizar.setVisible(false);
        btnEnviarMensagem.setEnabled(false);
        btnVisualizarMensagens.setEnabled(true);
        limparFormulario();
    }

    private void mostrarPainelVisualizar() {
        modoAtual = "VISUALIZAR";
        jPanelEnviar.setVisible(false);
        jPanelVisualizar.setVisible(true);
        btnEnviarMensagem.setEnabled(true);
        btnVisualizarMensagens.setEnabled(false);
        atualizarTabelaMensagens();
    }

    private void limparFormulario() {
        jComboBoxDestinatario.setSelectedIndex(0);
        campoAssunto.setText("");
        campoConteudo.setText("");
    }

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {
        // Verifica se há usuário logado
        if (!UsuarioLogado.getInstance().isLogado()) {
            JOptionPane.showMessageDialog(this, "Erro: Usuário não está logado. Faça login novamente.");
            return;
        }

        String destinatarioItem = (String) jComboBoxDestinatario.getSelectedItem();
        String assunto = campoAssunto.getText().trim();
        String conteudo = campoConteudo.getText().trim();

        // Validações conforme fluxo alternativo
        if (destinatarioItem == null || destinatarioItem.equals("Selecione um destinatário...")) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, selecione um destinatário.",
                "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
            jComboBoxDestinatario.requestFocus();
            return;
        }

        if (conteudo.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, preencha o campo de mensagem.",
                "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
            campoConteudo.requestFocus();
            return;
        }

        Integer destinatarioId = obterIdDestinatario(destinatarioItem);
        if (destinatarioId == null) {
            JOptionPane.showMessageDialog(this, "Erro: Destinatário não encontrado.");
            return;
        }

        // Insere mensagem no banco
        String sql = "INSERT INTO mensagens (remetente_id, destinatario_id, assunto, conteudo, lida) " +
                     "VALUES (?, ?, ?, ?, false)";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            pst.setInt(1, UsuarioLogado.getInstance().getId());
            pst.setInt(2, destinatarioId);
            pst.setString(3, assunto.isEmpty() ? "(Sem assunto)" : assunto);
            pst.setString(4, conteudo);
            
            int resultado = pst.executeUpdate();
            pst.close();
            
            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Mensagem enviada com sucesso!",
                    "Confirmação de Envio", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao enviar mensagem. Tente novamente.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao enviar mensagem: " + e.getMessage());
        }
    }

    private void visualizarMensagemSelecionada() {
        int selectedRow = jTableMensagens.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma mensagem para visualizar.");
            return;
        }

        Integer idMensagem = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        String sql = "SELECT m.assunto, m.conteudo, m.data_envio, u.nome as remetente " +
                     "FROM mensagens m " +
                     "INNER JOIN usuario u ON m.remetente_id = u.id_usuario " +
                     "WHERE m.id = ?";
        
        try {
            PreparedStatement pst = conexao.prepareStatement(sql);
            pst.setInt(1, idMensagem);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String assunto = rs.getString("assunto");
                String conteudo = rs.getString("conteudo");
                String dataEnvio = dateFormat.format(rs.getTimestamp("data_envio"));
                String remetente = rs.getString("remetente");
                
                // Marca como lida
                String sqlUpdate = "UPDATE mensagens SET lida = true WHERE id = ?";
                PreparedStatement pstUpdate = conexao.prepareStatement(sqlUpdate);
                pstUpdate.setInt(1, idMensagem);
                pstUpdate.executeUpdate();
                pstUpdate.close();
                
                // Exibe mensagem
                String mensagemCompleta = 
                    "═══════════════════════════════════════\n" +
                    "De: " + remetente + "\n" +
                    "Assunto: " + assunto + "\n" +
                    "Data/Hora: " + dataEnvio + "\n" +
                    "═══════════════════════════════════════\n\n" +
                    conteudo + "\n" +
                    "═══════════════════════════════════════\n";
                
                jTextAreaMensagem.setText(mensagemCompleta);
                
                // Atualiza tabela para refletir status "lida"
                atualizarTabelaMensagens();
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar mensagem: " + e.getMessage());
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
        btnEnviarMensagem = new javax.swing.JButton();
        btnVisualizarMensagens = new javax.swing.JButton();
        jPanelEnviar = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxDestinatario = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        campoAssunto = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        campoConteudo = new javax.swing.JTextArea();
        btnEnviar = new javax.swing.JButton();
        jPanelVisualizar = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableMensagens = new javax.swing.JTable();
        btnVisualizar = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaMensagem = new javax.swing.JTextArea();

        setTitle("Comunicações Internas");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Comunicações Internas");

        btnEnviarMensagem.setText("Enviar Mensagem");
        btnEnviarMensagem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarMensagemActionPerformed(evt);
            }
        });

        btnVisualizarMensagens.setText("Visualizar Mensagens");
        btnVisualizarMensagens.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVisualizarMensagensActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Enviar Mensagem");

        jLabel3.setText("Destinatário:");

        jLabel4.setText("Assunto:");

        jLabel5.setText("Mensagem:");

        campoConteudo.setColumns(20);
        campoConteudo.setRows(5);
        campoConteudo.setLineWrap(true);
        campoConteudo.setWrapStyleWord(true);
        jScrollPane2.setViewportView(campoConteudo);

        btnEnviar.setText("Enviar");
        btnEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelEnviarLayout = new javax.swing.GroupLayout(jPanelEnviar);
        jPanelEnviar.setLayout(jPanelEnviarLayout);
        jPanelEnviarLayout.setHorizontalGroup(
            jPanelEnviarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEnviarLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanelEnviarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(jPanelEnviarLayout.createSequentialGroup()
                        .addGroup(jPanelEnviarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelEnviarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxDestinatario, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(campoAssunto, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelEnviarLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnEnviar)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanelEnviarLayout.setVerticalGroup(
            jPanelEnviarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEnviarLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel2)
                .addGap(30, 30, 30)
                .addGroup(jPanelEnviarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBoxDestinatario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelEnviarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(campoAssunto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelEnviarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(btnEnviar)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setText("Mensagens Recebidas");

        jTableMensagens.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTableMensagens);

        btnVisualizar.setText("Visualizar Mensagem Selecionada");
        btnVisualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVisualizarActionPerformed(evt);
            }
        });

        jTextAreaMensagem.setColumns(20);
        jTextAreaMensagem.setRows(5);
        jTextAreaMensagem.setEditable(false);
        jTextAreaMensagem.setLineWrap(true);
        jTextAreaMensagem.setWrapStyleWord(true);
        jScrollPane3.setViewportView(jTextAreaMensagem);

        javax.swing.GroupLayout jPanelVisualizarLayout = new javax.swing.GroupLayout(jPanelVisualizar);
        jPanelVisualizar.setLayout(jPanelVisualizarLayout);
        jPanelVisualizarLayout.setHorizontalGroup(
            jPanelVisualizarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelVisualizarLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanelVisualizarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                    .addGroup(jPanelVisualizarLayout.createSequentialGroup()
                        .addComponent(btnVisualizar)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanelVisualizarLayout.setVerticalGroup(
            jPanelVisualizarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelVisualizarLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnVisualizar)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnEnviarMensagem)
                        .addGap(18, 18, 18)
                        .addComponent(btnVisualizarMensagens))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelEnviar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanelVisualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEnviarMensagem)
                    .addComponent(btnVisualizarMensagens))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelEnviar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelVisualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEnviarMensagemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarMensagemActionPerformed
        mostrarPainelEnviar();
    }//GEN-LAST:event_btnEnviarMensagemActionPerformed

    private void btnVisualizarMensagensActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVisualizarMensagensActionPerformed
        mostrarPainelVisualizar();
    }//GEN-LAST:event_btnVisualizarMensagensActionPerformed

    private void btnVisualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVisualizarActionPerformed
        visualizarMensagemSelecionada();
    }//GEN-LAST:event_btnVisualizarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnviar;
    private javax.swing.JButton btnEnviarMensagem;
    private javax.swing.JButton btnVisualizar;
    private javax.swing.JButton btnVisualizarMensagens;
    private javax.swing.JTextField campoAssunto;
    private javax.swing.JTextArea campoConteudo;
    private javax.swing.JComboBox<String> jComboBoxDestinatario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanelEnviar;
    private javax.swing.JPanel jPanelVisualizar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableMensagens;
    private javax.swing.JTextArea jTextAreaMensagem;
    // End of variables declaration//GEN-END:variables
}
