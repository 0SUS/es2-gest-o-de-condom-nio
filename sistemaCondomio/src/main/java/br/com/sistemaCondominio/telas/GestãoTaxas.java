/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.com.sistemaCondominio.telas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class ComunicacaoInterna extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ComunicacaoInterna.class.getName());

    /**
     * Creates new form ComunicacaoInterna
     */
    public ComunicacaoInterna() {
       initComponents();
    configurarPlaceholders();
    configurarTela();
    }

    private void configurarTela() {
    // Abre em tela cheia
    setExtendedState(JFrame.MAXIMIZED_BOTH);

    // Ajusta tamanhos das fontes
    Font fontePadrao = new Font("Segoe UI", Font.PLAIN, 16);
    campoDestinatario.setFont(fontePadrao);
    campoAssunto.setFont(fontePadrao);
    campoConteudo.setFont(fontePadrao);
    areaMensagens.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    btnEnviar.setFont(new Font("Segoe UI", Font.BOLD, 16));

    // Configura histórico como área multilinha
    areaMensagens.setEditable(false);
    areaMensagens.setHorizontalAlignment(JTextField.LEFT);
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

        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnEnviar = new javax.swing.JButton();
        campoDestinatario = new javax.swing.JTextField();
        campoAssunto = new javax.swing.JTextField();
        campoConteudo = new javax.swing.JTextField();
        areaMensagens = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
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

        areaMensagens.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        areaMensagens.setText("Histórico");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(462, 462, 462)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                .addGap(483, 483, 483))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(areaMensagens, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(578, 578, 578)
                            .addComponent(jLabel5))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(640, 640, 640)
                            .addComponent(btnEnviar))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(437, 437, 437)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(campoConteudo, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(campoAssunto, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(campoDestinatario, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66)
                .addComponent(areaMensagens)
                .addGap(236, 236, 236)
                .addComponent(jLabel5)
                .addGap(50, 50, 50)
                .addComponent(campoDestinatario)
                .addGap(18, 18, 18)
                .addComponent(campoAssunto)
                .addGap(18, 18, 18)
                .addComponent(campoConteudo, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                .addGap(41, 41, 41)
                .addComponent(btnEnviar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(315, 315, 315))
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

    // Adiciona mensagem ao histórico
    String novaMensagem = "\nPara: " + destinatario + "\nAssunto: " + assunto + "\nMensagem: " + conteudo + "\n-----------------------------";
    areaMensagens.setText(areaMensagens.getText() + novaMensagem);

    // Limpa campos após envio
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
    private javax.swing.JTextField areaMensagens;
    private javax.swing.JButton btnEnviar;
    private javax.swing.JTextField campoAssunto;
    private javax.swing.JTextField campoConteudo;
    private javax.swing.JTextField campoDestinatario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    // End of variables declaration//GEN-END:variables
}
