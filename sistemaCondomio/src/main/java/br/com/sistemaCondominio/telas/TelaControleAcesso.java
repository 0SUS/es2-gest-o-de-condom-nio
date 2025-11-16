package br.com.sistemaCondominio.telas;


import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class TelaControleAcesso extends JInternalFrame {


    private JTextField txtCodigoAcesso;
    private JButton btnEnviar;
    private JLabel lblStatus;


    public TelaControleAcesso() {
        setTitle("Controle de Acesso");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setBounds(100, 100, 450, 300);


        JPanel panel = new JPanel();
        panel.setLayout(null);
        getContentPane().add(panel);


        JLabel lblTitulo = new JLabel("Login de Acesso à Área");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(10, 20, 414, 25);
        panel.add(lblTitulo);


        JLabel lblCodigoAcesso = new JLabel("Código de Acesso");
        lblCodigoAcesso.setBounds(85, 80, 120, 20);
        panel.add(lblCodigoAcesso);


        txtCodigoAcesso = new JTextField();
        txtCodigoAcesso.setBounds(215, 80, 150, 25);
        panel.add(txtCodigoAcesso);


        btnEnviar = new JButton("Enviar");
        btnEnviar.setBounds(175, 130, 100, 30);
        panel.add(btnEnviar);


        lblStatus = new JLabel("Código de acesso inválido ou expirado.");
        lblStatus.setForeground(Color.RED);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setBounds(10, 180, 414, 20);
        lblStatus.setVisible(false);
        panel.add(lblStatus);


        btnEnviar.addActionListener(e -> validarAcesso());
    }


    private void validarAcesso() {
        String codigo = txtCodigoAcesso.getText();


        if (codigo.trim().isEmpty()) {
            lblStatus.setText("Por favor, insira um código.");
            lblStatus.setVisible(true);
            return;
        }


        // Simulação da lógica de negócios
        if (codigo.equals("1234")) {
            // TODO: Registrar evento de acesso no banco de dados
            // TODO: Enviar notificação opcional ao administrador
            JOptionPane.showMessageDialog(this, "Acesso Liberado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Fecha a janela interna
        } else {
            // TODO: Registrar tentativa de acesso inválida
            lblStatus.setText("Código de acesso inválido ou expirado.");
            lblStatus.setVisible(true);
        }
    }
}
