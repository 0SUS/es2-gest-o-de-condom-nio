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
    private JButton btnListar, btnGerarRelatorio, btnExportarPDF;
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
        btnListar = new JButton("Listar");
        btnListar.setEnabled(false);
        
        btnGerarRelatorio = new JButton("Gerar Relatório");
        btnGerarRelatorio.setEnabled(false);
        
        btnExportarPDF = new JButton("Exportar PDF");
        btnExportarPDF.setEnabled(false);
        
        panelBotoes.add(btnListar);
        panelBotoes.add(btnGerarRelatorio);
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

        btnListar.addActionListener(e -> listarRegistros());
        btnGerarRelatorio.addActionListener(e -> gerarRelatorioTexto());
        btnExportarPDF.addActionListener(e -> exportarPDF());
    }

    private void atualizarFiltrosVisiveis() {
        String tipo = cmbTipoRelatorio.getSelectedItem().toString();
        panelFiltros.setVisible(true);
        btnListar.setEnabled(true);

        // Oculta todos por padrão
        lblDataInicio.setVisible(false); txtDataInicio.setVisible(false);
        lblDataFim.setVisible(false); txtDataFim.setVisible(false);
        lblUnidade.setVisible(false); txtUnidade.setVisible(false);
        lblStatus.setVisible(false); cmbStatus.setVisible(false);

        switch (tipo) {
            case "Selecione...":
                panelFiltros.setVisible(false);
                btnListar.setEnabled(false);
                break;
            case "Unidades":
                lblUnidade.setVisible(true); txtUnidade.setVisible(true);
                // Apenas filtro por unidade, sem status
                break;
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

    private void listarRegistros() {
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
                model.setColumnIdentifiers(new String[]{"ID", "Número", "Rua", "Área (m²)", "Proprietário", "Telefone"});
                sql.append("SELECT id_residencia, numero, rua, area, nome_proprietario, telefone FROM residencia WHERE 1=1 ");
                
                if (txtUnidade.getText() != null && !txtUnidade.getText().trim().isEmpty()) {
                    sql.append("AND numero = ? ");
                    params.add(txtUnidade.getText().trim());
                }
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
                sql.append("SELECT id, unidade, valor, data_vencimento, status_pagamento FROM taxas WHERE 1=1 ");
                if (txtUnidade.getText() != null && !txtUnidade.getText().trim().isEmpty()) {
                    sql.append("AND unidade = ? ");
                    params.add(txtUnidade.getText().trim());
                }
                if (cmbStatus.getSelectedItem() != null && !cmbStatus.getSelectedItem().toString().equals("Todas")) {
                    sql.append("AND status_pagamento = ? ");
                    // Ajuste para mapear o status do combo (que pode ser diferente do banco) se necessário, 
                    // mas assumindo que o combo usa os mesmos valores ou faremos a conversão simples
                    String status = cmbStatus.getSelectedItem().toString();
                    if(status.equals("Pendente")) status = "Pendente"; // Exemplo, se precisar ajustar
                    if(status.equals("Paga")) status = "Pago";
                    params.add(status);
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

            // Atualiza a tabela sempre, limpando se não houver resultados
            tblResultado.setModel(model);

            if (model.getRowCount() > 0) {
                btnGerarRelatorio.setEnabled(true);
                btnExportarPDF.setEnabled(true);
                lblStatusRelatorio.setVisible(false);
            } else {
                lblStatusRelatorio.setText("Nenhum registro encontrado");
                lblStatusRelatorio.setVisible(true);
                btnGerarRelatorio.setEnabled(false);
                btnExportarPDF.setEnabled(false);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao listar registros: " + ex.getMessage());
        }
    }

    private void gerarRelatorioTexto() {
        if (tblResultado.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Não há dados listados para gerar relatório.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("=== RELATÓRIO DE ").append(cmbTipoRelatorio.getSelectedItem().toString().toUpperCase()).append(" ===\n\n");

        int colCount = tblResultado.getColumnCount();
        int rowCount = tblResultado.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            relatorio.append("Registro #").append(i + 1).append(":\n");
            for (int j = 0; j < colCount; j++) {
                String colName = tblResultado.getColumnName(j);
                Object val = tblResultado.getValueAt(i, j);
                relatorio.append(colName).append(": ").append(val != null ? val.toString() : "").append("\n");
            }
            relatorio.append("----------------------------------------\n");
        }

        relatorio.append("\n=== RESUMO ===\n");
        relatorio.append("Total de Registros: ").append(rowCount).append("\n");

        JTextArea textArea = new JTextArea(relatorio.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Relatório Gerado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportarPDF() {
        JOptionPane.showMessageDialog(this, 
            "Para gerar PDF real, é necessário adicionar bibliotecas como iText ou JasperReports ao projeto.\n" +
            "Atualmente, utilize o botão 'Gerar Relatório' para visualizar os dados em formato texto.", 
            "Funcionalidade de PDF", JOptionPane.INFORMATION_MESSAGE);
    }
}
