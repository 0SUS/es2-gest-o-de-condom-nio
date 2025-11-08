/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.com.sistemaCondominio.telas;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JOptionPane;

public class GestaoTaxas extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GestaoTaxas.class.getName());

    /**
     * Creates new form GestaoTaxas
     */
    public GestaoTaxas() {
        initComponents();
        configurarPlaceholders();

    }

   private void configurarPlaceholders() {
    adicionarPlaceholder(campoUnidade, "Unidade");
    adicionarPlaceholder(campoValor, "Valor (R$)");
    adicionarPlaceholder(campoData, "Data de vencimento");
    adicionarPlaceholder(campoStatus, "Status de pagamento");
}

private void adicionarPlaceholder(javax.swing.JTextField campo, String textoPlaceholder) {
    campo.setText(textoPlaceholder);
    campo.setForeground(Color.GRAY);

    campo.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (campo.getText().equals(textoPlaceholder)) {
                campo.setText("");
                campo.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (campo.getText().isEmpty()) {
                campo.setText(textoPlaceholder);
                campo.setForeground(Color.GRAY);
            }
        }
    });
}

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelTitulo = new javax.swing.JLabel();
        campoUnidade = new javax.swing.JTextField();
        campoValor = new javax.swing.JTextField();
        campoData = new javax.swing.JTextField();
        campoStatus = new javax.swing.JTextField();
        btnCancelar = new javax.swing.JButton();
        btnConfirmar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        areaHistorico = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gestão de taxas");

        jLabelTitulo.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabelTitulo.setText("Gestão de Taxas");

        campoUnidade.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoUnidade.setText("Unidade");

        campoValor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoValor.setText("Valor (R$)");

        campoData.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoData.setText("Data de vencimento");

        campoStatus.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        campoStatus.setText("Status de pagamento");

        btnCancelar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnCancelar.setText("Cancelar");

        btnConfirmar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnConfirmar.setText("Confirmar");
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Registrar Nova Taxa");

        areaHistorico.setColumns(20);
        areaHistorico.setRows(5);
        jScrollPane1.setViewportView(areaHistorico);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(campoStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 698, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(campoData, javax.swing.GroupLayout.PREFERRED_SIZE, 698, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(campoValor, javax.swing.GroupLayout.PREFERRED_SIZE, 698, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(campoUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, 698, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(480, 480, 480)
                            .addComponent(btnCancelar)
                            .addGap(169, 169, 169)
                            .addComponent(btnConfirmar))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(352, 352, 352)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 698, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(567, 567, 567)
                            .addComponent(jLabelTitulo))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(571, 571, 571)
                            .addComponent(jLabel1))))
                .addContainerGap(380, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addComponent(jLabelTitulo)
                .addGap(64, 64, 64)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(102, 102, 102)
                .addComponent(jLabel1)
                .addGap(48, 48, 48)
                .addComponent(campoUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(campoValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(campoData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(campoStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirmar)
                    .addComponent(btnCancelar))
                .addContainerGap(341, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        String unidade = campoUnidade.getText();
    String valor = campoValor.getText();
    String data = campoData.getText();
    String status = campoStatus.getText();

    if (unidade.equals("Unidade") || valor.equals("Valor (R$)") || data.equals("Data de vencimento") ||
        status.equals("Status de pagamento") ||
        unidade.isEmpty() || valor.isEmpty() || data.isEmpty() || status.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Preencha todos os campos antes de confirmar.");
        return;
    }

    String registro = "Unidade: " + unidade + " | Valor: " + valor +
                  " | Vencimento: " + data + " | Status: " + status;

String historicoAnterior = areaHistorico.getText();

if (historicoAnterior.isEmpty()) {
    areaHistorico.setText(registro);
} else {
    areaHistorico.setText(historicoAnterior + "\n" + registro);
}


    campoUnidade.setText("Unidade");
    campoUnidade.setForeground(Color.GRAY);
    campoValor.setText("Valor (R$)");
    campoValor.setForeground(Color.GRAY);
    campoData.setText("Data de vencimento");
    campoData.setForeground(Color.GRAY);
    campoStatus.setText("Status de pagamento");
    campoStatus.setForeground(Color.GRAY);
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {                                            
    campoUnidade.setText("Unidade");
    campoValor.setText("Valor (R$)");
    campoData.setText("Data de vencimento");
    campoStatus.setText("Status de pagamento");

    campoUnidade.setForeground(Color.GRAY);
    campoValor.setForeground(Color.GRAY);
    campoData.setForeground(Color.GRAY);
    campoStatus.setForeground(Color.GRAY);
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
    private javax.swing.JTextField campoStatus;
    private javax.swing.JTextField campoUnidade;
    private javax.swing.JTextField campoValor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelTitulo;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
