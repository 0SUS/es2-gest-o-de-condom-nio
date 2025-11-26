package br.com.sistemaCondominio.telas;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.sql.*;
import br.com.sistemaCondominio.dal.ModuloConexao;

public class TelaRelatorios extends JInternalFrame {

    private JComboBox<String> cmbTipoRelatorio;
    private JPanel panelFiltros;
    private JTextField txtDataInicio, txtDataFim, txtUnidade;
    private JComboBox<String> cmbStatus;
    private JButton btnGerar, btnExportarPDF;
    private JLabel lblStatusRelatorio, lblDataInicio, lblDataFim, lblUnidade, lblStatus;
    private JTable tblResultado;
    private JScrollPane scrollPaneResultado;
    private Connection conexao;

    public TelaRelatorios() {
        conexao = ModuloConexao.conector();
        setTitle("Geração de Relatórios");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setBounds(100, 100, 800, 600);

        // --- Painel Principal ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getContentPane().add(mainPanel);

        // --- 1. Seção de Seleção (Norte) ---
        JPanel panelSelecao = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSelecao.add(new JLabel("Selecione o Tipo de Relatório:"));
        cmbTipoRelatorio = new JComboBox<>(new String[]{
            "Selecione...", "Unidades", "Moradores", "Taxas de Condomínio", "Reservas", "Manutenções"
        });
        panelSelecao.add(cmbTipoRelatorio);
        mainPanel.add(panelSelecao, BorderLayout.NORTH);

        // --- 2. Seção de Filtros (Centro) ---
        panelFiltros = new JPanel();
        panelFiltros.setBorder(BorderFactory.createTitledBorder(null, "Filtros", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panelFiltros.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setVisible(false);

        lblDataInicio = new JLabel("Data Início:");
        txtDataInicio = new JTextField(10);
        lblDataFim = new JLabel("Data Fim:");
        txtDataFim = new JTextField(10);
        lblUnidade = new JLabel("Nº Residência:");
        txtUnidade = new JTextField(10);
        lblStatus = new JLabel("Status:");
        cmbStatus = new JComboBox<>();

        panelFiltros.add(lblDataInicio);
        panelFiltros.add(txtDataInicio);
        panelFiltros.add(lblDataFim);
        panelFiltros.add(txtDataFim);
        panelFiltros.add(lblUnidade);
        panelFiltros.add(txtUnidade);
        panelFiltros.add(lblStatus);
        panelFiltros.add(cmbStatus);
        mainPanel.add(panelFiltros, BorderLayout.CENTER);

        // --- 3. Seção de Resultados e Ações (Sul) ---
        JPanel panelAcoesResultados = new JPanel(new BorderLayout(10, 10));
        
        // Botões
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnGerar = new JButton("Gerar Relatório");
        btnGerar.setEnabled(false);
        btnExportarPDF = new JButton("Exportar PDF");
        btnExportarPDF.setEnabled(false);
        panelBotoes.add(btnGerar);
        panelBotoes.add(btnExportarPDF);
        
        lblStatusRelatorio = new JLabel("Nenhum registro encontrado.");
        lblStatusRelatorio.setForeground(Color.RED);
        lblStatusRelatorio.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatusRelatorio.setVisible(false);
        panelBotoes.add(lblStatusRelatorio);

        panelAcoesResultados.add(panelBotoes, BorderLayout.NORTH);

        // Tabela
        tblResultado = new JTable();
        scrollPaneResultado = new JScrollPane(tblResultado);
        panelAcoesResultados.add(scrollPaneResultado, BorderLayout.CENTER);

        mainPanel.add(panelAcoesResultados, BorderLayout.SOUTH);

        // --- Lógica de Eventos ---
        cmbTipoRelatorio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                atualizarFiltrosVisiveis();
            }
        });

