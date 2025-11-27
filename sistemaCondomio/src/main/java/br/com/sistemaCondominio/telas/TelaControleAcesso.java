package br.com.sistemaCondominio.telas;


import br.com.sistemaCondominio.dal.ModuloConexao;
import br.com.sistemaCondominio.dal.ReservaDAO;
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

    // Controle de tentativas falhas (em memória)
    private int tentativasFalhas = 0;
    private final int MAX_TENTATIVAS = 3;
    private String ultimoCpfTentado = "";


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

    private void registrarLog(Integer usuarioId, String tipoEvento, String detalhes, String area) {
        String sqlLog = "INSERT INTO historico_acesso (usuario_id, tipo_evento, detalhes, area) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pstLog = conexao.prepareStatement(sqlLog);
            if (usuarioId != null) {
                pstLog.setInt(1, usuarioId);
            } else {
                pstLog.setNull(1, java.sql.Types.INTEGER);
            }
            pstLog.setString(2, tipoEvento);
            pstLog.setString(3, detalhes);
            pstLog.setString(4, area);
            pstLog.executeUpdate();
            pstLog.close();
        } catch (Exception e) {
            System.out.println("Erro ao registrar log: " + e.getMessage());
        }
    }

    private void notificarAdministradores(String mensagemAlerta) {
        String sqlAdms = "SELECT id_usuario FROM usuario WHERE perfil = 'Administrador'";
        String sqlMsg = "INSERT INTO mensagens (remetente_id, destinatario_id, assunto, conteudo, lida) VALUES (?, ?, ?, ?, false)";
        
        try {
            // Busca ID do sistema ou usa um admin padrão, ou null (se tabela permitir). 
            // Assumindo que o alerta vem do sistema, mas tabela pede remetente_id FK.
            // Vou tentar usar o ID 1 ou o primeiro ADM encontrado como remetente se não tiver usuário logado contexto.
            // Mas idealmente seria um usuário 'SISTEMA'.
            // Para simplificar, vou usar o primeiro ADM encontrado para enviar para si mesmo e outros.
            
            PreparedStatement pstAdms = conexao.prepareStatement(sqlAdms);
            ResultSet rsAdms = pstAdms.executeQuery();
            
            while (rsAdms.next()) {
                int idAdm = rsAdms.getInt("id_usuario");
                
                PreparedStatement pstMsg = conexao.prepareStatement(sqlMsg);
                pstMsg.setInt(1, idAdm); // Remetente = o próprio ADM (auto-alerta)
                pstMsg.setInt(2, idAdm); // Destinatário
                pstMsg.setString(3, "ALERTA DE SEGURANÇA - Múltiplas Tentativas");
                pstMsg.setString(4, mensagemAlerta);
                pstMsg.executeUpdate();
                pstMsg.close();
            }
            rsAdms.close();
            pstAdms.close();
            
        } catch (Exception e) {
            System.out.println("Erro ao notificar ADMs: " + e.getMessage());
        }
    }

    private void gerenciarFalhaAcesso(Integer usuarioId, String motivo, String area) {
        lblStatus.setText(motivo);
        lblStatus.setVisible(true);
        
        // Verifica se é o mesmo usuário/CPF tentando novamente
        String cpfAtual = txtCpf.getText().trim();
        if (cpfAtual.equals(ultimoCpfTentado)) {
            tentativasFalhas++;
        } else {
            tentativasFalhas = 1;
            ultimoCpfTentado = cpfAtual;
        }

        registrarLog(usuarioId, "NEGADO", motivo, area);

        if (tentativasFalhas >= MAX_TENTATIVAS) {
            String alerta = "Múltiplas tentativas de acesso falhas (" + tentativasFalhas + ") para o CPF: " + cpfAtual + " na área: " + area;
            registrarLog(usuarioId, "ALERTA_SEGURANCA", "Múltiplas tentativas falhas", area);
            notificarAdministradores(alerta);
            JOptionPane.showMessageDialog(this, "Número máximo de tentativas excedido. O administrador foi notificado.", "Alerta de Segurança", JOptionPane.WARNING_MESSAGE);
            // Resetar contador após notificar? Ou bloquear?
            // tentativasFalhas = 0; 
        }
    }


    private void validarAcesso() {
        String cpf = txtCpf.getText().trim();
        String area = (String) cbArea.getSelectedItem();

        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira o CPF.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Atualiza status das reservas antes de verificar
        ReservaDAO.atualizarStatusReservas(conexao);

        try {
            // 1. Verificar se o CPF pertence a um usuário válido e obter o ID
            String sqlUsuario = "SELECT id_usuario FROM usuario WHERE cpf = ?";
            pst = conexao.prepareStatement(sqlUsuario);
            pst.setString(1, cpf);
            rs = pst.executeQuery();

            if (!rs.next()) {
                gerenciarFalhaAcesso(null, "CPF não encontrado no sistema.", area);
                return;
            }
            int usuarioId = rs.getInt("id_usuario");

            // 2. Verificar se há uma reserva ativa para a área
            // E verifica intervalo correto usando hora_fim
            // Trata hora_fim NULL como 1 hora de duração
            // Trata situacao NULL como válida (não finalizada)
            String sqlVerifica = "SELECT id, usuario_id, situacao FROM reservas_areas_comuns " +
                                 "WHERE area = ? AND data_reserva = ? " + 
                                 "AND ? BETWEEN hora_reserva AND COALESCE(hora_fim, hora_reserva + INTERVAL '1 hour') " +
                                 "AND (situacao IS NULL OR situacao NOT IN ('FINALIZADA', 'CANCELADA', 'EXPIRADA'))";
            
            pst = conexao.prepareStatement(sqlVerifica);
            pst.setString(1, area);
            pst.setDate(2, Date.valueOf(LocalDate.now()));
            // Usa hora atual truncada para segundos para evitar problemas de precisão
            Time horaAtual = Time.valueOf(LocalTime.now().withNano(0));
            pst.setTime(3, horaAtual);
            
            System.out.println("Verificando reserva para Area: " + area + ", Hora: " + horaAtual);
            
            rs = pst.executeQuery();

            if (rs.next()) { // Cenário: Existe uma reserva ATIVA neste horário
                int idReserva = rs.getInt("id");
                int idDonoReserva = rs.getInt("usuario_id");
                String situacao = rs.getString("situacao");
                if (situacao == null) situacao = "PENDENTE";

                System.out.println("Reserva encontrada! ID: " + idReserva + ", Dono: " + idDonoReserva + ", Situacao: " + situacao);

                if (idDonoReserva == usuarioId) {
                    // Cenário 1: A reserva é do usuário e está ativa
                    // SUCESSO
                    // Atualiza status para EM_USO se estiver PENDENTE
                    if (situacao.equals("PENDENTE")) {
                        String sqlUpdate = "UPDATE reservas_areas_comuns SET situacao = 'EM_USO' WHERE id = ?";
                        PreparedStatement pstUpdate = conexao.prepareStatement(sqlUpdate);
                        pstUpdate.setInt(1, idReserva);
                        pstUpdate.executeUpdate();
                        pstUpdate.close();
                    }
                    
                    registrarLog(usuarioId, "SUCESSO", "Acesso liberado via reserva existente", area);
                    JOptionPane.showMessageDialog(this, "Acesso Liberado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    tentativasFalhas = 0; // Reset sucesso
                    dispose();

                } else {
                    // Cenário 2: Acesso negado, a área está reservada por outra pessoa (e está ativa)
                    gerenciarFalhaAcesso(usuarioId, "Acesso Negado. A área já está reservada por outro morador.", area);
                }
            } else { // Cenário 3: A área não está reservada
                 System.out.println("Nenhuma reserva encontrada. Criando nova...");
                 
                 boolean isRenovacao = verificarReservaExpirada(usuarioId, area);
                 
                 // Cria uma reserva imediata
                 criarReservaImediata(usuarioId, area, isRenovacao);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao validar acesso: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean verificarReservaExpirada(int usuarioId, String area) {
        try {
            String sql = "SELECT COUNT(*) FROM reservas_areas_comuns WHERE area = ? AND usuario_id = ? AND data_reserva = ? AND situacao = 'EXPIRADA'";
            PreparedStatement pstExp = conexao.prepareStatement(sql);
            pstExp.setString(1, area);
            pstExp.setInt(2, usuarioId);
            pstExp.setDate(3, Date.valueOf(LocalDate.now()));
            ResultSet rsExp = pstExp.executeQuery();
            if (rsExp.next()) {
                 boolean expirada = rsExp.getInt(1) > 0;
                 rsExp.close();
                 pstExp.close();
                 return expirada;
            }
            rsExp.close();
            pstExp.close();
        } catch (Exception e) {
            System.out.println("Erro ao verificar reserva expirada: " + e.getMessage());
        }
        return false;
    }

    private void criarReservaImediata(int usuarioId, String area, boolean isRenovacao) throws java.sql.SQLException {
        // Cria uma reserva imediata de 1 hora por padrão
        String sqlInsert = "INSERT INTO reservas_areas_comuns (area, data_reserva, hora_reserva, hora_fim, usuario_id, observacoes, situacao) VALUES (?, ?, ?, ?, ?, ?, 'EM_USO')";
        pst = conexao.prepareStatement(sqlInsert);
        pst.setString(1, area);
        pst.setDate(2, Date.valueOf(LocalDate.now()));
        Time agora = Time.valueOf(LocalTime.now());
        Time daquiUmaHora = Time.valueOf(LocalTime.now().plusHours(1));
        
        pst.setTime(3, agora);
        pst.setTime(4, daquiUmaHora);
        pst.setInt(5, usuarioId);
        pst.setString(6, "Reserva imediata via controle de acesso");

        int adicionado = pst.executeUpdate();
        if (adicionado > 0) {
            String msg = isRenovacao 
                ? "Reserva Expirada! Sua reserva na área foi renovada por mais 1 hora!" 
                : "Acesso Liberado! A área foi reservada para você por 1 hora.";
            
            String detalheLog = isRenovacao 
                ? "Acesso liberado e reserva renovada (anterior expirada)" 
                : "Acesso liberado e reserva imediata criada";

            registrarLog(usuarioId, "SUCESSO", detalheLog, area);
            JOptionPane.showMessageDialog(this, msg, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            tentativasFalhas = 0; // Reset sucesso
            dispose();
        } else {
            lblStatus.setText("Falha ao tentar reservar a área.");
            lblStatus.setVisible(true);
        }
    }
}
