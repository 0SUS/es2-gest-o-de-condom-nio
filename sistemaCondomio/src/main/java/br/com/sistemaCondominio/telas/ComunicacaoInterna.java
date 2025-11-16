/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.com.sistemaCondominio.telas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ComunicacaoInterna extends javax.swing.JInternalFrame {

    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ComunicacaoInterna.class.getName());

    /**
     * Creates new form ComunicacaoInterna
     */
    public ComunicacaoInterna() {
       initComponents();
    configurarPlaceholders();
    configurarTela();
     setVisible(true);
     
     
    }

    private void configurarTela() {
    // Abre em tela cheia
    setClosable(true);
    setMaximizable(true);
    setIconifiable(true);
    setResizable(true);

    // Ajusta tamanhos das fontes
    Font fontePadrao = new Font("Segoe UI", Font.PLAIN, 16);
    campoDestinatario.setFont(fontePadrao);
    campoAssunto.setFont(fontePadrao);
    campoConteudo.setFont(fontePadrao);
 
    btnEnviar.setFont(new Font("Segoe UI", Font.BOLD, 16));

    // Configura histórico como área multilinha
    histórico.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    histórico.setEditable(false);
    histórico.setLineWrap(true);
    histórico.setWrapStyleWord(true);

}

    private void configurarPlaceholders() {
    configurarPlaceholder(campoDestinatario, "Destinatário");
    configurarPlaceholder(campoAssunto, "Assunto");
    configurarPlaceholder(campoConteudo, "Conteúdo");
}

    private void configurarPlaceholder(JTextField campo, String texto) {
    campo.setForeground(Color.GRAY);
    campo.setText(texto);

    campo.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (campo.getText().equals(texto)) {
                campo.setText("");
                campo.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (campo.getText().isEmpty()) {
                campo.setForeground(Color.GRAY);
                campo.setText(texto);
            }
        }
    });
}

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnEnviar = new javax.swing.JButton();
        campoDestinatario = new javax.swing.JTextField();
        campoAssunto = new javax.swing.JTextField();
        campoConteudo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        histórico = new javax.swing.JTextArea();

        jInternalFrame1.setVisible(true);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Comunicação interna");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel1.setText("Comunicação Interna");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel5.setText("Enviar Mensagem");

        btnEnviar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnEnviar.setText("Enviar");
        btnEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarActionPerformed(evt);
            }
        });

        campoDestinatario.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoDestinatario.setText("Destinatário");
        campoDestinatario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoDestinatarioActionPerformed(evt);
            }
        });

        campoAssunto.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoAssunto.setText("Assunto");

        campoConteudo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoConteudo.setText("Conteúdo");

        histórico.setColumns(20);
        histórico.setRows(5);
        jScrollPane1.setViewportView(histórico);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(272, 272, 272)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(345, 345, 345)
                                .addComponent(jLabel5))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(409, 409, 409)
                                .addComponent(btnEnviar))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(212, 212, 212)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(campoAssunto, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(campoDestinatario, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(campoConteudo, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 217, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(campoDestinatario)
                .addGap(18, 18, 18)
                .addComponent(campoAssunto)
                .addGap(18, 18, 18)
                .addComponent(campoConteudo)
                .addGap(18, 18, 18)
                .addComponent(btnEnviar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(98, 98, 98))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void campoDestinatarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoDestinatarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoDestinatarioActionPerformed

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarActionPerformed
         String destinatario = campoDestinatario.getText();
    String assunto = campoAssunto.getText();
    String conteudo = campoConteudo.getText();

    if (destinatario.isEmpty() || assunto.isEmpty() || conteudo.isEmpty()
            || destinatario.equals("Destinatário") || assunto.equals("Assunto") || conteudo.equals("Conteúdo")) {
        JOptionPane.showMessageDialog(this, "Preencha todos os campos antes de enviar.");
        return;
    }

    // Adiciona mensagem no histórico corretamente
    String novaMensagem = 
            "Para: " + destinatario +
            "\nAssunto: " + assunto +
            "\nMensagem: " + conteudo +
            "\n------------------------------------\n";

    histórico.append(novaMensagem);

    // Scroll sempre no final
    histórico.setCaretPosition(histórico.getDocument().getLength());

    // Limpa campos
    campoDestinatario.setText("Destinatário");
    campoAssunto.setText("Assunto");
    campoConteudo.setText("Conteúdo");

    campoDestinatario.setForeground(Color.GRAY);
    campoAssunto.setForeground(Color.GRAY);
    campoConteudo.setForeground(Color.GRAY);
    }//GEN-LAST:event_btnEnviarActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new ComunicacaoInterna().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnviar;
    private javax.swing.JTextField campoAssunto;
    private javax.swing.JTextField campoConteudo;
    private javax.swing.JTextField campoDestinatario;
    private javax.swing.JTextArea histórico;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
