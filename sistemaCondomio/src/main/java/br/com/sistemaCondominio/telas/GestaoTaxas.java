/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.com.sistemaCondominio.telas;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import br.com.sistemaCondominio.dal.ModuloConexao;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class GestaoTaxas extends javax.swing.JInternalFrame {
    private Connection conexao;
 
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GestaoTaxas.class.getName());

    /**
     * Creates new form GestaoTaxas
     */
    public GestaoTaxas() {
        initComponents();
        conexao = ModuloConexao.conector();
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        configurarPlaceholders();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void configurarPlaceholders() {
        adicionarPlaceholder(campoUnidade, "Unidade");
        adicionarPlaceholder(campoValor, "Valor");
        adicionarPlaceholder(campoData, "Data de Vencimento");
    }

    private void adicionarPlaceholder(JTextField campo, String texto) {
    campo.setText(texto);
    campo.setForeground(java.awt.Color.GRAY);

    campo.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (campo.getText().equals(texto)) {
                campo.setText("");
                campo.setForeground(java.awt.Color.BLACK); // ✅ cor visível ao digitar
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (campo.getText().isEmpty()) {
                campo.setText(texto);
                campo.setForeground(java.awt.Color.GRAY);
            }
        }
    });
}
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        areaHistorico = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        campoUnidade = new javax.swing.JTextField();
        campoValor = new javax.swing.JTextField();
        campoData = new javax.swing.JTextField();
        campoStatus = new JComboBox<>(new String[]{"Pendente", "Paga"});
        btnCancelar = new javax.swing.JButton();
        btnConfirmar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gestão de Taxas");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel1.setText("Gestão de Taxas");

        areaHistorico.setColumns(20);
        areaHistorico.setRows(5);
        jScrollPane1.setViewportView(areaHistorico);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Registrar Nova Taxa");

        campoUnidade.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoUnidade.setText("Unidade");
        campoUnidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoUnidadeActionPerformed(evt);
            }
        });

        campoValor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoValor.setText("Valor");
        campoValor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoValorActionPerformed(evt);
            }
        });

        campoData.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoData.setText("Data de Vencimento");
        campoData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoDataActionPerformed(evt);
            }
        });

        campoStatus.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnConfirmar.setText("Confirmar");
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(350, 350, 350)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(310, 310, 310)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(campoData, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnCancelar)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnConfirmar))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(campoUnidade, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(campoStatus, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(campoValor, javax.swing.GroupLayout.Alignment.LEADING)))))
                .addContainerGap(342, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(campoUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(campoValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(campoData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(campoStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar)
                    .addComponent(btnConfirmar))
                .addContainerGap(85, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void campoUnidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoUnidadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoUnidadeActionPerformed

    private void campoValorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoValorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoValorActionPerformed

    private void campoDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoDataActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoDataActionPerformed


    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {
        String numeroResidencia = campoUnidade.getText().trim();
        String valorStr = campoValor.getText().trim();
        String dataStr = campoData.getText().trim();
        String status = (String) campoStatus.getSelectedItem();

        if (numeroResidencia.isEmpty() || valorStr.isEmpty() || dataStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 1. Validar e obter o ID da residência
            // Tabela: residencia, Coluna: numero
            String sqlResidencia = "SELECT id_residencia FROM residencia WHERE numero = ?";
            PreparedStatement pstRes = conexao.prepareStatement(sqlResidencia);
            pstRes.setString(1, numeroResidencia);
            ResultSet rs = pstRes.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Nº da residência não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int idResidencia = rs.getInt("id_residencia");

            // 2. Preparar dados para inserção
            double valor = Double.parseDouble(valorStr);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date utilDate = sdf.parse(dataStr);
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

            // 3. Inserir a taxa no banco de dados
            String sqlInsert = "INSERT INTO taxas (id_residencia, valor, data_vencimento, status) VALUES (?, ?, ?, ?)";
            PreparedStatement pstInsert = conexao.prepareStatement(sqlInsert);
            pstInsert.setInt(1, idResidencia);
            pstInsert.setDouble(2, valor);
            pstInsert.setDate(3, sqlDate);
            pstInsert.setString(4, status);

            int adicionado = pstInsert.executeUpdate();

            if (adicionado > 0) {
                JOptionPane.showMessageDialog(this, "Taxa registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                areaHistorico.append("Taxa para unidade " + numeroResidencia + " registrada com sucesso.\n");
                // Limpar campos
                campoUnidade.setText("Unidade");
                campoUnidade.setForeground(java.awt.Color.GRAY);
                campoValor.setText("Valor");
                campoValor.setForeground(java.awt.Color.GRAY);
                campoData.setText("Data de Vencimento");
                campoData.setForeground(java.awt.Color.GRAY);
                campoStatus.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao registrar a taxa.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro de banco de dados: " + ex.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/MM/yyyy.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Formato de valor inválido. Use apenas números.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new GestaoTaxas().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaHistorico;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JTextField campoData;
    private javax.swing.JComboBox<String> campoStatus;
    private javax.swing.JTextField campoUnidade;
    private javax.swing.JTextField campoValor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}


