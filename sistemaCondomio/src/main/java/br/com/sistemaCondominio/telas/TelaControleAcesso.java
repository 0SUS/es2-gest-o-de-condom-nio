package br.com.sistemaCondominio.telas;


import br.com.sistemaCondominio.dal.ModuloConexao;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.Date;
import java.sql.Time;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;


public class TelaControleAcesso extends JInternalFrame {


    private JTextField txtCpf;
    private JComboBox<String> cbArea;
    private JButton btnVerificar;
    private JLabel lblStatus;

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;


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


        JLabel lblCpf = new JLabel("CPF do Morador:");
        lblCpf.setBounds(85, 80, 120, 20);
        panel.add(lblCpf);

        txtCpf = new JTextField();
        txtCpf.setBounds(215, 80, 150, 25);
        panel.add(txtCpf);

        JLabel lblArea = new JLabel("Área de Acesso:");
        lblArea.setBounds(85, 120, 120, 20);
        panel.add(lblArea);

        cbArea = new JComboBox<>(new String[]{"Piscina", "Churrascaria", "Sala de Festas"});
        cbArea.setBounds(215, 120, 150, 25);
        panel.add(cbArea);

        btnVerificar = new JButton("Verificar Acesso");
        btnVerificar.setBounds(150, 170, 150, 30);
        panel.add(btnVerificar);


        lblStatus = new JLabel("Acesso negado.");
        lblStatus.setForeground(Color.RED);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setBounds(10, 210, 414, 20);
        lblStatus.setVisible(false);
        panel.add(lblStatus);


        btnVerificar.addActionListener(e -> validarAcesso());

        conexao = ModuloConexao.conector();
    }



    private void validarAcesso() {
        String cpf = txtCpf.getText().trim();
        String area = (String) cbArea.getSelectedItem();

        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira o CPF.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 1. Verificar se o CPF pertence a um usuário válido e obter o ID
            String sqlUsuario = "SELECT id_usuario FROM usuario WHERE cpf = ?";
            pst = conexao.prepareStatement(sqlUsuario);
            pst.setString(1, cpf);
            rs = pst.executeQuery();

            if (!rs.next()) {
                lblStatus.setText("CPF não encontrado no sistema.");
                lblStatus.setVisible(true);
                return;
            }
            int usuarioId = rs.getInt("id_usuario");

            // 2. Verificar se há uma reserva ativa para a área
            String sqlVerifica = "SELECT usuario_id FROM reservas_areas_comuns " +
                                 "WHERE area = ? AND data_reserva = ? AND ? BETWEEN hora_reserva AND (hora_reserva + INTERVAL '1 hour')";
            pst = conexao.prepareStatement(sqlVerifica);
            pst.setString(1, area);
            pst.setDate(2, Date.valueOf(LocalDate.now()));
            pst.setTime(3, Time.valueOf(LocalTime.now()));
            rs = pst.executeQuery();

            if (rs.next()) { // Cenário 1 e 2: A área está reservada
                int idDonoReserva = rs.getInt("usuario_id");
                if (idDonoReserva == usuarioId) {
                    // Cenário 1: Acesso liberado, a reserva é do usuário
                    JOptionPane.showMessageDialog(this, "Acesso Liberado! (Reserva existente)", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    // Cenário 2: Acesso negado, a área está reservada por outra pessoa
                    lblStatus.setText("Acesso Negado. A área já está reservada por outro morador.");
                    lblStatus.setVisible(true);
                }
            } else { // Cenário 3: A área não está reservada
                // Cria uma reserva imediata
                String sqlInsert = "INSERT INTO reservas_areas_comuns (area, data_reserva, hora_reserva, usuario_id, observacoes) VALUES (?, ?, ?, ?, ?)";
                pst = conexao.prepareStatement(sqlInsert);
                pst.setString(1, area);
                pst.setDate(2, Date.valueOf(LocalDate.now()));
                pst.setTime(3, Time.valueOf(LocalTime.now()));
                pst.setInt(4, usuarioId);
                pst.setString(5, "Reserva imediata via controle de acesso");

                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(this, "Acesso Liberado! A área foi reservada para você.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    lblStatus.setText("Falha ao tentar reservar a área.");
                    lblStatus.setVisible(true);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao validar acesso: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }
}