        btnGerar.addActionListener(e -> gerarRelatorio());
        btnExportarPDF.addActionListener(e -> exportarPDF());
    }

    private void atualizarFiltrosVisiveis() {
        String tipo = cmbTipoRelatorio.getSelectedItem().toString();
        panelFiltros.setVisible(true);
        btnGerar.setEnabled(true);

        // Oculta todos por padrão
        lblDataInicio.setVisible(false); txtDataInicio.setVisible(false);
        lblDataFim.setVisible(false); txtDataFim.setVisible(false);
        lblUnidade.setVisible(false); txtUnidade.setVisible(false);
        lblStatus.setVisible(false); cmbStatus.setVisible(false);

        switch (tipo) {
            case "Selecione...":
                panelFiltros.setVisible(false);
                btnGerar.setEnabled(false);
                break;
            case "Unidades":
            case "Reservas":
            case "Manutenções":
                // Nenhum filtro visível para estes relatórios
                break;
            case "Moradores":
                lblUnidade.setVisible(true); txtUnidade.setVisible(true);
                lblStatus.setVisible(true); cmbStatus.setVisible(true);
                cmbStatus.setModel(new DefaultComboBoxModel<>(new String[]{"Todos", "Morador", "Administrador"}));
                break;
            case "Taxas de Condomínio":
                lblDataInicio.setVisible(true); txtDataInicio.setVisible(true);
                lblDataFim.setVisible(true); txtDataFim.setVisible(true);
                lblUnidade.setVisible(true); txtUnidade.setVisible(true);
                lblStatus.setVisible(true); cmbStatus.setVisible(true);
                cmbStatus.setModel(new DefaultComboBoxModel<>(new String[]{"Todas", "Paga", "Pendente"}));
                break;
        }
        panelFiltros.revalidate();
        panelFiltros.repaint();
    }

    private void gerarRelatorio() {
        String tipoRelatorio = cmbTipoRelatorio.getSelectedItem().toString();
        if (conexao == null) {
            JOptionPane.showMessageDialog(this, "Falha na conexão com o banco de dados.");
            return;
        }

        DefaultTableModel model = new DefaultTableModel();
        StringBuilder sql = new StringBuilder();
        java.util.List<Object> params = new java.util.ArrayList<>();

        // Constrói a consulta SQL baseada no tipo de relatório
        // Constrói a consulta SQL baseada no tipo de relatório
        switch (tipoRelatorio) {
            case "Unidades":
                model.setColumnIdentifiers(new String[]{"ID", "Nome", "CPF", "Telefone", "Username", "Nº Residência"});
                // Mostra todos os registros da tabela usuario
                sql.append("SELECT id_usuario, nome, cpf, telefone, username, numero FROM usuario");
                break;
            case "Moradores":
                model.setColumnIdentifiers(new String[]{"ID", "Nome", "CPF", "Telefone", "Username", "Nº Residência"});
                // Busca na tabela usuario filtrando pelo campo numero
                sql.append("SELECT id_usuario, nome, cpf, telefone, username, numero FROM usuario WHERE 1=1 ");
                
                if (txtUnidade.getText() != null && !txtUnidade.getText().trim().isEmpty()) {
                    sql.append("AND numero = ? ");
                    params.add(txtUnidade.getText().trim());
                }
                
                if (cmbStatus.getSelectedItem() != null && !cmbStatus.getSelectedItem().toString().equals("Todos")) {
                    sql.append("AND perfil = ? ");
                    params.add(cmbStatus.getSelectedItem().toString());
                }
                break;
            case "Taxas de Condomínio":
                model.setColumnIdentifiers(new String[]{"ID Taxa", "Nº Residência", "Valor", "Vencimento", "Status"});
                sql.append("SELECT t.id_taxa, r.numero, t.valor, t.data_vencimento, t.status FROM taxas t JOIN residencia r ON t.id_residencia = r.id_residencia WHERE 1=1 ");
                if (txtUnidade.getText() != null && !txtUnidade.getText().trim().isEmpty()) {
                    sql.append("AND r.numero = ? ");
                    params.add(txtUnidade.getText().trim());
                }
                if (cmbStatus.getSelectedItem() != null && !cmbStatus.getSelectedItem().toString().equals("Todas")) {
                    sql.append("AND t.status = ? ");
                    params.add(cmbStatus.getSelectedItem().toString());
                }
                break;
            case "Reservas":
                model.setColumnIdentifiers(new String[]{"ID", "Área", "Data", "Hora", "Morador"});
                sql.append("SELECT r.id, r.area, r.data_reserva, r.hora_reserva, u.nome FROM reservas_areas_comuns r JOIN usuario u ON r.usuario_id = u.id_usuario");
                break;
            case "Manutenções":
                model.setColumnIdentifiers(new String[]{"ID", "Área", "Tipo Problema", "Descrição", "Status"});
                sql.append("SELECT id, area, tipo_problema, descricao, status FROM manutencoes_areas_comuns");
                break;
            default:
                JOptionPane.showMessageDialog(this, "Tipo de relatório não suportado ainda.");
                return;
        }

        try (PreparedStatement pst = conexao.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                java.util.Vector<Object> row = new java.util.Vector<>();
                for (int i = 1; i <= model.getColumnCount(); i++) {
                    row.add(rs.getObject(i));
                }
                model.addRow(row);
            }

            if (model.getRowCount() > 0) {
                tblResultado.setModel(model);
                btnExportarPDF.setEnabled(true);
                lblStatusRelatorio.setVisible(false);
            } else {
                lblStatusRelatorio.setText("Nenhum registro encontrado para os filtros aplicados.");
                lblStatusRelatorio.setVisible(true);
                btnExportarPDF.setEnabled(false);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + ex.getMessage());
        }
    }

    private void exportarPDF() {
        // TODO: Implementar lógica de exportação para PDF (ex: iText ou Apache PDFBox).
        JOptionPane.showMessageDialog(this, "Relatório exportado com sucesso! (Simulação)", "Exportação PDF", JOptionPane.INFORMATION_MESSAGE);
    }
}
